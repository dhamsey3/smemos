import type { Request, Response } from 'express';
import Joi from 'joi';

import { db } from '../../db/knex.js';
import { validate } from '../../middleware/validate.js';
import { pushSyncEvent, pullEvents } from './service.js';

const pullSchema = Joi.object({
  lastVersion: Joi.number().integer().min(0).required(),
});

const pushSchema = Joi.object({
  events: Joi.array()
    .items(
      Joi.object({
        type: Joi.string().required(),
        payload: Joi.object().required(),
        operation: Joi.string().valid('CREATE', 'UPDATE', 'DELETE').required(),
      }),
    )
    .required(),
});

export const pullSyncHandler = [
  validate(pullSchema),
  async (req: Request, res: Response) => {
    const events = await pullEvents(req.user!.id, req.body.lastVersion);
    res.json({ data: events });
  },
];

export const pushSyncHandler = [
  validate(pushSchema),
  async (req: Request, res: Response) => {
    for (const event of req.body.events as any[]) {
      await pushSyncEvent(req.user!.id, event.type, event.payload.id ?? 'local', event.payload);
    }
    res.json({ data: { synced: req.body.events.length } });
  },
];

export const ingestOfflineSales = async (merchantId: string, sales: any[]) => {
  await db.transaction(async (trx) => {
    for (const sale of sales) {
      await trx('sales')
        .insert({
          id: sale.id,
          merchant_id: merchantId,
          total_amount_cents: sale.totalAmountCents,
          status: sale.status ?? 'PENDING',
        })
        .onConflict('id')
        .merge();
    }
  });
};

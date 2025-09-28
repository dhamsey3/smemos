import type { Request, Response } from 'express';
import Joi from 'joi';

import { validate } from '../../middleware/validate.js';
import { initiatePayment, handleWebhook } from './service.js';

const initiateSchema = Joi.object({
  saleId: Joi.string().required(),
  channel: Joi.string().valid('qr', 'transfer').required(),
});

export const initiatePaymentHandler = [
  validate(initiateSchema),
  async (req: Request, res: Response) => {
    const payload = await initiatePayment(req.user!.id, req.body);
    res.status(201).json({ data: payload });
  },
];

export const webhookHandler = async (req: Request, res: Response) => {
  await handleWebhook(req.headers['x-koloos-signature'] as string | undefined, req.bodyRaw, req.body);
  res.status(200).json({ received: true });
};

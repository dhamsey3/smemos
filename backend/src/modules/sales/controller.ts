import type { Request, Response } from 'express';
import Joi from 'joi';

import { validate } from '../../middleware/validate.js';
import { getSales, recordSale } from './service.js';

const saleSchema = Joi.object({
  items: Joi.array()
    .items(
      Joi.object({
        productId: Joi.string().required(),
        quantity: Joi.number().integer().min(1).required(),
      }),
    )
    .min(1)
    .required(),
});

export const listSalesHandler = async (req: Request, res: Response) => {
  const sales = await getSales(req.user!.id);
  res.json({ data: sales });
};

export const recordSaleHandler = [
  validate(saleSchema),
  async (req: Request, res: Response) => {
    const sale = await recordSale(req.user!.id, req.body.items);
    res.status(201).json({ data: sale });
  },
];

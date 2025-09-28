import type { Request, Response } from 'express';
import Joi from 'joi';

import { validate } from '../../middleware/validate.js';
import { createProduct, deleteProductById, getProducts, updateProductDetails } from './service.js';

const createSchema = Joi.object({
  name: Joi.string().required(),
  priceNaira: Joi.number().positive().required(),
  quantity: Joi.number().integer().min(0).required(),
});

const updateSchema = Joi.object({
  name: Joi.string(),
  priceNaira: Joi.number().positive(),
  quantity: Joi.number().integer().min(0),
}).min(1);

export const listProductsHandler = async (req: Request, res: Response) => {
  const items = await getProducts(req.user!.id);
  res.json({ data: items });
};

export const createProductHandler = [
  validate(createSchema),
  async (req: Request, res: Response) => {
    const product = await createProduct(req.user!.id, req.body);
    res.status(201).json({ data: product });
  },
];

export const updateProductHandler = [
  validate(updateSchema),
  async (req: Request, res: Response) => {
    const product = await updateProductDetails(req.user!.id, req.params.id, req.body);
    res.json({ data: product });
  },
];

export const deleteProductHandler = async (req: Request, res: Response) => {
  await deleteProductById(req.user!.id, req.params.id);
  res.status(204).send();
};

import type { Database } from '../../db/knex.js';
import type { Product } from './types.js';

export const listProducts = (db: Database, merchantId: string) =>
  db<Product>('products').where({ merchant_id: merchantId }).orderBy('updated_at', 'desc');

export const createProduct = (db: Database, merchantId: string, payload: { name: string; price_cents: number; quantity: number; }) =>
  db<Product>('products')
    .insert({ ...payload, merchant_id: merchantId })
    .returning('*');

export const updateProduct = (
  db: Database,
  merchantId: string,
  productId: string,
  payload: Partial<Pick<Product, 'name' | 'price_cents' | 'quantity'>>,
) =>
  db<Product>('products')
    .where({ id: productId, merchant_id: merchantId })
    .update({ ...payload, updated_at: db.fn.now() })
    .returning('*');

export const deleteProduct = (db: Database, merchantId: string, productId: string) =>
  db<Product>('products').where({ id: productId, merchant_id: merchantId }).del();

export const getProductById = (db: Database, merchantId: string, productId: string) =>
  db<Product>('products').where({ id: productId, merchant_id: merchantId }).first();

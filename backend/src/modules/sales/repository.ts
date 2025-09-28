import type { Database } from '../../db/knex.js';
import type { Sale, SaleItem } from './types.js';

export const createSale = (db: Database, payload: Partial<Sale>) =>
  db<Sale>('sales').insert(payload).returning('*');

export const createSaleItems = (db: Database, items: Partial<SaleItem>[]) =>
  db<SaleItem>('sale_items').insert(items).returning('*');

export const listSales = (db: Database, merchantId: string) =>
  db<Sale>('sales').where({ merchant_id: merchantId }).orderBy('created_at', 'desc');

export const getSaleById = (db: Database, merchantId: string, saleId: string) =>
  db<Sale>('sales').where({ id: saleId, merchant_id: merchantId }).first();

export const updateSaleStatus = (
  db: Database,
  saleId: string,
  status: string,
  paymentReference?: string,
) =>
  db<Sale>('sales')
    .where({ id: saleId })
    .update({ status, payment_reference: paymentReference, updated_at: db.fn.now() })
    .returning('*');

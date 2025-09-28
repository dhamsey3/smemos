import type { Database } from '../../db/knex.js';

export interface MerchantRecord {
  id: string;
  phone: string;
  name: string;
  password_hash: string;
}

export const findMerchantByPhone = async (db: Database, phone: string) =>
  db<MerchantRecord>('merchants').where({ phone }).first();

export const createMerchant = async (
  db: Database,
  payload: Pick<MerchantRecord, 'phone' | 'name' | 'password_hash'>,
) =>
  db<MerchantRecord>('merchants').insert(payload).returning('*');

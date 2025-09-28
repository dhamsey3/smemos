import type { Knex } from 'knex';

import { db } from '../../db/knex.js';

export interface SyncEventPayload {
  id: number;
  entity_type: string;
  entity_id: string;
  payload: unknown;
  version: number;
  created_at: string;
}

export const pushSyncEvent = async (
  merchantId: string,
  entityType: string,
  entityId: string,
  payload: unknown,
  trx?: Knex,
) => {
  const executor = trx ?? db;
  await executor('sync_events').insert({
    merchant_id: merchantId,
    entity_type: entityType,
    entity_id: entityId,
    payload: JSON.stringify(payload),
    version: executor.raw('extract(epoch from now())::bigint'),
  });
};

export const pullEvents = async (merchantId: string, lastVersion: number) =>
  db<SyncEventPayload>('sync_events')
    .where('merchant_id', merchantId)
    .andWhere('id', '>', lastVersion)
    .orderBy('id', 'asc')
    .limit(200);

import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import { beforeEach, describe, it, mock } from 'node:test';

import type { Database } from '../../db/knex.js';
import { handleWebhook } from './service.js';
import type { pushSyncEvent as PushSyncEvent } from '../sync/service.js';

type SaleRecord = {
  id: string;
  merchant_id: string;
  status: string;
  payment_reference: string | null;
  total_amount_cents: number;
  updated_at: Date | null;
};

type FakeDatabase = Database & {
  sales: SaleRecord[];
};

const createFakeDatabase = (
  initialSales: SaleRecord[],
  options: { supportReturning?: boolean } = {},
): FakeDatabase => {
  const sales = initialSales;
  const { supportReturning = true } = options;

  const database = ((table: string) => {
    if (table !== 'sales') {
      throw new Error(`Unexpected table: ${table}`);
    }

    const build = (filters: Partial<SaleRecord> = {}) => {
      const matches = (record: SaleRecord) =>
        Object.entries(filters).every(([key, value]) => record[key as keyof SaleRecord] === value);

      return {
        where(nextFilters: Partial<SaleRecord>) {
          return build({ ...filters, ...nextFilters });
        },
        async first() {
          return sales.find(matches);
        },
        update(data: Partial<SaleRecord>) {
          const updated: SaleRecord[] = [];

          for (const record of sales) {
            if (matches(record)) {
              Object.assign(record, data);
              updated.push({ ...record });
            }
          }

          return {
            async returning(..._columns: unknown[]) {
              if (!supportReturning) {
                throw new Error('RETURNING not supported');
              }

              return updated.map((entry) => ({ ...entry }));
            },
          };
        },
      };
    };

    return build();
  }) as unknown as Database;

  (database as unknown as { fn: { now: () => Date } }).fn = {
    now: () => new Date(),
  };

  return Object.assign(database, { sales });
};

describe('handleWebhook', () => {
  let database: FakeDatabase;

  beforeEach(() => {
    database = createFakeDatabase([
      {
        id: 'sale-1',
        merchant_id: 'merchant-1',
        status: 'PENDING',
        payment_reference: 'PSP_ref',
        total_amount_cents: 5000,
        updated_at: null,
      },
    ]);
  });

  it('updates the sale and pushes a sync event', async () => {
    const pushSync = mock.fn(async () => {});

    const event = { reference: 'PSP_ref', status: 'success' as const };
    const rawBody = JSON.stringify(event);
    const signature = crypto.createHash('sha256').update(rawBody).digest('hex');

    await handleWebhook(signature, rawBody, event, {
      database,
      pushSync: pushSync as unknown as PushSyncEvent,
    });

    assert.equal(database.sales[0]?.status, 'PAID');
    assert.equal(pushSync.mock.calls.length, 1);

    const [merchantId, entity, id] = pushSync.mock.calls[0]?.arguments ?? [];
    assert.equal(merchantId, 'merchant-1');
    assert.equal(entity, 'sale');
    assert.equal(id, 'sale-1');
  });

  it('processes the webhook when the database does not support RETURNING', async () => {
    const pushSync = mock.fn(async () => {});
    const databaseWithoutReturning = createFakeDatabase(
      database.sales.map((record) => ({ ...record })),
      {
        supportReturning: false,
      },
    );

    const event = { reference: 'PSP_ref', status: 'success' as const };
    const rawBody = JSON.stringify(event);
    const signature = crypto.createHash('sha256').update(rawBody).digest('hex');

    await handleWebhook(signature, rawBody, event, {
      database: databaseWithoutReturning,
      pushSync: pushSync as unknown as PushSyncEvent,
    });

    assert.equal(databaseWithoutReturning.sales[0]?.status, 'PAID');
    assert.equal(pushSync.mock.calls.length, 1);
  });

  it('returns early when the sale does not exist', async () => {
    database.sales[0]!.payment_reference = 'other';
    const pushSync = mock.fn(async () => {});

    const event = { reference: 'missing', status: 'failed' as const };
    const rawBody = JSON.stringify(event);
    const signature = crypto.createHash('sha256').update(rawBody).digest('hex');

    await handleWebhook(signature, rawBody, event, {
      database,
      pushSync: pushSync as unknown as PushSyncEvent,
    });

    assert.equal(pushSync.mock.calls.length, 0);
    assert.equal(database.sales[0]?.status, 'PENDING');
  });
});

import assert from 'node:assert/strict';
import { beforeEach, describe, it } from 'node:test';

import { AppError } from '../../middleware/error.js';
import { db } from '../../db/knex.js';
import { verifyOtp } from './service.js';

type MerchantRecord = {
  id: string;
  phone: string;
  name: string;
  password_hash: string;
};

type TestDatabase = typeof db & { __store: MerchantRecord[] };

const testDb = db as TestDatabase;

describe('verifyOtp', () => {
  const phone = '+1234567890';
  const name = 'Test Merchant';
  const validOtp = '123456';

  beforeEach(() => {
    testDb.__store.length = 0;
  });

  it('throws an AppError when the OTP is invalid', async () => {
    await assert.rejects(
      verifyOtp(phone, name, '000000'),
      (error) => {
        assert.ok(error instanceof AppError);
        assert.equal(error.message, 'Invalid OTP');
        assert.equal(error.statusCode, 401);
        return true;
      },
    );
  });

  it('returns tokens and merchant claims for a valid OTP', async () => {
    const result = await verifyOtp(phone, name, validOtp);

    assert.equal(result.merchant.phone, phone);
    assert.equal(result.merchant.name, name);
    assert.ok(result.tokens.accessToken.length > 0);
    assert.equal(testDb.__store.length, 1);
  });

  it('reuses an existing merchant record', async () => {
    const existing = {
      id: 'merchant-1',
      phone,
      name,
      password_hash: 'hash',
    } satisfies MerchantRecord;

    testDb.__store.push({ ...existing });

    const result = await verifyOtp(phone, name, validOtp);

    assert.equal(testDb.__store.length, 1);
    assert.deepEqual(result.merchant, {
      id: existing.id,
      phone: existing.phone,
      name: existing.name,
    });
  });
});

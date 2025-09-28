import assert from 'node:assert/strict';
import { test } from 'node:test';

import { AppError } from '../../dist/middleware/error.js';
import { db } from '../../dist/db/knex.js';
import { verifyOtp } from '../../dist/modules/auth/service.js';

const VALID_PHONE = '+1234567890';
const VALID_NAME = 'Test Merchant';
const VALID_OTP = '123456';

const merchant = {
  id: 'merchant-1',
  phone: VALID_PHONE,
  name: VALID_NAME,
  password_hash: 'hash',
};

test('verifyOtp rejects incorrect OTPs', async () => {
  db.__store.length = 0;

  await assert.rejects(verifyOtp(VALID_PHONE, VALID_NAME, '000000'), (error) => {
    assert(error instanceof AppError);
    assert.equal(error.message, 'Invalid OTP');
    return true;
  });
});

test('verifyOtp accepts correct OTPs', async () => {
  db.__store.length = 0;
  db.__store.push({ ...merchant });

  const result = await verifyOtp(VALID_PHONE, VALID_NAME, VALID_OTP);

  assert.deepEqual(result.merchant, {
    id: merchant.id,
    phone: merchant.phone,
    name: merchant.name,
  });
  assert.ok(result.tokens.accessToken);
  assert.equal(db.__store.length, 1);
});

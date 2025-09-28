import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';

import { appConfig } from '../../config/env.js';
import { db } from '../../db/knex.js';
import type { MerchantClaims, AuthTokens } from './types.js';
import { AppError } from '../../middleware/error.js';
import { createMerchant, findMerchantByPhone } from './repository.js';

const MOCK_OTP = '123456';

const ensureValidOtp = (submitted: string) => {
  if (submitted !== MOCK_OTP) {
    throw new AppError('Invalid OTP', 401);
  }
};

export const requestOtp = async (phone: string) => ({ otp: MOCK_OTP, phone });

export const verifyOtp = async (
  phone: string,
  name: string,
  otp: string,
): Promise<{ merchant: MerchantClaims; tokens: AuthTokens; }> => {
  ensureValidOtp(otp);

  let merchant = await findMerchantByPhone(db, phone);

  if (!merchant) {
    const [created] = await createMerchant(db, {
      phone,
      name,
      password_hash: await bcrypt.hash(phone.slice(-4).padStart(6, '0'), 10),
    });
    merchant = created;
  }

  const claims: MerchantClaims = { id: merchant.id, phone: merchant.phone, name: merchant.name };

  return {
    merchant: claims,
    tokens: buildTokens(claims),
  };
};

export const passwordLogin = async (phone: string, password: string) => {
  const merchant = await findMerchantByPhone(db, phone);
  if (!merchant) {
    return null;
  }
  const valid = await bcrypt.compare(password, merchant.password_hash);
  if (!valid) {
    return null;
  }

  const claims: MerchantClaims = { id: merchant.id, phone: merchant.phone, name: merchant.name };
  return {
    merchant: claims,
    tokens: buildTokens(claims),
  };
};

const buildTokens = (claims: MerchantClaims): AuthTokens => ({
  accessToken: jwt.sign(claims, appConfig.jwtSecret, { expiresIn: '12h' }),
});

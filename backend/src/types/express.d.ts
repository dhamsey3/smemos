import type { MerchantClaims } from '../modules/auth/types';

declare global {
  namespace Express {
    interface Request {
      user?: MerchantClaims;
      bodyRaw?: string;
    }
  }
}

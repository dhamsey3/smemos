import 'dotenv/config';

export const appConfig = {
  port: parseInt(process.env.PORT || '4000', 10),
  nodeEnv: process.env.NODE_ENV || 'development',
  jwtSecret: process.env.JWT_SECRET || 'koloos-dev-secret',
};

export const databaseConfig = {
  url: process.env.DATABASE_URL || 'postgres://postgres:postgres@localhost:5432/koloos',
};

export const paymentConfig = {
  paystack: {
    secretKey: process.env.PAYSTACK_SECRET_KEY || 'paystack-secret',
  },
  flutterwave: {
    secretKey: process.env.FLUTTERWAVE_SECRET_KEY || 'flutterwave-secret',
  },
};

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS merchants (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  phone VARCHAR(15) UNIQUE NOT NULL,
  name VARCHAR(120) NOT NULL,
  password_hash TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS products (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  merchant_id UUID REFERENCES merchants(id) ON DELETE CASCADE,
  name VARCHAR(120) NOT NULL,
  price_cents INTEGER NOT NULL,
  quantity INTEGER NOT NULL DEFAULT 0,
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sales (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  merchant_id UUID REFERENCES merchants(id) ON DELETE CASCADE,
  total_amount_cents INTEGER NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  payment_reference VARCHAR(60),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sale_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sale_id UUID REFERENCES sales(id) ON DELETE CASCADE,
  product_id UUID REFERENCES products(id),
  unit_price_cents INTEGER NOT NULL,
  quantity INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sale_id UUID REFERENCES sales(id) ON DELETE CASCADE,
  provider VARCHAR(20) NOT NULL,
  reference VARCHAR(100) UNIQUE NOT NULL,
  amount_cents INTEGER NOT NULL,
  channel VARCHAR(30),
  status VARCHAR(20) NOT NULL,
  raw_payload JSONB,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sync_events (
  id BIGSERIAL PRIMARY KEY,
  merchant_id UUID REFERENCES merchants(id),
  entity_type VARCHAR(30) NOT NULL,
  entity_id UUID NOT NULL,
  payload JSONB NOT NULL,
  version BIGINT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

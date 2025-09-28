ALTER TABLE sales
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT NOW();

UPDATE sales SET updated_at = COALESCE(updated_at, NOW());

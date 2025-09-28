import knex, { Knex } from 'knex';

import { databaseConfig } from '../config/env.js';

export const createConnection = (overrides?: Knex.Config): Knex =>
  knex({
    client: 'pg',
    connection: databaseConfig.url,
    pool: { min: 2, max: 10 },
    ...overrides,
  });

export type Database = Knex;

export const db = createConnection();

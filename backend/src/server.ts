import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import swaggerUi from 'swagger-ui-express';

import { appConfig } from './config/env.js';
import { logger } from './config/logger.js';
import { errorHandler } from './middleware/error.js';
import routes from './routes/index.js';
import { swaggerSpec } from './swagger.js';

const app = express();

app.use(morgan('combined'));
app.use(helmet());
app.use(cors());
app.use(
  express.json({
    verify: (req, _res, buf) => {
      req.bodyRaw = buf.toString();
    },
  }),
);

app.get('/health', (_req, res) => res.json({ status: 'ok' }));
app.use('/api/v1', routes);
app.use('/docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));
app.use(errorHandler);

app.listen(appConfig.port, () => {
  logger.info(`KoloOS API running on port ${appConfig.port}`);
});

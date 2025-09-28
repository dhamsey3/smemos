import swaggerJSDoc from 'swagger-jsdoc';

export const swaggerSpec = swaggerJSDoc({
  definition: {
    openapi: '3.0.1',
    info: {
      title: 'KoloOS API',
      version: '1.0.0',
    },
    components: {
      securitySchemes: {
        bearerAuth: {
          type: 'http',
          scheme: 'bearer',
        },
      },
    },
    security: [{ bearerAuth: [] }],
  },
  apis: ['src/routes/*.ts'],
});

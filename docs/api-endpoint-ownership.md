# Klippr API Endpoint Ownership

Fuente: Swagger `https://klippr-backend-production.up.railway.app/swagger/v1/swagger.json`.

## Klippr Consumer

- `POST /api/Authentication/sign-up/consumer`
- `/api/profiles/consumer*`
- `/api/v1/Preferences*`
- `/api/v1/Favorites*`
- `GET /api/promotions`
- `GET /api/promotions/active`
- `POST /api/redemptions`
- `GET /api/redemptions/{redemptionId}`
- `GET /api/redemptions/consumers/{consumerId}`
- `GET /api/reviews`
- `POST /api/reviews`
- `GET /api/reviews/can-review`
- `POST /api/reviews/{reviewId}/like`
- `GET /api/reviews/{reviewId}/comments`
- `POST /api/reviews/{reviewId}/comments`

## Compartidos

- `POST /api/Authentication/sign-in`
- `POST /api/Authentication/forgot-password`
- `PUT /api/Authentication/reset-password`
- `GET /api/promotions/{promotionId}`
- `GET /api/Users/{userId}`

## Fuera del cliente Consumer

- Endpoints Business: `/api/profiles/business*`, `POST /api/Authentication/sign-up/business`, mutaciones de `/api/promotions`, `/api/redemptions/businesses/*`, `/api/redemptions/tokens/*/confirm`, `/api/analytics/*`.
- Endpoints Admin: `/api/admin/**`, `POST /api/verification/approve`, `POST /api/verification/reject`.
- Reviews legacy: `/api/v1/Reviews*`; el contrato canónico para Consumer es `/api/reviews*`.

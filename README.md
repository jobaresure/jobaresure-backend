# Jobaresure — Job Application Backend

REST API for the Jobaresure employment platform: authentication, organizations & employers,
job postings, job-seeker profiles, applications, and identity/company verification.

Built with **Spring Boot 4** (Java 17), **Spring Security** (JWT/RBAC), **Spring Data JPA**,
and **MySQL**.

## Documentation

- 📖 **[API Documentation](docs/API.md)** — all endpoints with descriptions, request/response
  shapes, headers, auth/role requirements, errors, rate limiting, and the enum reference.
- 🧪 **[Postman collection & environment](postman/)** — import to try the API quickly.
- 🗄️ **[Database schema](src/main/resources/db/schema.sql)** — canonical SQL schema.
- 🔎 **Swagger UI** (when running): `http://localhost:8080/swagger-ui.html`
  · OpenAPI spec: `http://localhost:8080/v3/api-docs`

## Setup

### Prerequisites
- **JDK 17** (the Gradle toolchain targets Java 17)
- **MySQL 8** — a local instance reachable at `jdbc:mysql://localhost:3306`
- **Git** (to clone) — the bundled `./gradlew` wrapper handles Gradle, no separate install needed

### 1. Clone
```bash
git clone https://github.com/Srivignesh-Professional/JobAreSure-Backend.git
cd JobAreSure-Backend
```

### 2. Create the database
Apply the canonical schema (creates the `job_dev` database and all tables):
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```
The schema is owned by `schema.sql`; Hibernate runs in `validate` mode and never alters tables.

### 3. Configure
Defaults live in `src/main/resources/application.properties`. Every value can be overridden with
an environment variable — set only what differs from the defaults for your machine.

| Variable | Default | Purpose |
|---|---|---|
| `SERVER_PORT` | `8080` | HTTP port the API listens on |
| `DB_URL` | `jdbc:mysql://localhost:3306/job_dev?...` | JDBC connection string |
| `DB_USERNAME` | `root` | Database user |
| `DB_PASSWORD` | `password` | Database password |
| `JPA_DDL_AUTO` | `validate` | Hibernate schema mode (`validate` / `none` / `update`) |
| `JPA_SHOW_SQL` | `true` | Log generated SQL |
| `JWT_SECRET` | _dev placeholder_ | **Set a strong ≥32-char secret in production** |
| `JWT_ACCESS_EXPIRATION_MS` | `900000` (15 min) | Access-token lifetime |
| `JWT_REFRESH_EXPIRATION_MS` | `1209600000` (14 days) | Refresh-token lifetime |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,...` | Comma-separated allowed origins |
| `SMS_PROVIDER` | `mock` | `mock` logs OTP to console; `msg91` sends real SMS |
| `EMAIL_PROVIDER` | `mock` | `mock` logs codes to console; `smtp` sends real email |

> In `mock` mode (the default), OTP and email-verification codes are printed to the server
> console instead of being sent — handy for local testing.

Example (Bash) overriding the DB password before running:
```bash
DB_PASSWORD=mysecret ./gradlew bootRun
```

### 4. Run
```bash
./gradlew bootRun
```
The API starts on `http://localhost:8080`. Verify with Swagger UI at
`http://localhost:8080/swagger-ui.html`.

### 5. Test
```bash
./gradlew test
```

## Project layout

```
src/main/java/com/jobaresure/
├── controller/    REST controllers (API surface)
├── service/       Business logic
├── repository/    Spring Data JPA repositories
├── entity/        JPA entities
├── dto/           Request/response payloads
├── enums/         Domain enums
├── security/      JWT, filters, rate limiting
├── config/        Security, OpenAPI, properties
└── exception/     Global error handling

src/main/resources/
├── application.properties
└── db/schema.sql  Canonical database schema
```

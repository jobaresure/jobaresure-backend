# Jobaresure Backend — Setup & Frontend Integration

Production-grade Spring Boot backend for the Jobaresure employment platform
(AI-powered hiring for low/medium-skilled workers). This document covers Phase 1
(foundation) and the roadmap for the remaining phases.

- **Stack:** Spring Boot 4.0.x · JDK 17 · MySQL · Spring Security + JWT · springdoc OpenAPI
- **Base URL:** `http://localhost:8080`
- **API prefix:** `/api/v1`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

> **Note on the brief:** Zod / Helmet / express-rate-limit are Node.js libraries and
> don't apply to a Spring Boot service. The Spring-native equivalents used here are:
> **Jakarta Bean Validation** (`@Valid`, `@NotBlank`, `@Email`…) for request validation,
> **Spring Security HTTP headers + CORS** for the "Helmet" hardening, and a built-in
> **token-bucket `RateLimitFilter`** for rate limiting.

---

## 1. What's implemented in Phase 1

| Area | Status |
|------|--------|
| Project/security setup (Spring Security 7, BCrypt, CORS, secure headers) | ✅ |
| `User` entity + RBAC roles: `JOB_SEEKER`, `COMPANY`, `ADMIN` | ✅ |
| JWT access + refresh tokens (stateless), refresh rotation | ✅ |
| Registration (job seeker, company) + login + `/me` | ✅ |
| Mobile OTP architecture (send/verify, hashed codes, cooldown, attempt limits) | ✅ |
| SMS provider abstraction: `mock` (default) + `msg91` | ✅ |
| Email verification (send code / verify) + email provider abstraction (`mock` default) | ✅ |
| Password reset by email (forgot → emailed code → reset) | ✅ |
| Rate limiting on `/auth/**` and `/otp/**` | ✅ |
| Global JSON error handling + Swagger w/ Bearer auth | ✅ |

Existing modules already in the codebase (Organizations, Employers, Jobs,
Applications, Education, Work Experience) are now protected by the new security layer.

## Phase 2 — Profiles & Verification (implemented)

| Area | Status |
|------|--------|
| Job seeker self-service profile (get/update, partial) + profile-completion % | ✅ |
| Work experience CRUD, education CRUD | ✅ |
| Skills catalog (search/create) + applicant skills (add by id or name, remove) | ✅ |
| Company self-service profile (organization + employer) | ✅ |
| Identity verification workflow (job seeker submits → admin reviews) | ✅ |
| Company verification workflow (company admin submits → admin reviews → org verified) | ✅ |
| Admin verification queue (list by status, approve/reject/resubmit) | ✅ |

Document numbers are masked before storage; document files themselves will be uploaded
to Supabase Storage in Phase 6 (the workflow accepts a URL today).

## Phase 3 — Job Applications (implemented)

| Area | Status |
|------|--------|
| Job seeker apply to a job (applicant derived from JWT, not the request body) | ✅ |
| Duplicate-apply prevention + applying gated on `active` jobs | ✅ |
| Job seeker: list my applications, withdraw an application | ✅ |
| Company: list applicants for a job (org-scoped), advance hiring pipeline | ✅ |
| `totalApplications` counter maintained on the job | ✅ |

### Roadmap (later phases)
4. AI Resume Builder (Claude) — text / voice-transcript / upload, 8 Indian languages
5. AI job matching (skills + experience + salary + location scoring)
6. File uploads via Supabase Storage (profile images, resumes, certificates, docs)
7. Notifications, interview scheduling, candidate tracking
8. Subscriptions & payments (Razorpay) + Admin management APIs

Config placeholders for all of the above are already in `application.properties` /
`.env.example` so wiring them later needs no structural change.

---

## 2. Prerequisites

- JDK 17+ (the Gradle toolchain pins language level 17)
- MySQL 8.x running locally
- (Optional) MSG91 / Anthropic / Supabase / Razorpay accounts for live integrations

## 3. Run locally

```bash
# 1. Create the database (or let createDatabaseIfNotExist do it)
#    Full reference schema lives in src/main/resources/v1_tables.sql + v2_auth_tables.sql
#    + v3_profile_verification_tables.sql  (all use CHAR(36) UUID keys)

# 2. Configure env (optional for local — sensible defaults are built in)
cp .env.example .env   # then edit values

# 3. Start
./gradlew bootRun
```

The app boots with `JPA_DDL_AUTO=update`, so the `users` and `otp_tokens` tables are
created automatically on first run. With `SMS_PROVIDER=mock`, OTP codes are printed to
the application log instead of being sent over SMS — perfect for development.

### Loading environment variables
Spring Boot does **not** read `.env` automatically. Choose one:
- **IntelliJ:** Run config → *Environment variables*, or the *EnvFile* plugin pointing at `.env`.
- **Shell:** `export $(grep -v '^#' .env | xargs)` then `./gradlew bootRun`.
- **Production:** set real environment variables in your container/host and use `JPA_DDL_AUTO=validate`.

---

## 4. Connecting the Lovable / Vite frontend

Add these to your frontend's `.env` (Vite exposes only `VITE_`-prefixed vars):

```env
# Point the frontend at this backend
VITE_API_URL=http://localhost:8080/api/v1

# Supabase is used for client-side file uploads to Storage (later phase).
# Use the ANON key in the frontend; the SERVICE ROLE key stays in the backend only.
VITE_SUPABASE_URL=https://YOUR-PROJECT.supabase.co
VITE_SUPABASE_ANON_KEY=your-anon-key

# AI keys are NEVER exposed to the frontend — the backend proxies all AI calls.
# (ANTHROPIC_API_KEY lives only in the backend .env.)
```

> **Security rule of thumb:** the browser only ever holds the Supabase **anon** key and
> `VITE_API_URL`. The Anthropic key, Supabase **service-role** key, MSG91 key, Razorpay
> secret, and JWT secret all live exclusively in the backend environment.

### Frontend API client expectations
- **Auth header:** `Authorization: Bearer <accessToken>` on protected calls.
- **Token storage:** persist `accessToken` (15 min) + `refreshToken` (14 days); when a
  call returns `401`, call `POST /auth/refresh` with the refresh token, then retry.
- **CORS:** add your frontend origin to `CORS_ALLOWED_ORIGINS` (defaults include
  `http://localhost:5173`).

Minimal client example:

```ts
const API = import.meta.env.VITE_API_URL;

async function login(email: string, password: string) {
  const res = await fetch(`${API}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });
  const json = await res.json();          // ApiResponse<AuthResponse>
  if (!json.success) throw new Error(json.message);
  localStorage.setItem("accessToken", json.data.accessToken);
  localStorage.setItem("refreshToken", json.data.refreshToken);
  return json.data.user;
}
```

---

## 5. API contract (Phase 1)

All responses use this envelope:

```json
{
  "success": true,
  "message": "Human readable message",
  "data": { },
  "timestamp": "2026-06-14T10:00:00",
  "totalElements": null,
  "totalPages": null
}
```

Errors use the same shape with `success: false`, a `message`, and (for validation
failures) a `data` map of `field -> error`. Status codes: `400` validation/bad request,
`401` unauthenticated/bad credentials, `403` wrong role, `404` not found, `409` duplicate,
`429` rate limited.

### Auth — `/api/v1/auth`

| Method | Path | Auth | Body | Returns |
|--------|------|------|------|---------|
| POST | `/register/job-seeker` | public | `firstName, lastName, email, phone, password` | `AuthResponse` |
| POST | `/register/company` | public | `firstName, lastName, email, phone, password, companyName, companyEmail` | `AuthResponse` |
| POST | `/login` | public | `email, password` | `AuthResponse` |
| POST | `/refresh` | public | `refreshToken` | `AuthResponse` |
| GET  | `/me` | Bearer | — | `UserSummary` |
| POST | `/email/send-verification` | public | `email` | Sends an `EMAIL_VERIFICATION` code (generic response — no email enumeration) |
| POST | `/email/verify` | public | `email, code` | Flips the account's `emailVerified` |
| POST | `/password/forgot` | public | `email` | Sends a `PASSWORD_RESET` code (generic response) |
| POST | `/password/reset` | public | `email, code, newPassword` | Verifies the code and sets the new password |

`AuthResponse`:
```json
{
  "accessToken": "jwt...",
  "refreshToken": "jwt...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": "uuid", "email": "a@b.com", "phone": "+91...",
    "role": "JOB_SEEKER", "status": "active",
    "emailVerified": false, "phoneVerified": false, "profileId": "uuid"
  }
}
```

### OTP — `/api/v1/otp`

| Method | Path | Body | Notes |
|--------|------|------|-------|
| POST | `/send` | `phone, purpose` | `purpose` ∈ `PHONE_VERIFICATION` / `EMAIL_VERIFICATION` / `LOGIN` / `PASSWORD_RESET` |
| POST | `/verify` | `phone, code, purpose` | On `PHONE_VERIFICATION`, flips the user's `phoneVerified` |

On registration, a `PHONE_VERIFICATION` OTP **and** an `EMAIL_VERIFICATION` code are sent
automatically (best-effort, each in its own transaction). Email codes are delivered via the
`app.email.provider` abstraction — `mock` (default) logs the code to the console; codes share
the OTP store (hashed, cooldown, attempt limits, expiry).

### Job Seeker Profile — `/api/v1/profile` (role `JOB_SEEKER`, Bearer)

| Method | Path | Body | Notes |
|--------|------|------|-------|
| GET | `/me` | — | Full profile + experiences + educations + skills + completion % |
| PUT | `/me` | partial profile fields | Null fields left unchanged |
| POST | `/me/experiences` | `jobTitle, companyName, location, startDate, endDate, isCurrent, description` | |
| PUT | `/me/experiences/{id}` | same | |
| DELETE | `/me/experiences/{id}` | — | |
| POST | `/me/educations` | `degree, fieldOfStudy, institutionName, …` | |
| PUT | `/me/educations/{id}` | same | |
| DELETE | `/me/educations/{id}` | — | |
| POST | `/me/skills` | `skillId` *or* `skillName`, `proficiency`, `yearsOfExperience` | Name auto-creates a catalog skill |
| DELETE | `/me/skills/{applicantSkillId}` | — | |

### Skills — `/api/v1/skills`
| Method | Path | Auth | Notes |
|--------|------|------|-------|
| GET | `/?keyword=&page=&size=` | public | Search/list the catalog |
| POST | `/` | `ADMIN` | Create a catalog skill |

### Company Profile — `/api/v1/company` (role `COMPANY`, Bearer)
| Method | Path | Notes |
|--------|------|-------|
| GET | `/me` | Organization profile + current employer |
| PUT | `/me` | Update organization (company **admin** employer only) |
| GET | `/me/employer` | Current recruiter profile |
| PUT | `/me/employer` | Update current recruiter profile |

### Verification — `/api/v1/verifications` (Bearer)
| Method | Path | Role | Body |
|--------|------|------|------|
| POST | `/identity` | `JOB_SEEKER` | `documentType, documentUrl, documentNumber` |
| POST | `/company` | `COMPANY` (admin) | `documentType, documentUrl, documentNumber` |
| GET | `/me` | any | My submissions |

### Admin Verification — `/api/v1/admin/verifications` (role `ADMIN`, Bearer)
| Method | Path | Notes |
|--------|------|-------|
| GET | `/?status=PENDING&page=&size=` | Review queue |
| PATCH | `/{id}/review` | `decision` (APPROVED/REJECTED/RESUBMIT_REQUIRED), `reviewNotes` — approval flips `identityVerified` / `isVerified` |

### Applications — `/api/v1/applications` (Bearer)
The applicant is always derived from the authenticated user — never trust an id in the body.

| Method | Path | Role | Body | Notes |
|--------|------|------|------|-------|
| POST | `/` | `JOB_SEEKER` | `jobId`, `resumeUrl?`, `coverLetter?` | Apply. Job must be `active`; one application per job. `resumeUrl` falls back to the profile resume |
| GET | `/me` | `JOB_SEEKER` | — | My applications, newest first |
| PATCH | `/{id}/withdraw` | `JOB_SEEKER` | — | Withdraw one of my applications |
| GET | `/job/{jobPublicId}?page=&size=` | `COMPANY` | — | Applicants for one of my org's jobs (org-scoped) |
| PATCH | `/{id}/status` | `COMPANY` | `status` | Advance the pipeline: `reviewing`/`shortlisted`/`interview`/`offered`/`rejected` |

`ApplicationResponse`:
```json
{
  "id": "uuid", "jobId": "uuid", "jobTitle": "…",
  "applicantName": "First Last", "status": "applied",
  "appliedAt": "2026-06-20T10:00:00"
}
```

### Access rules
- Public: auth, otp, Swagger, `GET /jobs/**`, `GET /organizations/**`.
- `ADMIN` only: `/api/v1/admin/**` (reserved for later phases).
- Everything else: authenticated. Fine-grained role checks are applied per-endpoint with
  `@PreAuthorize` (method security is enabled).

---

## 6. Project layout (additions)

```
com.jobaresure
├── config
│   ├── SecurityConfig            # filter chain, CORS, headers, AuthenticationManager
│   ├── OpenApiConfig             # Swagger + Bearer scheme
│   └── properties/*              # typed @ConfigurationProperties (jwt, otp, sms, cors, rate-limit)
├── controller/{AuthController, OtpController}
├── dto/auth/*                    # request/response DTOs + Bean Validation
├── entity/{User, OtpToken}
├── enums/{UserRole, OtpPurpose}
├── repository/{UserRepository, OtpTokenRepository, ApplicantRepository}
├── security/                     # JwtService, filters, UserDetails, entry points, SecurityUtils
└── service/
    ├── AuthService(+Impl), OtpService
    └── sms/{SmsProvider, MockSmsProvider, Msg91SmsProvider}
```

## 7. Production checklist
- Set a strong 32+ char `JWT_SECRET`; rotate periodically.
- `JPA_DDL_AUTO=validate` and apply `v1_tables.sql` + `v2_auth_tables.sql` +
  `v3_profile_verification_tables.sql` via migration (all UUID keys are `CHAR(36)`).
- `SMS_PROVIDER=msg91` with real MSG91 template id + auth key.
- Restrict `CORS_ALLOWED_ORIGINS` to your real frontend domain(s) only.
- Terminate TLS at the proxy (HSTS header is already emitted).
- For multi-node scaling, move the rate-limiter and OTP store to Redis.

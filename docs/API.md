# Jobaresure API Documentation

REST API for the Jobaresure employment platform.

- **Base URL (local):** `http://localhost:8080`
- **API prefix:** all endpoints are under `/api/v1`
- **Content type:** `application/json` for every request/response body
- **Interactive docs:** Swagger UI at `/swagger-ui.html`, OpenAPI spec at `/v3/api-docs`
- **Postman:** an importable collection + environment live in [`/postman`](../postman)

---

## Table of contents

1. [Conventions](#conventions)
2. [Response envelope](#response-envelope)
3. [Authentication](#authentication)
4. [Errors](#errors)
5. [Rate limiting](#rate-limiting)
6. [Pagination](#pagination)
7. [Enum reference](#enum-reference)
8. [Endpoints](#endpoints)
   - [Auth](#auth)
   - [OTP](#otp)
   - [Organizations](#organizations)
   - [Employers](#employers)
   - [Jobs](#jobs)
   - [Job Seeker Profile](#job-seeker-profile)
   - [Company Profile](#company-profile)
   - [Skills](#skills)
   - [Verification](#verification)
   - [Admin · Verification](#admin--verification)
   - [Applications](#applications)

---

## Conventions

| Concept | Detail |
|---|---|
| IDs | Most resources use a UUID (`CHAR(36)`). Jobs are addressed by a human-friendly **`publicId`** string in the URL. |
| Dates | `LocalDate` fields use `YYYY-MM-DD`. Timestamps use ISO-8601 `YYYY-MM-DDTHH:mm:ss`. |
| Money | Salary fields are decimals (e.g. `1500000.00`). |
| Auth roles | `JOB_SEEKER`, `COMPANY`, `ADMIN`. |

### Common request headers

| Header | When | Value |
|---|---|---|
| `Content-Type` | Any request with a body (POST/PUT/PATCH) | `application/json` |
| `Authorization` | Protected endpoints | `Bearer <accessToken>` |

---

## Response envelope

**Every** response is wrapped in a standard envelope:

```json
{
  "success": true,
  "message": "Human-readable summary",
  "data": { },
  "timestamp": "2026-06-21T09:30:00",
  "totalElements": null,
  "totalPages": null
}
```

| Field | Type | Notes |
|---|---|---|
| `success` | boolean | `true` for 2xx, `false` for errors. |
| `message` | string | Short description of the outcome. |
| `data` | object / array / null | The payload. `null` for actions with no return body. |
| `timestamp` | datetime | Server time the response was built. |
| `totalElements` / `totalPages` | integer | Populated for some paginated responses; otherwise `null`. |

Paginated endpoints return a Spring `Page` object inside `data` (see [Pagination](#pagination)).

---

## Authentication

The API uses **stateless JWT Bearer tokens**.

1. Obtain a token pair via `POST /api/v1/auth/login` (or a registration endpoint).
2. Send the access token on protected requests:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIt...
   ```
3. When the access token expires, exchange the refresh token via `POST /api/v1/auth/refresh`.

**Token lifetimes (default):** access token 15 minutes, refresh token 14 days.

### Access matrix

| Area | Access |
|---|---|
| `POST /api/v1/auth/**`, `POST /api/v1/otp/**` | Public |
| `GET /api/v1/jobs/**` | Public |
| `GET /api/v1/organizations/**` | Public |
| `/api/v1/admin/**` | `ADMIN` only |
| `/api/v1/profile/**` | `JOB_SEEKER` only |
| `/api/v1/company/**` | `COMPANY` only |
| Everything else | Any authenticated user |

> Per-endpoint role requirements are noted in each section below.

---

## Errors

Errors use the same envelope with `success: false`. The HTTP status conveys the category:

| Status | When | `message` example |
|---|---|---|
| `400 Bad Request` | Business rule violation / bad input | `"Email already verified"` |
| `400 Bad Request` | Bean-validation failure | `"Validation failed"` (field errors in `data`) |
| `401 Unauthorized` | Missing/invalid/expired token, bad credentials | `"Invalid credentials"` |
| `403 Forbidden` | Authenticated but wrong role | `"You do not have permission to perform this action"` |
| `404 Not Found` | Resource does not exist | `"Organization not found"` |
| `409 Conflict` | Duplicate resource | `"Organization with this name already exists"` |
| `429 Too Many Requests` | Rate limit exceeded | `"Too many requests. Please slow down and try again shortly."` |
| `500 Internal Server Error` | Unhandled error | `"Something went wrong: ..."` |

**Validation error body** (`400`): field-level messages are returned in `data`:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email must be valid",
    "password": "Password must be between 8 and 72 characters"
  }
}
```

---

## Rate limiting

A per-client-IP rate limit is applied to **auth and OTP endpoints only** (`/api/v1/auth/**`, `/api/v1/otp/**`).

- Default budget: **20 requests / 60 seconds** per IP (token-bucket).
- The client IP is taken from `X-Forwarded-For` (first hop) when present, else the socket address.
- On exceed: HTTP **429** with the standard error envelope. No additional rate-limit headers are sent.

---

## Pagination

List endpoints accept these query params:

| Param | Default | Notes |
|---|---|---|
| `page` | `0` | Zero-indexed page number. |
| `size` | `10` (skills: `20`) | Items per page. |
| `sortBy` | `createdAt` | Field to sort by (where supported). |
| `sortDir` | `desc` | `asc` or `desc` (where supported). |

The `data` payload is a Spring `Page`:

```json
{
  "success": true,
  "message": "...",
  "data": {
    "content": [ /* items */ ],
    "pageable": { "pageNumber": 0, "pageSize": 10 },
    "totalElements": 42,
    "totalPages": 5,
    "first": true,
    "last": false,
    "numberOfElements": 10,
    "sort": { "sorted": true }
  }
}
```

---

## Enum reference

| Enum | Allowed values (JSON) |
|---|---|
| `UserRole` | `JOB_SEEKER`, `COMPANY`, `ADMIN` |
| `Status` (account) | `active`, `inactive`, `suspended` |
| `OtpPurpose` | `PHONE_VERIFICATION`, `EMAIL_VERIFICATION`, `LOGIN`, `PASSWORD_RESET` |
| `EmployerRole` | `admin`, `recruiter`, `viewer` |
| `EmployerStatus` | `pending`, `active`, `inactive`, `suspended` |
| `CompanySize` | `SIZE_1_10`, `SIZE_11_50`, `SIZE_51_200`, `SIZE_201_500`, `SIZE_500_PLUS` |
| `JobType` | `full_time`, `part_time`, `contract`, `internship` |
| `WorkMode` | `remote`, `hybrid`, `onsite` |
| `JobStatus` | `draft`, `active`, `closed`, `paused`, `expired` |
| `SeniorityLevel` | `entry`, `mid`, `senior`, `lead`, `manager` |
| `EducationLevel` | `high_school`, `bachelors`, `masters`, `phd` |
| `NoticePeriod` | `immediate`, `days15`, `days30`, `days60`, `days90` |
| `ProficiencyLevel` | `BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT` |
| `ApplicationStatus` | `applied`, `reviewing`, `shortlisted`, `interview`, `offered`, `rejected`, `withdrawn` |
| `DocumentType` | `AADHAAR`, `PAN`, `DRIVING_LICENSE`, `VOTER_ID`, `PASSPORT`, `GST_CERTIFICATE`, `INCORPORATION_CERTIFICATE`, `BUSINESS_PAN`, `UDYAM_CERTIFICATE`, `OTHER` |
| `VerificationType` | `IDENTITY`, `COMPANY` |
| `VerificationStatus` | `PENDING`, `APPROVED`, `REJECTED`, `RESUBMIT_REQUIRED` |

---

# Endpoints

> In the tables below, **Auth** indicates the required access level. All bodies are JSON. Omit `Authorization` for `Public` endpoints.

## Auth

Base path: `/api/v1/auth` · **Rate limited**

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/register/job-seeker` | Public | Register a new job-seeker account |
| POST | `/register/company` | Public | Register a company (creates org + primary contact) |
| POST | `/login` | Public | Log in with email & password |
| POST | `/refresh` | Public | Exchange a refresh token for a new token pair |
| GET | `/me` | Authenticated | Get the currently authenticated user |
| POST | `/email/send-verification` | Public | Send an email-verification code |
| POST | `/email/verify` | Public | Verify an email with the emailed code |
| POST | `/password/forgot` | Public | Request a password-reset code by email |
| POST | `/password/reset` | Public | Reset password using the emailed code |

### POST `/register/job-seeker`
Registers a job seeker and returns a token pair.

**Request**
```json
{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada@example.com",
  "phone": "+919876543210",
  "password": "Password123!"
}
```
Validation: all fields required; `email` valid; `phone` E.164 (`^\+?[1-9]\d{7,14}$`); `password` 8–72 chars.

**Response** `201 Created`
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "accessToken": "eyJ...",
    "refreshToken": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 900000,
    "user": {
      "id": "a2f6c9e3-91c7-4d5b-9d5b-4c3b8a9d2a1f",
      "email": "ada@example.com",
      "phone": "+919876543210",
      "role": "JOB_SEEKER",
      "status": "active",
      "emailVerified": false,
      "phoneVerified": false,
      "profileId": "b1..."
    }
  }
}
```

### POST `/register/company`
**Request**
```json
{
  "firstName": "Grace",
  "lastName": "Hopper",
  "email": "grace@acme.com",
  "phone": "+919876500000",
  "password": "Password123!",
  "companyName": "Acme Corp",
  "companyEmail": "hr@acme.com"
}
```
**Response** `201 Created` — same `AuthResponse` shape as above with `role: "COMPANY"`.

### POST `/login`
**Request**
```json
{ "email": "ada@example.com", "password": "Password123!" }
```
**Response** `200 OK` — `AuthResponse` (see register). `401` on bad credentials.

### POST `/refresh`
**Request**
```json
{ "refreshToken": "eyJ..." }
```
**Response** `200 OK` — a new `AuthResponse` token pair.

### GET `/me`
**Headers:** `Authorization: Bearer <token>`
**Response** `200 OK`
```json
{
  "success": true,
  "message": "Current user",
  "data": {
    "id": "a2f6c9e3-...","email": "ada@example.com","phone": "+91...",
    "role": "JOB_SEEKER","status": "active",
    "emailVerified": true,"phoneVerified": false,"profileId": "b1..."
  }
}
```

### POST `/email/send-verification`
```json
{ "email": "ada@example.com" }
```
`200 OK` — always returns a neutral message (no account enumeration). In `mock` email mode the code is logged to the server console.

### POST `/email/verify`
```json
{ "email": "ada@example.com", "code": "123456" }
```
`200 OK` — `"Email verified successfully"`.

### POST `/password/forgot`
```json
{ "email": "ada@example.com" }
```
`200 OK` — neutral message.

### POST `/password/reset`
```json
{ "email": "ada@example.com", "code": "123456", "newPassword": "NewPassword123!" }
```
`200 OK` — `"Password reset successfully. Please log in."` (`newPassword` 8–72 chars.)

---

## OTP

Base path: `/api/v1/otp` · **Public · Rate limited**

| Method | Path | Description |
|---|---|---|
| POST | `/send` | Send an OTP to a phone for a purpose |
| POST | `/verify` | Verify an OTP code for a phone |

### POST `/send`
```json
{ "phone": "+919876543210", "purpose": "PHONE_VERIFICATION" }
```
`purpose` ∈ `OtpPurpose`. `200 OK` — `"OTP sent successfully"`. In `mock` SMS mode the code is logged to the console.

### POST `/verify`
```json
{ "phone": "+919876543210", "code": "123456", "purpose": "PHONE_VERIFICATION" }
```
`200 OK` — `"OTP verified successfully"`. `400` on wrong/expired code.

---

## Organizations

Base path: `/api/v1/organizations` · **GET = Public; writes = Authenticated**

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/` | Authenticated | Create an organization |
| GET | `/{id}` | Public | Get an organization by UUID |
| GET | `/` | Public | List organizations (paginated) |
| PUT | `/{id}` | Authenticated | Update an organization |
| DELETE | `/{id}` | Authenticated | Delete an organization |
| PATCH | `/{id}/verify` | Authenticated | Mark an organization verified |
| GET | `/check?companyName=` | Public | Check whether a company name exists |

### POST `/`
**Request**
```json
{
  "companyName": "Globex Inc",
  "companyEmail": "hr@globex.com",
  "emailDomain": "globex.com",
  "website": "https://globex.com",
  "industry": "Technology",
  "headquartersLocation": "Bengaluru, India",
  "foundedYear": 2008,
  "description": "Leading tech company",
  "linkedinUrl": "https://linkedin.com/company/globex",
  "twitterUrl": "https://twitter.com/globex"
}
```
Required: `companyName`, `companyEmail`. `foundedYear` ∈ [1800, 2030].

**Response** `201 Created`
```json
{
  "success": true,
  "message": "Organization created successfully",
  "data": {
    "id": "a2f6c9e3-...","companyName": "Globex Inc","companySlug": "globex-inc",
    "emailDomain": "globex.com","companyEmail": "hr@globex.com","companyPhone": null,
    "logoUrl": null,"website": "https://globex.com","industry": "Technology",
    "companySize": null,"description": "Leading tech company","about": null,
    "headquartersLocation": "Bengaluru, India","foundedYear": 2008,
    "linkedinUrl": "...","twitterUrl": "...","isVerified": false,"status": "active",
    "activeJobsCount": 0,"employersCount": 0,
    "createdAt": "2026-06-21T09:30:00","updatedAt": "2026-06-21T09:30:00"
  }
}
```
`409` if the company name already exists.

### GET `/{id}` · GET `/`
`200 OK` returns a full `OrganizationResponse` (single) or a `Page<OrganizationListResponse>` (list). List item fields: `id, companyName, logoUrl, industry, headquartersLocation, isVerified, employersCount, activeJobsCount, status, createdAt`.

### PUT `/{id}`
All fields optional; only supplied fields change.
```json
{ "industry": "Fintech", "about": "We build financial software", "status": "active" }
```
`200 OK` — updated `OrganizationResponse`. `404` if not found.

### DELETE `/{id}`
`200 OK` — `"Organization deleted successfully"`, `data: null`. (Returns the envelope, not an empty 204 body.)

### PATCH `/{id}/verify`
`200 OK` — `OrganizationResponse` with `isVerified: true`.

### GET `/check?companyName=Globex Inc`
`200 OK` — `data` is a boolean (`true` if exists).

---

## Employers

Base path: `/api/v1` (employer routes) · **GET-by-org = Public (under `/organizations/**`); rest = Authenticated**

| Method | Path | Description |
|---|---|---|
| POST | `/organizations/{orgId}/employers` | Create an employer under an org |
| GET | `/employers/{id}` | Get an employer by UUID |
| GET | `/employers` | List all employers (paginated) |
| GET | `/organizations/{orgId}/employers` | List employers of an org (paginated) |
| PUT | `/employers/{id}` | Update an employer |
| DELETE | `/employers/{id}` | Delete an employer |
| PATCH | `/employers/{id}/status` | Change employer status |
| PATCH | `/employers/{id}/role` | Change employer role |
| PATCH | `/employers/{id}/primary` | Set as primary contact |
| GET | `/employers/check?email=` | Check whether an email is registered |
| PATCH | `/employers/{id}/last-login` | Update last-login timestamp |

### POST `/organizations/{orgId}/employers`
**Path param:** `orgId` (UUID).
**Request**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@globex.com",
  "phone": "+919812345678",
  "jobTitle": "HR Manager",
  "department": "HR",
  "role": "recruiter",
  "profilePicture": "https://cdn.example.com/john.png"
}
```
Required: `firstName`, `lastName`, `email`. `role` ∈ `EmployerRole`.

**Response** `201 Created`
```json
{
  "success": true,
  "message": "Employer created successfully",
  "data": {
    "id": "...","userId": null,"organizationId": "...","organizationName": "Globex Inc",
    "firstName": "John","lastName": "Doe","fullName": "John Doe","email": "john.doe@globex.com",
    "phone": "+919812345678","profilePicture": "...","jobTitle": "HR Manager","department": "HR",
    "role": "recruiter","status": "pending","isPrimaryContact": false,"totalJobsPosted": 0,
    "lastLogin": null,"createdAt": "2026-06-21T09:30:00","updatedAt": "2026-06-21T09:30:00"
  }
}
```

### PUT `/employers/{id}`
```json
{ "firstName": "Jonathan", "jobTitle": "Senior Recruiter", "department": "Talent", "role": "recruiter", "status": "active" }
```
`200 OK` — updated `EmployerResponse`.

### PATCH `/employers/{id}/status`
```json
{ "status": "active" }
```
`status` ∈ `EmployerStatus`, required. `200 OK` — `EmployerResponse`.

### PATCH `/employers/{id}/role`
```json
{ "role": "admin" }
```
`role` ∈ `EmployerRole`, required. `200 OK` — `EmployerResponse`.

### PATCH `/employers/{id}/primary` · PATCH `/employers/{id}/last-login`
No body. `200 OK` — `EmployerResponse` (primary) / `data: null` (last-login).

### GET `/employers/check?email=john.doe@globex.com`
`200 OK` — `data` boolean.

### Listing
`GET /employers` and `GET /organizations/{orgId}/employers` return `Page<EmployerListResponse>` (`id, fullName, email, jobTitle, organizationName, role, status, isPrimaryContact, createdAt`).

---

## Jobs

Base path: `/api/v1/jobs` · **GET = Public; writes = Authenticated**

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/` | Authenticated | Create a job |
| GET | `/{publicId}` | Public | Get a job by its public id |
| GET | `/` | Public | List jobs (paginated, newest first) |
| PUT | `/{publicId}` | Authenticated | Update a job |
| PATCH | `/{publicId}/status` | Authenticated | Change a job's status |
| DELETE | `/{publicId}` | Authenticated | Delete a job |
| GET | `/search` | Public | Search/filter jobs (paginated) |

### POST `/`
**Request**
```json
{
  "jobTitle": "Senior Backend Engineer",
  "organizationId": "a2f6c9e3-...",
  "postedBy": "b3d7e1f2-...",
  "jobType": "full_time",
  "workMode": "hybrid",
  "location": "Bengaluru, India",
  "jobDescription": "Build and scale our backend services.",
  "salaryMin": 1500000,
  "salaryMax": 2500000,
  "experienceMin": 3,
  "experienceMax": 7,
  "applicationDeadline": "2026-12-31"
}
```
Required: `jobTitle`, `organizationId` (UUID), `postedBy` (employer UUID), `jobType`, `workMode`, `jobDescription`.

**Response** `201 Created` — full `JobResponse` (addressed afterwards by `data.publicId`):
```json
{
  "success": true,
  "message": "Job created successfully",
  "data": {
    "publicId": "job_ab12cd34","jobTitle": "Senior Backend Engineer",
    "organizationPublicId": "...","organizationName": "Globex Inc",
    "postedByPublicId": "...","postedByName": "John Doe",
    "jobType": "full_time","workMode": "hybrid","location": "Bengaluru, India",
    "salaryMin": 1500000,"salaryMax": 2500000,"salaryCurrency": "USD","isSalaryVisible": true,
    "experienceMin": 3,"experienceMax": 7,"applicationDeadline": "2026-12-31",
    "jobDescription": "...","responsibilities": null,"requirements": null,
    "preferredQualifications": null,"benefits": null,"applicationInstructions": null,
    "numberOfOpenings": 1,"department": null,"seniorityLevel": null,"educationLevel": null,
    "status": "draft","isFeatured": false,"isUrgent": false,
    "totalApplications": 0,"viewsCount": 0,
    "publishedAt": null,"closedAt": null,
    "createdAt": "2026-06-21T09:30:00","updatedAt": "2026-06-21T09:30:00"
  }
}
```

### PUT `/{publicId}`
All fields optional. Accepts the full editable job surface (`responsibilities`, `requirements`, `seniorityLevel`, `educationLevel`, `isFeatured`, `isUrgent`, …). `200 OK` — updated `JobResponse`.

### PATCH `/{publicId}/status`
```json
{ "status": "active" }
```
`status` ∈ `JobStatus`. `200 OK` — `JobResponse`.

### DELETE `/{publicId}`
`200 OK` — `"Job deleted successfully"`, `data: null`.

### GET `/search`
**Query params** (all optional, bound to `JobSearchCriteria`): `keyword`, `location`, `jobType`, `workMode`, plus pagination `page`, `size`, `sortBy`, `sortDir`.
Example: `/api/v1/jobs/search?keyword=engineer&location=Bengaluru&jobType=full_time&workMode=hybrid&page=0&size=10`
`200 OK` — `Page<JobListResponse>`.

---

## Job Seeker Profile

Base path: `/api/v1/profile` · **Role: `JOB_SEEKER`** · `Authorization` required on all.

| Method | Path | Description |
|---|---|---|
| GET | `/me` | Get my full profile (incl. experiences, educations, skills) |
| PUT | `/me` | Update my profile (partial) |
| POST | `/me/experiences` | Add a work experience |
| PUT | `/me/experiences/{id}` | Update a work experience |
| DELETE | `/me/experiences/{id}` | Delete a work experience |
| POST | `/me/educations` | Add an education entry |
| PUT | `/me/educations/{id}` | Update an education entry |
| DELETE | `/me/educations/{id}` | Delete an education entry |
| POST | `/me/skills` | Add a skill (by id or name) |
| DELETE | `/me/skills/{applicantSkillId}` | Remove a skill |

### GET `/me`
`200 OK` — `JobSeekerProfileResponse`:
```json
{
  "success": true,
  "message": "Profile retrieved",
  "data": {
    "id": "...","userId": "...","firstName": "Ada","lastName": "Lovelace",
    "email": "ada@example.com","phone": "+91...","profilePicture": null,
    "headline": "Backend Engineer","bio": "...","currentLocation": "Bengaluru, India",
    "totalExperienceYears": 5.5,"currentSalary": 1800000,"expectedSalary": 2500000,"currency": "INR",
    "resumeUrl": null,"portfolioUrl": "...","linkedinUrl": "...","githubUrl": "...",
    "noticePeriod": "days30","isActivelyLooking": true,"profileCompleted": 80,
    "identityVerified": false,"status": "active",
    "workExperiences": [ /* WorkExperienceDto */ ],
    "educations": [ /* EducationDto */ ],
    "skills": [ /* ApplicantSkillDto */ ]
  }
}
```

### PUT `/me`
Partial update. Example:
```json
{
  "headline": "Backend Engineer",
  "bio": "I love distributed systems.",
  "currentLocation": "Bengaluru, India",
  "totalExperienceYears": 5.5,
  "expectedSalary": 2500000,
  "currency": "INR",
  "noticePeriod": "days30",
  "isActivelyLooking": true
}
```
`noticePeriod` ∈ `NoticePeriod`. `200 OK` — `JobSeekerProfileResponse`.

### POST `/me/experiences`  (and PUT `/me/experiences/{id}`)
```json
{
  "jobTitle": "Backend Engineer",
  "companyName": "Initech",
  "location": "Pune, India",
  "startDate": "2021-01-01",
  "endDate": "2023-06-30",
  "isCurrent": false,
  "description": "Built REST APIs in Spring Boot."
}
```
Required: `jobTitle`, `companyName`. `201 Created` (add) / `200 OK` (update) — `WorkExperienceDto` (`id, jobTitle, companyName, location, startDate, endDate, isCurrent, description`).

### POST `/me/educations`  (and PUT `/me/educations/{id}`)
```json
{
  "degree": "B.Tech",
  "fieldOfStudy": "Computer Science",
  "institutionName": "IIT Bombay",
  "location": "Mumbai, India",
  "startDate": "2014-08-01",
  "endDate": "2018-05-31",
  "isCurrent": false,
  "grade": "8.5 CGPA",
  "description": "Specialized in distributed systems."
}
```
Required: `degree`, `fieldOfStudy`, `institutionName`. Returns `EducationDto`.

### POST `/me/skills`
Provide **either** `skillId` (existing catalog skill) **or** `skillName` (matched/created):
```json
{ "skillName": "Spring Boot", "proficiency": "ADVANCED", "yearsOfExperience": 4 }
```
`proficiency` ∈ `ProficiencyLevel`. `201 Created` — `ApplicantSkillDto` (`id, skillId, skillName, category, proficiency, yearsOfExperience`). Remove via `DELETE /me/skills/{applicantSkillId}` (the `id` from this response).

---

## Company Profile

Base path: `/api/v1/company` · **Role: `COMPANY`** · `Authorization` required on all.

| Method | Path | Description |
|---|---|---|
| GET | `/me` | Get my organization profile |
| PUT | `/me` | Update my organization (company admin) |
| GET | `/me/employer` | Get my employer (recruiter) profile |
| PUT | `/me/employer` | Update my employer profile |

### GET `/me`
`200 OK` — `CompanyProfileResponse` (org fields + nested `currentEmployer`):
```json
{
  "success": true,
  "message": "Company retrieved",
  "data": {
    "id": "...","publicId": "org_ab12","companyName": "Acme Corp","companySlug": "acme-corp",
    "companyEmail": "hr@acme.com","companyPhone": "+91...","logoUrl": "...","website": "...",
    "industry": "Technology","companySize": "SIZE_51_200","description": "...","about": "...",
    "headquartersLocation": "Hyderabad, India","foundedYear": 2010,
    "linkedinUrl": "...","twitterUrl": "...","isVerified": false,"status": "active",
    "currentEmployer": {
      "id": "...","userId": "...","firstName": "Grace","lastName": "Hopper",
      "email": "grace@acme.com","phone": "+91...","profilePicture": null,
      "jobTitle": "Head of Talent","department": "People",
      "role": "admin","status": "active","isPrimaryContact": true
    }
  }
}
```

### PUT `/me`
Partial org update. `companySize` ∈ `CompanySize`.
```json
{ "industry": "Technology", "companySize": "SIZE_51_200", "about": "Founded to make space travel routine.", "foundedYear": 2010 }
```

### PUT `/me/employer`
```json
{ "firstName": "Grace", "lastName": "Hopper", "phone": "+919876500000", "jobTitle": "Head of Talent", "department": "People" }
```
`200 OK` — `EmployerProfileDto`.

---

## Skills

Base path: `/api/v1/skills`

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/` | Authenticated | List/search the skill catalog (paginated) |
| POST | `/` | `ADMIN` | Create a catalog skill |

> Note: skill listing is **not** a public route — any authenticated user may read it; only `ADMIN` may create.

### GET `/?keyword=java&page=0&size=20`
`200 OK` — `Page<SkillResponse>` (`id, name, slug, category`). `keyword` is optional.

### POST `/`
```json
{ "name": "Kubernetes", "category": "DevOps" }
```
Required: `name`. `201 Created` — `SkillResponse`.

---

## Verification

Base path: `/api/v1/verifications` · `Authorization` required on all.

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/identity` | `JOB_SEEKER` | Submit an identity verification |
| POST | `/company` | `COMPANY` | Submit a company verification |
| GET | `/me` | Authenticated | List my verification submissions |

### POST `/identity`  ·  POST `/company`
```json
{
  "documentType": "AADHAAR",
  "documentUrl": "https://cdn.example.com/docs/aadhaar.pdf",
  "documentNumber": "1234-5678-9012"
}
```
Required: `documentType` (∈ `DocumentType`), `documentUrl`. `documentNumber` is masked server-side.

**Response** `201 Created`
```json
{
  "success": true,
  "message": "Identity verification submitted",
  "data": {
    "id": "...","type": "IDENTITY","subjectId": "...","documentType": "AADHAAR",
    "documentUrl": "...","documentNumberMasked": "XXXX-XXXX-9012","status": "PENDING",
    "reviewNotes": null,"submittedAt": "2026-06-21T09:30:00","reviewedAt": null
  }
}
```

### GET `/me`
`200 OK` — `data` is an array of `VerificationResponse`.

---

## Admin · Verification

Base path: `/api/v1/admin/verifications` · **Role: `ADMIN`** · `Authorization` required.

| Method | Path | Description |
|---|---|---|
| GET | `/` | List verification requests (paginated), optional status filter |
| PATCH | `/{id}/review` | Approve / reject / request resubmission |

### GET `/?status=PENDING&page=0&size=20`
`status` (optional) ∈ `VerificationStatus`. `200 OK` — `Page<VerificationResponse>` (newest first).

### PATCH `/{id}/review`
```json
{ "decision": "APPROVED", "reviewNotes": "Documents verified successfully." }
```
Required: `decision` (∈ `VerificationStatus`). `200 OK` — updated `VerificationResponse`.

---

## Applications

Base path: `/api/v1/applications` · `Authorization` required on all.

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/` | `JOB_SEEKER` | Apply to a job |
| GET | `/me` | `JOB_SEEKER` | List my applications |
| PATCH | `/{id}/withdraw` | `JOB_SEEKER` | Withdraw one of my applications |
| GET | `/job/{jobPublicId}` | `COMPANY` | List applications for one of my jobs (paginated) |
| PATCH | `/{id}/status` | `COMPANY` | Move an application through the pipeline |

### POST `/`
```json
{
  "jobId": "a2f6c9e3-91c7-4d5b-9d5b-4c3b8a9d2a1f",
  "resumeUrl": "https://cdn.example.com/resumes/ada.pdf",
  "coverLetter": "I am excited to apply for this role."
}
```
> `jobId` is the job's internal **UUID**, not its `publicId`. Required: `jobId`.

**Response** `201 Created`
```json
{
  "success": true,
  "message": "Application submitted",
  "data": {
    "id": "...","jobId": "...","jobTitle": "Senior Backend Engineer",
    "applicantName": "Ada Lovelace","status": "applied","appliedAt": "2026-06-21T09:30:00"
  }
}
```
`409` if you already applied to that job.

### GET `/me`
`200 OK` — array of `ApplicationResponse`.

### PATCH `/{id}/withdraw`
No body. `200 OK` — `ApplicationResponse` with `status: "withdrawn"`.

### GET `/job/{jobPublicId}?page=0&size=10`
`200 OK` — `Page<ApplicationResponse>` for the given job (company-owned).

### PATCH `/{id}/status`
```json
{ "status": "shortlisted" }
```
`status` ∈ `ApplicationStatus`, required. `200 OK` — updated `ApplicationResponse`.

---

*Generated from the controller and DTO source. Keep in sync when endpoints change, or regenerate from the live OpenAPI spec at `/v3/api-docs`.*

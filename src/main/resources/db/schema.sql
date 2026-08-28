-- ===================================================================
-- Jobaresure — canonical database schema
-- ===================================================================
-- This single file is the source of truth for the application schema.
-- It supersedes the older split migrations (v1_tables.sql,
-- v2_auth_tables.sql, v3_profile_verification_tables.sql).
--
-- All UUID primary/foreign keys are CHAR(36) to match the JPA entities
-- (spring.jpa.properties.hibernate.type.preferred_uuid_jdbc_type=CHAR).
--
-- ENUM value ordering matters: Hibernate's MySQL dialect generates native
-- enum columns with values sorted ALPHABETICALLY. The enum lists below are
-- kept alphabetical so that, if ddl-auto is ever set to "update", Hibernate
-- does not endlessly re-issue "alter table ... modify column" on startup.
--
-- Recommended runtime setting: spring.jpa.hibernate.ddl-auto=validate
-- (Hibernate validates against — but never modifies — this schema.)
--
-- Apply with:
--   mysql -u root -p < src/main/resources/db/schema.sql
-- ===================================================================

CREATE DATABASE IF NOT EXISTS job_dev
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE job_dev;

-- ===================================================================
-- AUTH
-- ===================================================================
CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','COMPANY','JOB_SEEKER') NOT NULL,
    status ENUM('active','inactive','suspended') NOT NULL DEFAULT 'active',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    profile_id CHAR(36),
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS otp_tokens (
    id CHAR(36) PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    purpose ENUM('EMAIL_VERIFICATION','LOGIN','PASSWORD_RESET','PHONE_VERIFICATION') NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    consumed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_otp_identifier_purpose (identifier, purpose)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- ORGANIZATIONS & EMPLOYERS
-- ===================================================================
CREATE TABLE IF NOT EXISTS organizations (
    id CHAR(36) PRIMARY KEY,
    public_id VARCHAR(30) NOT NULL UNIQUE,
    company_name VARCHAR(255) NOT NULL UNIQUE,
    company_slug VARCHAR(255) UNIQUE,
    email_domain VARCHAR(100),
    company_email VARCHAR(255) NOT NULL,
    company_phone VARCHAR(20),
    logo_url VARCHAR(500),
    website VARCHAR(255),
    industry VARCHAR(100),
    company_size ENUM('1-10','11-50','51-200','201-500','500+'),
    description TEXT,
    about TEXT,
    headquarters_location VARCHAR(255),
    founded_year INT,
    linkedin_url VARCHAR(255),
    twitter_url VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    status ENUM('active','inactive','suspended') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS employers (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) UNIQUE,
    organization_id CHAR(36) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    profile_picture VARCHAR(500),
    job_title VARCHAR(150),
    department VARCHAR(100),
    role ENUM('admin','recruiter','viewer') DEFAULT 'recruiter',
    permissions JSON,
    status ENUM('active','inactive','pending','suspended') DEFAULT 'pending',
    is_primary_contact BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_employer_org
        FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    INDEX idx_employer_org (organization_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- APPLICANTS
-- ===================================================================
CREATE TABLE IF NOT EXISTS applicants (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    profile_picture VARCHAR(500),
    headline VARCHAR(255),
    bio TEXT,
    current_location VARCHAR(255),
    preferred_locations JSON,
    total_experience_years DECIMAL(3,1),
    current_salary DECIMAL(10,2),
    expected_salary DECIMAL(10,2),
    currency VARCHAR(3) DEFAULT 'USD',
    resume_url VARCHAR(500),
    portfolio_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    github_url VARCHAR(255),
    notice_period ENUM('15days','30days','60days','90days','immediate'),
    job_type_preference JSON,
    work_mode_preference JSON,
    is_actively_looking BOOLEAN DEFAULT TRUE,
    profile_completed INT DEFAULT 0,
    identity_verified BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('active','inactive','suspended') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_applicant_location (current_location),
    INDEX idx_applicant_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS work_experience (
    id CHAR(36) PRIMARY KEY,
    applicant_id CHAR(36) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE,
    is_current BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS education (
    id CHAR(36) PRIMARY KEY,
    applicant_id CHAR(36) NOT NULL,
    degree VARCHAR(255) NOT NULL,
    field_of_study VARCHAR(255) NOT NULL,
    institution_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN DEFAULT FALSE,
    grade VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- SKILLS
-- ===================================================================
CREATE TABLE IF NOT EXISTS skills (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    category VARCHAR(80),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_skills_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS applicant_skills (
    id CHAR(36) PRIMARY KEY,
    applicant_id CHAR(36) NOT NULL,
    skill_id CHAR(36) NOT NULL,
    proficiency ENUM('ADVANCED','BEGINNER','EXPERT','INTERMEDIATE'),
    years_of_experience INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_applicant_skill (applicant_id, skill_id),
    FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- JOBS
-- ===================================================================
CREATE TABLE IF NOT EXISTS jobs (
    id CHAR(36) PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    job_title VARCHAR(255) NOT NULL,
    organization_id CHAR(36) NOT NULL,
    posted_by CHAR(36) NOT NULL,
    job_type VARCHAR(50) NOT NULL,
    work_mode VARCHAR(50) NOT NULL,
    location VARCHAR(255),
    salary_min DECIMAL(10,2),
    salary_max DECIMAL(10,2),
    salary_currency VARCHAR(3) DEFAULT 'USD',
    is_salary_visible BOOLEAN DEFAULT TRUE,
    experience_min INT,
    experience_max INT,
    application_deadline DATE,
    total_applications INT DEFAULT 0,
    views_count INT DEFAULT 0,
    job_description TEXT NOT NULL,
    responsibilities TEXT,
    requirements TEXT,
    preferred_qualifications TEXT,
    benefits TEXT,
    application_instructions TEXT,
    number_of_openings INT DEFAULT 1,
    department VARCHAR(100),
    seniority_level VARCHAR(50),
    education_level VARCHAR(50),
    status VARCHAR(50) DEFAULT 'draft',
    is_featured BOOLEAN DEFAULT FALSE,
    is_urgent BOOLEAN DEFAULT FALSE,
    external_job_url VARCHAR(500),
    published_at TIMESTAMP NULL,
    closed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (posted_by) REFERENCES employers(id) ON DELETE CASCADE,
    INDEX idx_jobs_status (status),
    INDEX idx_jobs_location (location),
    INDEX idx_jobs_type (job_type),
    INDEX idx_jobs_work_mode (work_mode),
    INDEX idx_jobs_org (organization_id),
    INDEX idx_jobs_posted_by (posted_by),
    INDEX idx_jobs_featured (is_featured),
    INDEX idx_jobs_urgent (is_urgent),
    INDEX idx_jobs_seniority (seniority_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS job_skills (
    id CHAR(36) PRIMARY KEY,
    job_id CHAR(36) NOT NULL,
    skill_id CHAR(36) NOT NULL,
    is_required BOOLEAN DEFAULT TRUE,
    priority INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_job_skill (job_id, skill_id),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- APPLICATIONS
-- ===================================================================
CREATE TABLE IF NOT EXISTS applications (
    id CHAR(36) PRIMARY KEY,
    job_id CHAR(36) NOT NULL,
    applicant_id CHAR(36) NOT NULL,
    resume_url VARCHAR(500),
    cover_letter TEXT,
    status ENUM('applied','interview','offered','rejected','reviewing','shortlisted','withdrawn') DEFAULT 'applied',
    current_salary DECIMAL(10,2),
    expected_salary DECIMAL(10,2),
    available_from DATE,
    answers JSON,
    notes_by_employer TEXT,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_application (job_id, applicant_id),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE,
    INDEX idx_applications_status (status),
    INDEX idx_applications_job (job_id),
    INDEX idx_applications_applicant (applicant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- VERIFICATION
-- ===================================================================
CREATE TABLE IF NOT EXISTS verification_records (
    id CHAR(36) PRIMARY KEY,
    type ENUM('COMPANY','IDENTITY') NOT NULL,
    subject_id CHAR(36) NOT NULL,
    submitted_by_user_id CHAR(36),
    document_type ENUM('AADHAAR','BUSINESS_PAN','DRIVING_LICENSE','GST_CERTIFICATE',
                       'INCORPORATION_CERTIFICATE','OTHER','PAN','PASSPORT',
                       'UDYAM_CERTIFICATE','VOTER_ID') NOT NULL,
    document_url VARCHAR(1000),
    document_number_masked VARCHAR(50),
    status ENUM('APPROVED','PENDING','REJECTED','RESUBMIT_REQUIRED') NOT NULL DEFAULT 'PENDING',
    review_notes VARCHAR(1000),
    reviewed_by_user_id CHAR(36),
    reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_verif_subject (type, subject_id),
    INDEX idx_verif_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
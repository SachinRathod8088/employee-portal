-- employee_mgmt.sql
-- Employee Management System - idempotent, compatible version (fixed ON CONFLICT issues)
-- Save and run in pgAdmin Query Tool or with psql -d employee_mgmt -f path/to/employee_mgmt_full_fixed2.sql

-- OPTIONAL: Drop & recreate database (UNCOMMENT if you want a clean DB)
-- DROP DATABASE IF EXISTS employee_mgmt;
-- CREATE DATABASE employee_mgmt WITH ENCODING = 'UTF8';
-- \c employee_mgmt

/**************************************************************
 BLOCK 1 — Extensions
**************************************************************/
CREATE EXTENSION IF NOT EXISTS pgcrypto;

/**************************************************************
 BLOCK 2 — ENUM TYPES (wrapped for compatibility)
**************************************************************/
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'gender_enum') THEN
    CREATE TYPE gender_enum AS ENUM ('Male','Female','Other');
  END IF;
END;
$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'emp_status_enum') THEN
    CREATE TYPE emp_status_enum AS ENUM ('Active','Inactive','Terminated','On_Leave');
  END IF;
END;
$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'project_status_enum') THEN
    CREATE TYPE project_status_enum AS ENUM ('Planned','Ongoing','Completed','OnHold','Cancelled');
  END IF;
END;
$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'attendance_status_enum') THEN
    CREATE TYPE attendance_status_enum AS ENUM ('Present','Absent','On_Leave','Half_Day','Holiday','WFH');
  END IF;
END;
$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'salary_payment_mode_enum') THEN
    CREATE TYPE salary_payment_mode_enum AS ENUM ('BankTransfer','Cheque','Cash');
  END IF;
END;
$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'skill_experience_enum') THEN
    CREATE TYPE skill_experience_enum AS ENUM ('Fresher','0-1','1-2','2-5','5+','Experienced');
  END IF;
END;
$$;

/**************************************************************
 BLOCK 3 — Helper trigger function to auto-update updated_at
**************************************************************/
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

/**************************************************************
 BLOCK 4 — MASTER TABLES: department, location, grade
**************************************************************/
CREATE TABLE IF NOT EXISTS department_master (
  department_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  department_code VARCHAR(20) UNIQUE,
  department_name VARCHAR(100) NOT NULL,
  description TEXT,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_department_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_department_updated BEFORE UPDATE ON department_master FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS location_master (
  location_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  location_code VARCHAR(20) UNIQUE,
  location_name VARCHAR(150) NOT NULL,
  address TEXT,
  city VARCHAR(100),
  state VARCHAR(100),
  country VARCHAR(100),
  postal_code VARCHAR(20),
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_location_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_location_updated BEFORE UPDATE ON location_master FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS grade_master (
  grade_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  grade_name VARCHAR(50) NOT NULL,
  min_salary NUMERIC(12,2) DEFAULT 0.00,
  max_salary NUMERIC(12,2) DEFAULT 0.00,
  description TEXT,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_grade_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_grade_updated BEFORE UPDATE ON grade_master FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

/**************************************************************
 BLOCK 5 — MASTER TABLES: designation, employee_type, skill_master
**************************************************************/
CREATE TABLE IF NOT EXISTS designation_master (
  designation_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  designation_name VARCHAR(100) NOT NULL,
  grade_id BIGINT,
  description TEXT,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now(),
  CONSTRAINT fk_designation_grade FOREIGN KEY (grade_id) REFERENCES grade_master(grade_id) ON DELETE SET NULL ON UPDATE CASCADE
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_designation_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_designation_updated BEFORE UPDATE ON designation_master FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS employee_type_master (
  employee_type_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  employee_type_name VARCHAR(50) NOT NULL,
  description TEXT,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_employee_type_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_employee_type_updated BEFORE UPDATE ON employee_type_master FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS skill_master (
  skill_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  skill_name VARCHAR(100) NOT NULL UNIQUE,
  skill_category VARCHAR(100),
  skill_experience_category skill_experience_enum DEFAULT 'Fresher',
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_skill_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_skill_updated BEFORE UPDATE ON skill_master FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

/**************************************************************
 BLOCK 6 — MASTER TABLE: project_master
**************************************************************/
CREATE TABLE IF NOT EXISTS project_master (
  project_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_code VARCHAR(50) UNIQUE,
  project_name VARCHAR(150) NOT NULL,
  client_name VARCHAR(150),
  start_date DATE,
  end_date DATE,
  status project_status_enum DEFAULT 'Planned',
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_project_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_project_updated BEFORE UPDATE ON project_master FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

/**************************************************************
 BLOCK 7 — CORE: employee
**************************************************************/
CREATE TABLE IF NOT EXISTS employee (
  employee_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  employee_code VARCHAR(50) UNIQUE,
  first_name VARCHAR(100) NOT NULL,
  middle_name VARCHAR(100),
  last_name VARCHAR(100),
  gender gender_enum DEFAULT 'Male',
  date_of_birth DATE,
  email VARCHAR(150) UNIQUE,
  phone VARCHAR(30),
  alternate_phone VARCHAR(30),
  department_id BIGINT,
  designation_id BIGINT,
  grade_id BIGINT,
  employee_type_id BIGINT,
  location_id BIGINT,
  date_of_joining DATE,
  date_of_leaving DATE,
  status emp_status_enum DEFAULT 'Active',
  is_deleted BOOLEAN DEFAULT FALSE,
  created_by BIGINT,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now(),
  CONSTRAINT fk_emp_department FOREIGN KEY (department_id) REFERENCES department_master(department_id) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_emp_designation FOREIGN KEY (designation_id) REFERENCES designation_master(designation_id) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_emp_grade FOREIGN KEY (grade_id) REFERENCES grade_master(grade_id) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_emp_type FOREIGN KEY (employee_type_id) REFERENCES employee_type_master(employee_type_id) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_emp_location FOREIGN KEY (location_id) REFERENCES location_master(location_id) ON DELETE SET NULL ON UPDATE CASCADE
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_employee_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_employee_updated BEFORE UPDATE ON employee FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_emp_dept ON employee(department_id);
CREATE INDEX IF NOT EXISTS idx_emp_desig ON employee(designation_id);
CREATE INDEX IF NOT EXISTS idx_emp_status ON employee(status);

/**************************************************************
 BLOCK 8 — RELATED: employee_address, employee_skills
**************************************************************/
CREATE TABLE IF NOT EXISTS employee_address (
  address_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  employee_id BIGINT NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
  address_type VARCHAR(30) DEFAULT 'Present',
  line1 VARCHAR(255),
  line2 VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(100),
  country VARCHAR(100),
  postal_code VARCHAR(20),
  is_primary BOOLEAN DEFAULT FALSE,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_emp_address_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_emp_address_updated BEFORE UPDATE ON employee_address FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;
CREATE INDEX IF NOT EXISTS idx_addr_emp ON employee_address(employee_id);

CREATE TABLE IF NOT EXISTS employee_skills (
  employee_skill_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  employee_id BIGINT NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
  skill_id BIGINT NOT NULL REFERENCES skill_master(skill_id) ON DELETE CASCADE ON UPDATE CASCADE,
  proficiency VARCHAR(20) DEFAULT 'Intermediate',
  years_experience NUMERIC(4,1) DEFAULT 0.0,
  created_at timestamptz DEFAULT now(),
  UNIQUE (employee_id, skill_id)
);

/**************************************************************
 BLOCK 9 — AUTH: employee_login
**************************************************************/
CREATE TABLE IF NOT EXISTS employee_login (
  login_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  employee_id BIGINT NOT NULL UNIQUE REFERENCES employee(employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  last_login timestamptz,
  is_locked BOOLEAN DEFAULT FALSE,
  failed_attempts INTEGER DEFAULT 0,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_emp_login_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_emp_login_updated BEFORE UPDATE ON employee_login FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();';
  END IF;
END$$;

/**************************************************************
 BLOCK 10 — TRANSACTIONAL: employee_salary, employee_project_allocation
**************************************************************/
CREATE TABLE IF NOT EXISTS employee_salary (
  salary_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  employee_id BIGINT NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
  effective_from DATE NOT NULL,
  effective_to DATE,
  basic_salary NUMERIC(12,2) NOT NULL DEFAULT 0.00,
  hra NUMERIC(12,2) DEFAULT 0.00,
  allowances NUMERIC(12,2) DEFAULT 0.00,
  deductions NUMERIC(12,2) DEFAULT 0.00,
  net_salary NUMERIC(12,2) GENERATED ALWAYS AS (basic_salary + hra + allowances - deductions) STORED,
  created_at timestamptz DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_salary_emp ON employee_salary(employee_id);

CREATE TABLE IF NOT EXISTS employee_project_allocation (
  allocation_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  employee_id BIGINT NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
  project_id BIGINT NOT NULL REFERENCES project_master(project_id) ON DELETE CASCADE ON UPDATE CASCADE,
  role_in_project VARCHAR(100),
  allocation_start_date DATE,
  allocation_end_date DATE,
  allocation_percent SMALLINT DEFAULT 100 CHECK (allocation_percent >= 0 AND allocation_percent <= 100),
  created_at timestamptz DEFAULT now(),
  UNIQUE (employee_id, project_id, allocation_start_date)
);
CREATE INDEX IF NOT EXISTS idx_alloc_emp ON employee_project_allocation(employee_id);

/**************************************************************
 BLOCK 11 — promotion_history (UPDATED)
**************************************************************/
CREATE TABLE IF NOT EXISTS promotion_history (
  promo_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  employee_id BIGINT NOT NULL REFERENCES employee(employee_id) ON DELETE CASCADE ON UPDATE CASCADE,
  new_designation_id BIGINT REFERENCES designation_master(designation_id) ON DELETE SET NULL ON UPDATE CASCADE,
  new_grade_id BIGINT REFERENCES grade_master(grade_id) ON DELETE SET NULL ON UPDATE CASCADE,
  effective_date DATE NOT NULL,
  remarks TEXT,
  created_at timestamptz DEFAULT now()
);

/**************************************************************
 BLOCK 12 — helper lookup table
**************************************************************/
CREATE TABLE IF NOT EXISTS employee_status_lookup (
  status_code VARCHAR(30) PRIMARY KEY,
  description TEXT
);

INSERT INTO employee_status_lookup (status_code, description)
VALUES 
  ('Active','Currently employed and active'),
  ('Inactive','Employment paused or not active'),
  ('Terminated','Employment ended'),
  ('On_Leave','Temporarily on leave')
ON CONFLICT DO NOTHING;

/**************************************************************
 BLOCK 13 — SAMPLE MASTER DATA
  (USE ON CONFLICT DO NOTHING — generic and safe)
**************************************************************/
INSERT INTO department_master (department_code, department_name, description)
VALUES
  ('HR','Human Resources','Handles recruitment and employee relations'),
  ('IT','Information Technology','Tech and infra'),
  ('FIN','Finance','Accounts & payroll')
ON CONFLICT DO NOTHING;

INSERT INTO location_master (location_code, location_name, address, city, state, country, postal_code)
VALUES
  ('PNV','Panvel Office','Plot 123, Industrial Area','Panvel','Maharashtra','India','410206'),
  ('MUM','Mumbai Office','Tower A, Business Park','Mumbai','Maharashtra','India','400001')
ON CONFLICT DO NOTHING;

INSERT INTO grade_master (grade_name, min_salary, max_salary, description)
VALUES
  ('G1',20000,40000,'Entry level'),
  ('G2',40001,80000,'Mid level'),
  ('G3',80001,150000,'Senior level')
ON CONFLICT DO NOTHING;

INSERT INTO designation_master (designation_name, grade_id, description)
SELECT 'Junior QA', g.grade_id, 'Junior level QA engineer' FROM grade_master g WHERE g.grade_name='G1'
ON CONFLICT DO NOTHING;

INSERT INTO designation_master (designation_name, grade_id, description)
SELECT 'QA Engineer', g.grade_id, 'Mid level QA engineer' FROM grade_master g WHERE g.grade_name='G2'
ON CONFLICT DO NOTHING;

INSERT INTO designation_master (designation_name, grade_id, description)
SELECT 'Senior QA', g.grade_id, 'Senior QA engineer' FROM grade_master g WHERE g.grade_name='G3'
ON CONFLICT DO NOTHING;

INSERT INTO employee_type_master (employee_type_name, description)
VALUES
  ('Full-time','Full time permanent employee'),
  ('Contract','Contractor'),
  ('Intern','Internship')
ON CONFLICT DO NOTHING;

INSERT INTO skill_master (skill_name, skill_category, skill_experience_category)
VALUES
  ('Selenium','Automation','0-1'),
  ('Java','Programming','2-5'),
  ('SQL','Database','1-2')
ON CONFLICT DO NOTHING;

INSERT INTO project_master (project_code, project_name, client_name, start_date, status)
VALUES
  ('PROJ-001','Employee Portal','Acme Corp','2025-01-01','Ongoing'),
  ('PROJ-002','Payroll Revamp','Beta Ltd','2024-10-01','Ongoing')
ON CONFLICT DO NOTHING;

/**************************************************************
 BLOCK 14 — SAMPLE EMPLOYEE + RELATED ROWS
**************************************************************/
INSERT INTO employee (employee_code, first_name, last_name, gender, date_of_birth, email, phone,
                      department_id, designation_id, grade_id, employee_type_id, location_id, date_of_joining, status)
SELECT
  'EMP2025-001',
  'Prithviraj',
  'Patil',
  'Male'::gender_enum,
  '2001-06-08'::date,
  'prithvi@example.com',
  '9876543210',
  d.department_id,
  des.designation_id,
  g.grade_id,
  et.employee_type_id,
  l.location_id,
  '2025-06-15'::date,
  'Active'::emp_status_enum
FROM department_master d
JOIN designation_master des ON des.designation_name = 'Junior QA'
JOIN grade_master g ON g.grade_name = 'G1'
JOIN employee_type_master et ON et.employee_type_name = 'Full-time'
JOIN location_master l ON l.location_code = 'PNV'
WHERE d.department_code = 'IT'
ON CONFLICT DO NOTHING;

WITH e AS (SELECT employee_id FROM employee WHERE email='prithvi@example.com')
INSERT INTO employee_address (employee_id, address_type, line1, city, state, country, postal_code, is_primary)
SELECT e.employee_id, 'Permanent', 'House 12, Sector 5', 'Panvel', 'Maharashtra', 'India', '410206', TRUE FROM e
ON CONFLICT DO NOTHING;

WITH e AS (SELECT employee_id FROM employee WHERE email='prithvi@example.com')
INSERT INTO employee_skills (employee_id, skill_id, proficiency, years_experience)
SELECT e.employee_id, s.skill_id, 'Beginner', 0.5 FROM e JOIN skill_master s ON s.skill_name='Selenium'
ON CONFLICT (employee_id, skill_id) DO NOTHING;

WITH e AS (SELECT employee_id FROM employee WHERE email='prithvi@example.com')
INSERT INTO employee_skills (employee_id, skill_id, proficiency, years_experience)
SELECT e.employee_id, s.skill_id, 'Intermediate', 1.2 FROM e JOIN skill_master s ON s.skill_name='SQL'
ON CONFLICT (employee_id, skill_id) DO NOTHING;

WITH e AS (SELECT employee_id FROM employee WHERE email='prithvi@example.com')
INSERT INTO employee_login (employee_id, username, password_hash)
SELECT e.employee_id, 'prithviraj.patil', 'REPLACE_WITH_HASH' FROM e
ON CONFLICT DO NOTHING;

WITH e AS (SELECT employee_id FROM employee WHERE email='prithvi@example.com')
INSERT INTO employee_salary (employee_id, effective_from, basic_salary, hra, allowances, deductions)
SELECT e.employee_id, '2025-06-15'::date, 25000.00, 8000.00, 2000.00, 500.00 FROM e
ON CONFLICT DO NOTHING;

WITH e AS (SELECT employee_id FROM employee WHERE email='prithvi@example.com'),
      p AS (SELECT project_id FROM project_master WHERE project_code='PROJ-001')
INSERT INTO employee_project_allocation (employee_id, project_id, role_in_project, allocation_start_date, allocation_percent)
SELECT e.employee_id, p.project_id, 'QA Tester', '2025-06-16'::date, 100 FROM e CROSS JOIN p
ON CONFLICT (employee_id, project_id, allocation_start_date) DO NOTHING;

WITH e AS (SELECT employee_id FROM employee WHERE email='prithvi@example.com'),
     des_new AS (SELECT designation_id FROM designation_master WHERE designation_name='QA Engineer'),
     g_new AS (SELECT grade_id FROM grade_master WHERE grade_name='G2')
INSERT INTO promotion_history (employee_id, new_designation_id, new_grade_id, effective_date, remarks)
SELECT e.employee_id, des_new.designation_id, g_new.grade_id, '2026-01-01'::date, 'Promoted after 6 months' FROM e, des_new, g_new
ON CONFLICT DO NOTHING;

-- End of file






-- registration extra added--


CREATE TABLE IF NOT EXISTS registration (
  registration_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  phone VARCHAR(30),
  temp_password_hash VARCHAR(255), -- store hashed temp password or null
  verification_token VARCHAR(255),
  token_expires_at timestamptz,
  status VARCHAR(30) DEFAULT 'PENDING', -- PENDING | VERIFIED | CANCELLED | APPROVED
  linked_employee_id BIGINT, -- set after admin approval or auto-create
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now(),
  UNIQUE (username),
  UNIQUE (email),
  CONSTRAINT fk_registration_employee FOREIGN KEY (linked_employee_id) REFERENCES employee(employee_id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- trigger function to keep updated_at auto
CREATE OR REPLACE FUNCTION trg_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_registration_updated') THEN
    EXECUTE 'CREATE TRIGGER trg_registration_updated BEFORE UPDATE ON registration FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();';
  END IF;
END$$;


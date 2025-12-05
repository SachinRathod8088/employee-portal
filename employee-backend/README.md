# Employee Backend

## Prereqs
- Java 17+
- Maven
- PostgreSQL (database `employee_mgmt`)

## Steps
1. Apply your full schema SQL (recommended) before starting:
   - Run your `employee_mgmt_full_fixed2.sql` using psql or pgAdmin:
     `psql -U postgres -d employee_mgmt -f path/to/employee_mgmt_full_fixed2.sql`
   - OR place it as `src/main/resources/db/migration/V1__initial_schema.sql` (Flyway will run it).

2. Build & run:

/mvnw clean package
./mvnw spring-boot:run

or


mvn clean package
java -jar target/employee-backend-0.0.1-SNAPSHOT.jar


3. Environment variables:
- DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, JWT_SECRET

Example (Linux/mac):


export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=employee_mgmt
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=very_long_secret_change_me_please
./mvnw spring-boot:run


Windows PowerShell:
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="employee_mgmt"
$env:DB_USER="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="very_long_secret_change_me_please"
mvnw spring-boot:run

Endpoints

POST /api/auth/register — register (creates registration record)

POST /api/auth/login — login; returns token and employee (if linked)

POST /api/auth/registration/{id}/approve — approve registration (creates employee & login)

GET /api/employees — list employees

GET /api/employees/{id} — get employee

POST /api/employees — create employee

PUT /api/employees/{id} — update employee

DELETE /api/employees/{id} — delete employee


---



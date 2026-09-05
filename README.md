# Sunrise Dental Clinic — Appointment & Patient Management System

A 3-tier Java web application (native Servlets + JSP + JDBC, no Spring/Struts/Hibernate)
built for the CIS6003 Advanced Programming assessment brief. Runs on Apache Tomcat against
a MySQL database created in MySQL Workbench.

## 1. Prerequisites

- JDK 11+
- Apache Maven 3.6+
- MySQL Server 8.x + MySQL Workbench
- **Apache Tomcat 10.1.x** (this project uses the `jakarta.servlet.*` API namespace and a
  `web-app_6_0` `web.xml`, matching Tomcat 10.1's Jakarta Servlet 6.0 implementation).
  If you're on Tomcat 9.x instead, swap the `jakarta.servlet:jakarta.servlet-api:6.0.0`
  dependency in `pom.xml` for `javax.servlet:javax.servlet-api:4.0.1`, change every
  `jakarta.servlet` import back to `javax.servlet`, and revert `web.xml` to the
  `web-app_4_0`/`version="4.0"` schema shown in its comments.
- (Optional, for the Selenium test) Google Chrome + matching chromedriver

## 2. Database setup (MySQL Workbench)

**Fresh install** (no existing data):
1. Open MySQL Workbench, connect to your local server.
2. Open `database/schema.sql` and run the whole script. This creates the `sunrise_dental`
   database, all tables (including `duration_minutes` on treatments and `birth_year`/`gender`
   on users), the `fn_calculate_total` function, the overlap-aware `trg_prevent_double_booking`
   and `trg_audit_bill_insert` triggers, the `sp_register_appointment`,
   `sp_register_appointment_existing`, and `sp_generate_bill` stored procedures, the reporting
   views, and seed data (3 dentists, 6 treatments with durations).
3. Generate a real password hash for the admin account:
   ```
   javac -d out src/main/java/com/sunrise/dental/util/PasswordUtil.java
   java -cp out com.sunrise.dental.util.PasswordUtil "Admin@123"
   ```
   Copy the printed hash and run:
   ```sql
   UPDATE users SET password_hash = '<paste-hash-here>' WHERE username = 'admin';
   ```

**Upgrading an existing database**: run whichever migrations you're missing, in order:
`database/migration_v2.sql` → `database/migration_v3.sql` → `database/migration_v4.sql`.
- v2: session/help-page groundwork from the first UI pass
- v3: per-dentist consultation fees, patient birth year/gender, one-bill-per-appointment,
  appointment status updates
- v4: lets an admin delete a receptionist account without losing that receptionist's
  historical appointments (their `created_by` is set to NULL instead of the delete being blocked)

If you're already on v3, just run v4.

## 3. Configure the database connection

Edit `src/main/java/com/sunrise/dental/db/DBConnectionManager.java` and set `DB_USER` /
`DB_PASSWORD` to match your local MySQL credentials.

## 4. Build the WAR

```
mvn clean package
```
This produces `target/dental-clinic-system.war`.

## 5. Deploy to Tomcat

Copy the WAR into Tomcat's `webapps/` folder (or deploy it through the Tomcat Manager app),
then start Tomcat. Visit:

```
http://localhost:8080/dental-clinic-system/
```

Log in with **admin / Admin@123** (or whatever password you hashed in step 2).

## 6. Running the automated tests

Fast unit tests (no DB/Tomcat/browser needed — mock the DAO layer with Mockito):
```
mvn test -Dtest=AppointmentServiceTest,BillingServiceTest
```

Full end-to-end Selenium test (needs MySQL + Tomcat running and Chrome installed):
```
mvn test -Dtest=LoginFlowAutomationTest
```

See `docs/testing-and-tdd.md` for the full test plan, rationale, and how the tests were
written test-first (TDD).

## 7. Project structure

```
dental-clinic-system/
├── pom.xml
├── database/schema.sql          <- fresh install: run this in MySQL Workbench
database/migration_v2.sql    <- upgrading an existing v1 database: run this instead
├── src/main/java/com/sunrise/dental/
│   ├── model/       plain data objects (Patient, Appointment, Bill, ...)
│   ├── db/          DBConnectionManager (Singleton)
│   ├── dao/, dao/impl/   Data Access Object interfaces + JDBC implementations
│   ├── factory/     DAOFactory (Factory Method)
│   ├── billing/     BillingStrategy + implementations (Strategy)
│   ├── observer/    AppointmentEventPublisher/Listener (Observer)
│   ├── service/     business logic tier (AppointmentService, BillingService, ...)
│   ├── controller/  FrontControllerServlet + controller/handler (Front Controller + Command)
│   ├── exception/   custom checked exceptions
│   └── util/        PasswordUtil, ValidationUtil, MessageUtil
├── src/main/webapp/  JSP views, css
├── src/test/java/    JUnit/Mockito unit tests + Selenium end-to-end test
└── docs/             design pattern & TDD write-ups (for your report)
```

## 8. Documentation for your report

- `docs/design-patterns.md` — every design pattern used, where, why, and an evaluation of each.
- `docs/testing-and-tdd.md` — test rationale, the TDD red-green-refactor log, test data, and
  an explanation of how the test automation works.
- `docs/future-features-and-git-workflow.md` — a suggested roadmap of extra features plus an
  example day-by-day Git commit sequence, useful for Task D (Git/GitHub with version history).

## 9. Feature overview

- **Role-based access** — RECEPTIONIST accounts get every operational feature (appointments,
  patients, reports, profile). ADMIN accounts additionally see "Add User" (create a Receptionist
  or Admin login) and "Manage Users" (search/filter-by-role/paginate every account, with a Delete
  button shown only on Receptionist cards). The Front Controller enforces this server-side, not
  just by hiding the nav links - a Receptionist hitting the URL directly is redirected away.
- **Appointments dashboard** — searchable, filterable (dentist/treatment/status), sortable,
  paginated card view of every appointment.
- **Add Appointment page** — a New Patient / Existing Patient toggle, a live patient search box
  (matches by name, contact number, or patient ID), treatment-duration-aware scheduling with a
  server-computed time-slot picker that greys out unavailable 15-minute slots up front (rather
  than only rejecting on submit), and dates restricted to today-or-future.
- **Patients page** — searchable/paginated patient list (showing patient ID, birth year, gender);
  each patient's detail view always shows three cards (Total Appointments, Last Appointment, Next
  Appointment), rendering "-" when N/A.
- **Reports** — each of the three report tables (Revenue by Treatment, Dentist Workload,
  Outstanding Bills) has its own date-range filter defaulting to the last 30 days, plus a
  "Generate PDF" button (via the OpenPDF library, added as a plain Maven dependency) that
  exports that table with the clinic name, report title, and applied date range printed.
- **Profile page** — staff can view/edit their own name, birth year, and gender.
- **Public self-registration** — new staff can create their own Receptionist-level account
  from the login page; admins provision either role from the "Add User" page.
- **Validation everywhere** — every form field is validated both client-side (HTML5
  pattern/min/max) and server-side (`ValidationUtil`): names reject digits/symbols, contact
  numbers must be exactly 10 digits, birth years can't be in the future, appointment
  date/times can't be in the past, and time slots must land on a valid 15-minute block.

## 10. Assumptions made

- Each "appointment" implicitly registers a new patient record (the brief describes patient
  details as part of the appointment form, not a separate "manage patients" screen), matching
  section 2 of the brief.
- `RECEPTIONIST` is the operational role (appointments, patients, reports, profile); `ADMIN`
  additionally gets user management and can still do everything a Receptionist can - the brief
  doesn't specify differentiated permissions, so this two-tier split (with Admin retaining full
  access rather than being restricted to user management only) is a documented assumption, made
  so the single seeded account can both bootstrap the system and provision further staff.
- Clinic opening hours are assumed to be 08:00–17:00 (last appointment starts at 17:00), with
  bookable slots fixed to 30-minute blocks, for the purposes of time validation and the
  duration-aware overlap check.
- A 10% loyalty discount from a patient's 3rd visit onward was added as the value-adding
  business rule for the billing Strategy pattern and the double-booking trigger.
- Self-registration always creates a `RECEPTIONIST` account; creating an `ADMIN` account
  requires an existing admin to use the "Add New User" form, since the brief doesn't specify
  a role-elevation workflow and self-service admin creation would be a security gap.
- "Last appointment" / "Next appointment" on the patient detail page are computed from the
  current date/time server-side (an appointment already at/before now counts as "last";
  the nearest still-`SCHEDULED` one in the future counts as "next").

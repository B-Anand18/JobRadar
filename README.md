# JobRadar - Java Backend Job Aggregator

Ever opened LinkedIn at 11 AM, found a "perfect match" job posted 3 hours ago, and saw **1000+ applicants** already? Yeah, me too. That's exactly why I built JobRadar.

This Spring Boot application fetches fresh Java backend jobs from LinkedIn (last 24 hours only), filters them by location, and emails me every morning at 9 AM. No more scrolling through stale listings or competing with thousands of applicants.

## Why This Exists

The job market moves fast. By the time you see a job posting on LinkedIn, hundreds have already applied. JobRadar gives you a fighting chance by:
- Fetching jobs posted in the **last 24 hours only**
- Filtering by your target location (Bengaluru in my case)
- Delivering them straight to your inbox before your morning coffee
- Deduplicating so you don't see the same job twice

Basically, it's like having a personal recruiter who works while you sleep.

<img width="380" height="549" alt="Screenshot 2026-05-05 150701" src="https://github.com/user-attachments/assets/3fc6a458-7e63-4554-a972-c8e8d83b5d2b" />


## Tech Stack

- **Java 21** - Because why not use the latest LTS
- **Spring Boot 3.3** - Web, WebFlux, JPA, Mail
- **PostgreSQL** - Render cloud database for persistence
- **TinyFish API** - For LinkedIn job scraping via SSE streaming
- **Maven** - Dependency management
- **Render** - Cloud deployment

## Features

- ✅ Real-time job fetching via Server-Sent Events (SSE)
- ✅ Location-based filtering (Bengaluru/Bangalore)
- ✅ Automatic deduplication by job URL
- ✅ Daily email notifications at 9 AM
- ✅ Manual trigger via REST API
- ✅ PostgreSQL persistence
- ✅ Production-ready error handling
- ✅ Comprehensive logging

## Architecture

```
JobController (REST API)
    ↓
JobOrchestratorService (Business Logic)
    ↓
TinyFishClientService (SSE Streaming) → SSEParserUtil
    ↓
JobFilterUtil (Location + Dedup)
    ↓
EmailService (Gmail SMTP)
```

Clean separation of concerns. Each layer does one thing well.

## Setup

### Prerequisites

- Java 21
- Maven 3.6+
- Gmail account with App Password
- TinyFish API key
- PostgreSQL database (Render or local)

### 1. Clone & Configure

```bash
cd JobRadar
```

Create `.env` file:

```env
# TinyFish API
TINYFISH_API_KEY=your_api_key

# PostgreSQL (Render)
DATABASE_URL=jdbc:postgresql://your-host.render.com:5432/your_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Gmail
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
MAIL_TO=recipient@example.com

# Optional
PORT=8080
SCHEDULER_ENABLED=true
```

### 2. Gmail App Password

1. Enable 2FA on your Google account
2. Generate App Password: https://myaccount.google.com/apppasswords
3. Use that password in `MAIL_PASSWORD`

### 3. PostgreSQL Setup (Render)

1. Create PostgreSQL database on Render
2. Copy the **External Database URL**
3. Convert `postgresql://user:pass@host/db` to `jdbc:postgresql://host:5432/db`
4. Add credentials to `.env`

### 4. Build & Run

```bash
mvn clean package
java -jar target/jobradar-0.0.1-SNAPSHOT.jar
```

Or:

```bash
mvn spring-boot:run
```

## API Endpoints

### Manual Job Fetch

```bash
POST /api/jobs/run-now
```

Triggers the job pipeline manually. Useful for testing or when you can't wait for the 9 AM cron.

```bash
curl -X POST http://localhost:8080/api/jobs/run-now
```

**Response:**
```json
{
  "status": "success",
  "message": "Job fetch pipeline executed successfully. Check logs for details."
}
```

### Health Check

```bash
GET /actuator/health
```

## Email Format

You'll get something like this every morning:

```
Found 3 Java Backend Job(s) in Bengaluru:

================================================================================

1. Senior Java Developer @ Tech Corp
   Location: Bengaluru, Karnataka, India
   Experience: 3-5 years
   Salary: 15-20 LPA
   Skills: Java, Spring Boot, Microservices
   Link: https://linkedin.com/jobs/123

2. Backend Engineer @ StartupXYZ
   Location: Bangalore
   Experience: 2-4 years
   Link: https://linkedin.com/jobs/456

================================================================================

This is an automated email from JobRadar.
```

## Scheduled Execution

Runs daily at **9:00 AM** (configurable in `application.yaml`).

To disable:
```env
SCHEDULER_ENABLED=false
```

## Project Structure

```
com.dev.jobradar
├── controller/          # REST API endpoints
├── client/              # TinyFish API integration
├── service/             # Business logic (orchestration, email)
├── model/               # DTOs
├── repository/          # JPA repositories
├── scheduler/           # Cron jobs
├── config/              # WebClient, beans
├── util/                # SSE parsing, filtering
└── JobradarApplication.java
```

## Deployment (Render)

### 1. Push to GitHub

```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin <your-repo-url>
git push -u origin main
```

### 2. Deploy on Render

1. Create **PostgreSQL** database first
2. Create **Web Service** from GitHub repo
3. Configure:
   - **Build Command:** `mvn clean package`
   - **Start Command:** `java -jar target/jobradar-0.0.1-SNAPSHOT.jar`
4. Add Environment Variables:
   - `TINYFISH_API_KEY`
   - `DATABASE_URL` (from PostgreSQL, convert to JDBC format)
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`
   - `MAIL_TO`
   - `PORT` (Render provides this)

## Configuration

All settings in `src/main/resources/application.yaml`:

- **Database:** PostgreSQL connection, Hibernate settings
- **TinyFish API:** Base URL, timeout, LinkedIn search URL
- **Email:** SMTP settings, recipient, subject
- **Scheduler:** Cron expression, enable/disable
- **Logging:** Log levels

## Troubleshooting

**Email not sending?**
- Verify Gmail App Password
- Check 2FA is enabled
- Review logs for SMTP errors

**No jobs fetched?**
- Verify TinyFish API key
- Check API timeout settings
- Review TinyFish logs

**Database connection failed?**
- Ensure `DATABASE_URL` starts with `jdbc:postgresql://`
- Verify credentials are correct
- Check Render database is active

**Scheduler not running?**
- Ensure `SCHEDULER_ENABLED=true`
- Verify cron expression syntax
- Check logs for scheduler initialization

## What I Learned

- **SSE Streaming:** Handling Server-Sent Events in Spring WebFlux
- **Async Processing:** Non-blocking I/O for API calls
- **Email Integration:** SMTP configuration and error handling
- **Cloud Deployment:** Render PostgreSQL + Web Service setup
- **Cron Jobs:** Spring's @Scheduled annotation
- **Environment Management:** .env files with spring-dotenv

## Future Improvements

- [ ] Telegram/Slack notifications
- [ ] Multiple job boards (Naukri, Indeed)

## License

MIT - Do whatever you want with it.

---

Built out of frustration with the job market. If this helps you land a job, let me know!

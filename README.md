# JobRadar - Java Backend Job Aggregator

A production-ready Spring Boot application that fetches Java backend jobs from LinkedIn using TinyFish API, filters them by location (Bengaluru/Bangalore), and sends daily email notifications.

## 🚀 Features

- ✅ Fetches jobs from LinkedIn via TinyFish API
- ✅ Processes Server-Sent Events (SSE) streaming
- ✅ Filters jobs by Bengaluru/Bangalore location
- ✅ Deduplicates jobs by URL
- ✅ Sends daily email with job listings
- ✅ Manual trigger via REST API
- ✅ Scheduled daily execution at 9 AM
- ✅ Production-ready error handling
- ✅ Comprehensive logging

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- Gmail account with App Password
- TinyFish API key

## 🔧 Setup

### 1. Clone the repository

```bash
cd JobRadar
```

### 2. Configure Environment Variables

Create a `.env` file in the project root (use `.env.example` as template):

```env
TINYFISH_API_KEY=your_tinyfish_api_key_here
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
MAIL_TO=recipient@example.com
PORT=8080
SCHEDULER_ENABLED=true
```

### 3. Gmail App Password Setup

1. Go to Google Account Settings
2. Enable 2-Factor Authentication
3. Generate App Password: https://myaccount.google.com/apppasswords
4. Use the generated password in `MAIL_PASSWORD`

### 4. Build the Application

```bash
mvn clean package
```

### 5. Run the Application

```bash
java -jar target/jobradar-0.0.1-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

## 🌐 API Endpoints

### Manual Job Fetch

**Endpoint:** `POST /api/jobs/run-now`

**Description:** Manually triggers the job fetch pipeline

**Example:**
```bash
curl -X POST http://localhost:8080/api/jobs/run-now
```

**Success Response:**
```json
{
  "status": "success",
  "message": "Job fetch pipeline executed successfully. Check logs for details."
}
```

**Error Response:**
```json
{
  "status": "error",
  "message": "Job fetch pipeline failed: <error details>"
}
```

### Health Check

**Endpoint:** `GET /actuator/health`

**Example:**
```bash
curl http://localhost:8080/actuator/health
```

## ⏰ Scheduled Execution

The application automatically runs daily at **9:00 AM** (configurable via `scheduler.cron` in `application.yaml`).

To disable scheduled execution:
```env
SCHEDULER_ENABLED=false
```

## 📧 Email Format

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

## 🏗️ Architecture

```
Controller (REST API)
    ↓
JobOrchestratorService
    ↓
TinyFishClientService → SSEParserUtil
    ↓
JobFilterUtil (Location + Dedup)
    ↓
EmailService
```

## 📦 Project Structure

```
com.dev.jobradar
├── controller/
│   └── JobController.java
├── client/
│   └── TinyFishClientService.java
├── service/
│   ├── JobOrchestratorService.java
│   └── EmailService.java
├── model/
│   └── JobDTO.java
├── scheduler/
│   └── JobScheduler.java
├── config/
│   └── WebClientConfig.java
├── util/
│   ├── SSEParserUtil.java
│   └── JobFilterUtil.java
└── JobradarApplication.java
```

## 🔍 Logging

Application logs include:
- SSE event processing
- Job extraction count
- Filter results
- Email sending status
- Error details

Log level can be adjusted in `application.yaml`:
```yaml
logging:
  level:
    com.dev.jobradar: INFO
```

## 🚢 Deployment (Render)

### 1. Push to GitHub

```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin <your-repo-url>
git push -u origin main
```

### 2. Deploy on Render

1. Create new Web Service on Render
2. Connect your GitHub repository
3. Configure:
   - **Build Command:** `mvn clean package`
   - **Start Command:** `java -jar target/jobradar-0.0.1-SNAPSHOT.jar`
4. Add Environment Variables:
   - `TINYFISH_API_KEY`
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`
   - `MAIL_TO`
   - `PORT` (Render provides this automatically)

## 🧪 Testing

### Test Manual Trigger
```bash
curl -X POST http://localhost:8080/api/jobs/run-now
```

### Check Logs
Monitor application logs for:
- Job fetch status
- Filter results
- Email sending confirmation

### Verify Email
Check recipient inbox for job listings email.

## ⚙️ Configuration

All configuration is in `src/main/resources/application.yaml`:

- **TinyFish API:** Base URL, timeout, LinkedIn search URL
- **Email:** SMTP settings, recipient, subject
- **Scheduler:** Cron expression, enable/disable
- **Server:** Port configuration

## 🛠️ Troubleshooting

### Email Not Sending
- Verify Gmail App Password is correct
- Check 2FA is enabled on Gmail account
- Review logs for SMTP errors

### No Jobs Fetched
- Verify TinyFish API key is valid
- Check API timeout settings
- Review TinyFish API logs

### Scheduler Not Running
- Ensure `SCHEDULER_ENABLED=true`
- Verify `@EnableScheduling` is present in main class
- Check cron expression syntax

## 📝 License

This project is for educational purposes.

## 👨‍💻 Author

Built with ❤️ using Spring Boot

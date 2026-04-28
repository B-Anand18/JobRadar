# 🚀 Quick Start Guide

## Step 1: Set Up Environment Variables

Copy `.env.example` to `.env` and fill in your credentials:

```bash
TINYFISH_API_KEY=your_actual_api_key
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
MAIL_TO=recipient@example.com
```

## Step 2: Build the Application

```bash
mvn clean install
```

## Step 3: Run the Application

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/jobradar-0.0.1-SNAPSHOT.jar
```

## Step 4: Test Manual Trigger

Open a new terminal and run:

```bash
curl -X POST http://localhost:8080/api/jobs/run-now
```

## Step 5: Check Logs

Watch the console output for:
- ✅ "Starting job fetch from TinyFish API"
- ✅ "COMPLETE event received"
- ✅ "Extracted X jobs from COMPLETE event"
- ✅ "Filtered X jobs by location"
- ✅ "Successfully sent email with X jobs"

## Step 6: Verify Email

Check the recipient's inbox for the job listings email.

## 📊 Expected Flow

```
1. API Call → TinyFish SSE Stream
2. Parse Events → Extract COMPLETE event
3. Filter Jobs → Bengaluru/Bangalore only
4. Deduplicate → Remove duplicate URLs
5. Send Email → Plain text format
```

## 🐛 Troubleshooting

### Build Fails
```bash
mvn clean install -U
```

### Port Already in Use
Change port in `.env`:
```
PORT=8081
```

### Email Not Sending
- Verify Gmail App Password (not regular password)
- Enable 2FA on Gmail account
- Check SMTP logs in console

### No Jobs Found
- Verify TinyFish API key is valid
- Check if LinkedIn URL is accessible
- Review TinyFish API response in logs

## 🎯 Testing Checklist

- [ ] Application starts without errors
- [ ] Manual trigger endpoint responds
- [ ] Jobs are fetched from TinyFish API
- [ ] Jobs are filtered by location
- [ ] Email is sent successfully
- [ ] Email contains job listings
- [ ] Scheduler is configured (check logs at 9 AM)

## 📝 Notes

- First run may take longer due to Maven dependencies
- Scheduler runs daily at 9:00 AM (can be disabled)
- All operations are logged for debugging
- Application is production-ready for deployment

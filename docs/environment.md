# Environment Configuration Guide (Local Profile)

This document describes how to configure environment-specific settings for running the **Moneyowl APIs** application locally using Spring Boot profiles.

---

## Overview

Moneyowl uses **Spring Profiles** to separate configuration by environment (e.g. `local`, `dev`).  
For local development, configuration is defined in `application-local.yaml` and activated using the `local` profile.

---

## 1. Create the Local Configuration File

Create a new configuration file named `application-local.yaml` at the following location: src/main/resources

## 2. Bootstrap from the Base Configuration

1. Open the existing `application.yaml`.
2. Copy **all contents** from `application.yaml`.
3. Paste them into `application-local.yaml`.

This ensures consistency across environments while allowing local overrides.

---

## 3. Activate the Local Spring Profile

Add the following configuration **at the root level** of `application-local.yaml`:

```yaml
spring:
  config:
    activate:
      on-profile: local
```
## 4. Replace Placeholder Values with Real Credentials

```text
| Key               | Description                        |
| ----------------- | ---------------------------------- |
| `JWT_SECRET_KEY`  | Secret key used to sign JWT tokens |
| `DB_URL`          | JDBC URL for PostgreSQL            |
| `DB_USERNAME`     | Database username                  |
| `DB_PASSWORD`     | Database password                  |
| `GROQ_API_KEY`    | API key for Groq LLM               |
| `GROQ_MODEL_NAME` | Selected Groq model name           |
| `SMTP_USERNAME`   | Email address used to send emails  |
| `SMTP_PASSWORD`   | App-specific email password        |

```
## 4. Database Configuration
PostgreSQL JDBC URL Format is

```
jdbc:postgresql://DB_HOST:PORT/DB_NAME
```
Ensure the database server is running before starting the application.

## 6. Groq AI Configuration
Obtain a GROQ API Key from the Groq platform.
Select any supported model available in your Groq account.
Configure the key and model name in application-local.yaml.

## 7. Email (SMTP) Configuration
Moneyowl uses Gmail SMTP for email delivery.
SMTP Username - Any valid Gmail email address.
This address will appear as the email sender.

SMTP Password - Must be an [App Password](https://myaccount.google.com/apppasswords
), not your Gmail account password.

## 8. Security Best Practices
Never commit real credentials to version control.
Add application-local.yaml to .gitignore.
Rotate API keys and passwords periodically.
Use environment variables or secrets managers in non-local environments.








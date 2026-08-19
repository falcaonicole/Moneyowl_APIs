# Database Setup Guide (MongoDB)

This document describes how to install, configure, and initialize the **MongoDB database** required for running the *
*Moneyowl APIs** application locally.

The application uses **MongoDB** as its primary NoSQL database and expects the required database and collections to be
pre-created before startup.

---

## Overview

* Database Engine: **MongoDB Community Server**
* Management Tool: **MongoDB Compass**
* Default Port: **27017**

---

## Prerequisites

Before proceeding, ensure the following are available:

* MongoDB Community Server
* MongoDB Compass

---

# Database Setup on macOS

## 1. Install MongoDB Community Server

Install MongoDB using Homebrew.

```bash
brew tap mongodb/brew

brew install mongodb-community
```

Verify the installation:

```bash
mongod --version
```

Expected output:

```text
db version vX.X.X
```

---

## 2. Start MongoDB Server

Start MongoDB as a background service.

```bash
brew services start mongodb-community
```

Verify the service status.

```bash
brew services list
```

Expected:

```text
mongodb-community started
```

MongoDB will be accessible on:

```text
localhost:27017
```

---

## 3. Install MongoDB Compass

Download and install MongoDB Compass from the official MongoDB website.
https://www.mongodb.com/try/download/compass

Launch MongoDB Compass after installation.

---

## 4. Connect to MongoDB

Open MongoDB Compass.

Create a new connection using:

```text
mongodb://localhost:27017
```

Click **Connect**.

---

## 5. Create an Administrative User

Open the **MONGOSH** tab inside Compass.

Execute:

```javascript
use admin
```

```javascript
db.createUser(
{
   user: "admin",
   pwd: "AdminPassword123!",
   roles: [
      { role: "userAdminAnyDatabase", db: "admin" },
      { role: "readWriteAnyDatabase", db: "admin" },
      { role: "dbAdminAnyDatabase", db: "admin" }
   ]
})
```

---

## 6. Enable Authentication

Open the MongoDB configuration file.

```bash
nano /opt/homebrew/etc/mongod.conf
```

Add or update:

```yaml
security:
  authorization: enabled
```

To Save the File press

CTRL + O

Enter

CTRL + X

Restart MongoDB.

```bash
brew services restart mongodb-community
```

---

## 7. Connect Using Authentication

Open MongoDB Compass.

Edit the connection that we created earlier:

**Hostname**

```text
localhost
```

**Port**

```text
27017
```

**Authentication Method**

```text
Username / Password
```

**Username**

```text
admin
```

**Password**

```text
${ANY_PASSWORD}
```

**Authentication Database**

```text
admin
```

Click **Connect**.

Alternatively, use the following connection string:

```text
mongodb://${USERNAME}:${PASSWORD}@localhost:27017/?authSource=admin
```

---

## 8. Create the Application Database

Within MongoDB Compass:

Select **Create Database**.

Provide:

**Database Name**

```text
AssetData
```

**Collection Name**

```text
user_portfolio_setu_response
user_portfolio
```

Click **Create Database**.

---

## 9. Test Application Connectivity

Use the following connection string:

```text
mongodb://${USERNAME}:${PASSWORD}@localhost:27017/?authSource=admin
```

Successful connection confirms that authentication has been configured correctly.

---

# Database Setup on Windows

## 1. Install MongoDB Community Server

Download the MongoDB Community Server installer from the official MongoDB website.

Run the installer and select:

* MongoDB Server
* MongoDB Compass
* Install MongoDB as a Service

Keep the default settings unless otherwise required:

| Setting      | Value   |
|--------------|---------|
| Port         | 27017   |
| Service Name | MongoDB |

Complete the installation.

---

## 2. Verify MongoDB Service

Open:

```text
services.msc
```

Locate:

```text
MongoDB
```

Ensure the service status is:

```text
Running
```

If necessary, start the service manually.

---

## 3. Open MongoDB Compass

Launch MongoDB Compass.

Connect using:

```text
mongodb://localhost:27017
```

---

## 4. Create Administrative User

Open the **MONGOSH** tab.

Execute:

```javascript
use admin

db.createUser(
{
   user: "admin",
   pwd: "${PASSWORD}",
   roles: [
      { role: "userAdminAnyDatabase", db: "admin" },
      { role: "readWriteAnyDatabase", db: "admin" },
      { role: "dbAdminAnyDatabase", db: "admin" }
   ]
})
```

---

## 5. Enable Authentication

Edit the MongoDB configuration file.

Typical location:

```text
C:\Program Files\MongoDB\Server\<version>\bin\mongod.cfg
```

Add:

```yaml
security:
  authorization: enabled
```

Restart the MongoDB service.

---

## 6. Create Application Database

Open MongoDB Compass.

Select **Create Database**.

Enter:

```text
AssetData
```

Initial Collection:

```text
user_portfolio_setu_response
user_portfolio
```

Click **Create Database**.

---

## 7. Test Application Connectivity

Use the following connection string:

```text
mongodb://${USERNAME}:${PASSWORD}@localhost:27017/?authSource=admin
```

Successful connection confirms that authentication has been configured correctly.
---

# Validation Checklist

After setup, verify the following:

* MongoDB service is running
* Authentication is enabled
* Administrative user exists
* Application user exists
* `AssetData` database is created
* Required collections are present
* MongoDB Compass can connect successfully
* The application can connect using the configured credentials

---

# Database Setup Guide (PostgreSQL)

This document describes how to install, configure, and initialize the **PostgreSQL database** required for running the **Moneyowl APIs** application locally.

The application uses **PostgreSQL** as its primary relational database and expects the schema to be pre-initialized before startup.

---

## Overview

- Database Engine: **PostgreSQL**
- Management Tool: **pgAdmin 4**

---

## Prerequisites

Before proceeding, ensure the following are installed:

- PostgreSQL Server 
- pgAdmin 4
- Access to the project’s DDL.sql and DML.sql file at docs/database/ folder

---

## Database Setup on macOS

### 1. Install PostgreSQL Server

Install PostgreSQL using the official [Postgres App](https://postgresapp.com):

- Download from the official PostgreSQL website
- Complete the installation and note:
    - PostgreSQL port (default: `5432`)
    - Superuser username (default: `postgres`)
    - Password set during installation

---

### 2. Install pgAdmin 4

- Download and install **pgAdmin 4** from the official [pgAdmin website](https://www.pgadmin.org/download/pgadmin-4-macos/)
- Launch pgAdmin after installation

---

### 3. Start PostgreSQL Server

1. Open the **Postgres App**
2. Start the PostgreSQL server
3. Verify the server is running on port `5432`

---

### 4. Create a Database

1. Open **pgAdmin 4**
2. Connect to the PostgreSQL server
3. Right-click on **Databases**
4. Select **Create → Database**
5. Enter:
    - Database Name: *(any name, e.g. `moneyowl`)*
6. Click **Save**

---

### 5. Initialize Schema and Data

1. Open the **Query Tool** in pgAdmin
2. Load the provided `DDL.sql` and `DML.sql` file
3. Execute the script

This will:
- Create all required schemas
- Create tables and constraints
- Insert initial/reference data

---

## Database Setup on Windows

### 1. Install PostgreSQL Server

1. Download the PostgreSQL installer for Windows from the official [PostgreSQL website](https://www.postgresql.org/download/windows/)
2. Run the installer and follow the setup wizard
3. During installation:
    - Select PostgreSQL Server and pgAdmin 4
    - Set a password for the `postgres` user
    - Keep the default port (`5432`) unless required otherwise
4. Complete the installation

---

### 2. Verify PostgreSQL Service

1. Open **Services** (`services.msc`)
2. Locate **PostgreSQL**
3. Ensure the service status is **Running**
4. If not running, start the service manually

---

### 3. Open pgAdmin 4

1. Launch **pgAdmin 4**
2. Connect to the PostgreSQL server using:
    - Host: `localhost`
    - Port: `5432`
    - Username: `postgres`
    - Password: *(set during installation)*

---

### 4. Create a Database

1. Right-click on **Databases**
2. Select **Create → Database**
3. Provide:
    - Database Name: *(any name, e.g. `moneyowl`)*
    - Owner: `postgres`
4. Click **Save**

---

### 5. Initialize Schema and Data

1. Select the newly created database
2. Open **Query Tool**
3. Load the `DDL.sql` and `DML.sql` file
4. Execute the script

Ensure the script completes without errors.

---

## Validation Checklist

After setup, verify the following:

- PostgreSQL server is running
- Database exists and is accessible
- Tables and schemas are created
- Initial data is present (if applicable)
- Application can connect using the configured credentials

---


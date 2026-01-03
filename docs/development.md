***Local Development Setup — Moneyowl APIs***

This document describes how to set up and run the Moneyowl APIs locally for development purposes.

***System Requirements***

Ensure the following software is installed on your machine before proceeding:

1. JDK 21                  [MacOS](https://www.oracle.com/in/java/technologies/downloads/#jdk21-mac)       [Windows](https://www.oracle.com/in/java/technologies/downloads/#jdk21-windows)
2. IntelliJ IDEA CE        [MacOS](https://www.jetbrains.com/idea/download/?section=mac)       [Windows](https://www.jetbrains.com/idea/download/?section=windows)
3. [Postman](https://www.postman.com/downloads/)                 
4. PgAdmin4 GUI v9.9       [MacOS](https://www.pgadmin.org/download/pgadmin-4-macos/)
5. PostgresSQL Server      [MacOS](https://postgresapp.com)       [Windows](https://www.postgresql.org/download/windows/)

Note: Make sure JAVA_HOME is correctly configured and points to JDK 21.

***Clone the Repository in IntelliJ IDE***

File --> New --> Project from Version Control and paste this url
````
https://github.com/falcaonicole/Moneyowl_APIs.git
````

***Configure the Application Run Configuration***

In IntelliJ IDEA:
Open Run → Edit Configurations → Add a new Application configuration

Set the following values:

Under Build and Run Select 
- JDK 21 as Runner
- main class - com.finance.moneyowl.MoneyowlApplication
- Environment variables - spring.profiles.active=local

Click Apply and OK

***Setup Github credentials on your IDE***
- Create a Personal Access Token (PAT) in GitHub
- When IntelliJ prompts for GitHub authentication, use the PAT instead of your password
  This is required for repository access and dependency resolution.

***Setup the Environment Variables described in docs/environment.md and Database in docs/database.md***

Ensure:
PostgreSQL is running.
Database credentials match the local configuration.
Required schemas and tables exist.

***Once Environment and Database is setup. Clean build the App by running following command in the Maven goals***
````
mvn clean install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.alowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true
````

***Run the App - App starts on port 8080***

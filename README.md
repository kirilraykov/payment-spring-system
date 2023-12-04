# E-merchant Payment System Overview
This monorepo project is providing an example Payment system related to Merchants and Payment(transactions).
It's consisted of two projects —
Java 17/Spring Application as Backend system and JS(React) application as Front-end system.
For Data storage it's using free MySQL service applicable for example projects.
The project also implements custom Exception handler,
DB schema migrations, Jwt Authentication Security and background jobs for keeping clean state of data.

The application is containerized with Docker for easy deployment and managed with Maven for efficient project handling.

## Prerequisites
* Git
* Java 17 recommended (Any version after 11 may be used)
* Maven 3.6 or later
* Docker (depending on the build strategy)
* npm (for running the front-end)

## Setting Up the Development Environment
1. **Clone the Repository:**
```bash
   git clone https://github.com/your-repository/e-merchant-app.git
   cd e-merchant-app
  ```
2. **Back-End Setup:**
* Navigate to the back-end directory:
```bash
   cd e-merchant-service
```
* Build the project with Maven:
```bash
   mvn clean install
```
* Run the Spring Boot application:
```bash
   mvn spring-boot:run
```

3. **Front-End Setup:**
* Navigate to the front-end directory:
```bash
   cd e-merchant-ui
```
* Install dependencies:
```bash
   npm install
```
* Start React application:
```bash
   npm start
```

4. **Using Docker:**
* Build and run the application using Docker Compose:
```bash
   docker-compose up --build
```

### Accessing the Application
- The front-end application will be accessible at http://localhost:3000.
- The back-end APIs can be accessed via http://localhost:8080.

### Documentation
- API Documentation available at: http://localhost:8080/swagger-ui/index.html
- For more details, examples and demo of the working project, please see `docs` directory.

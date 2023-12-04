### Features:
- Inheritance and polymorphic associations for models, as well as input validations.
- Different user types - Merchant and Admin, where Admin is allowed to Import list of users(including both admins and merchants) and see all transactions for any user. Merchants are not able to use the import CSV feature and can only see their transactions.
- Background Scheduled job for cleaning transactions older than 1 hour
- When creating transaction, the dependencies are checked and verified (Authorize Transaction -> Charge Transaction -> Refund Transaction)
- Custom Exception handler which accepts Validation and Custom exceptions to provide user-friendly error handling
- DB Migrations using Flyway (for deploying/running on new environments)
- Ability to import list of users from CSV file
- JWT Authentication and security using Spring Security. Only the login and signup endpoints are freely accessible.
- Dockerized build process where each project (BE and FE) contain dockerfile and common docker compose file.
- .gitignore for each project.
- Unit test coverage.
- React JS example UI.

### Potential Improvements
As this is an example project, it is not Production-quality.
As the time for the development took couple of days, some features are missing some important changes.
I'll provide examples below:
1. Unit Tests - the coverage is not 100% because of time limitations. Some of the more important controllers and services are tested, but tests are still missing for some features.
2. Integration tests - as this is monorepo project, I could've implemented separate suite of functional integration tests using RestAssured,TestNG,Selenium,Gatling
3. Configurations - there is a single application.prop file, but in production scenario we will have different scopes and possibly Spring Cloud Config service for centralization of configurations.
4. Corner case coverage - there are some features which would require more validations and special case coverage. Example: CSV import currently works for valid lists, but if some of the fields are missing or incorrect, it WON"T create error data, but it will fail silently.
5. Security - even though Spring Security with JWT is implemented, in a production scenario, the implementation would be different.

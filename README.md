# Online Book Library App

The Online Book Library App is an advanced and well-architected online book library application constructed using Spring Boot. This multi-module Gradle project is designed with future scalability in mind. If any module is to be considered as a separate microservice, the transition can be smooth and effortless, owing to the loosely coupled nature of the modules. Adhering to best practices, the application uses the latest libraries and is written in clean, maintainable code.

For optimal performance, the application utilizes MySQL as its primary database and Redis for distributed caching. However, the application remains database agnostic, offering flexibility to operate with an in-memory cache instead of Redis if preferred – a feature configurable via properties. From a security standpoint, the application leverages the JWT mechanism to ensure stateless authentication and authorization. Also using JIB and Jacoco plugins to be easily integrated with CI/CD.

Overall, the Online Book Library App stands as a testament to modularity and flexibility. It's built in a way that transitioning from a multi-module monolith to a microservice architecture and deploying as a distributed system can be achieved seamlessly.

## Modules:

### 2. module-app
- **Description**: App running module. Holding also application.properties file.

### 2. module-book
- **Description**: The module-book is responsible for handling all operations related to book data, including management of the books inventory. It provides endpoints to both manage and simply retrieve book data as a regular user. Additionally, it offers robust pagination and search functionalities for easier user experience.

### 3. module-user-security
- **Description**: The module-user-security is designed to manage user data and ensure the security of all application endpoints. It provides essential endpoints and functionalities to handle user profiles, roles, permissions, and authentication processes. As auth mechanism it uses JWT. This module plays a crucial role in maintaining the integrity and privacy of user data while ensuring restricted access to specific application features based on user roles and permissions.

### 4. module-purchase
- **Description**: The module-purchase offers endpoints to manage (for admin users) and perform purchases of specific books. Users can view their purchase history, while administrators can oversee and manage all purchase activities in the system.

### 5. module-recommendation
- **Description**: The module-recommendation is built to offer personalized book recommendations to users based on their purchase history. The recommendation algorithms consider various factors, including books of the same author, genre, or a combination of both. This ensures that users receive diverse and relevant book suggestions, enhancing their reading experience.

### 6. module-report
- **Description**: The module-report provides endpoints for generating various report, including total book inventory, total user purchases, and more. These reports are presented in a PDF format.

## Deploying and Running the App

The application can be run in multiple ways, depending on the development or production needs. Below are the detailed instructions for each method:

### 1. Running from Gradle or IntelliJ IDEA:
- If you're looking to run the application during development or for testing purposes, you can utilize following methods -

    - **Gradle**:
      Use the following command in the terminal from the root of the project:
      ```
      ./gradlew bootRun
      ```

    - **IntelliJ IDEA**:
      You can also run the application directly from IntelliJ IDEA. In this case, consider using the in-memory database and in-memory cache for a smoother experience. This can be achieved by modifying the `application.properties` file accordingly.

    - If you prefer in memory db and cache set "cache.type=caffeine" and spring.h2.console.enabled=true (uncomment h2 datasource).

### 2. Running using Docker:
- For a production-like environment or to ensure that the application works in a containerized setup along with its dependencies (MySQL and Redis), you can use Docker.

  First, ensure that Docker and Docker Compose are installed on your machine.

    - From the root of the project, execute the following command in the terminal:
      ```
      ./gradlew deployAndRun -Penv=dev
      ```

      This command will build the Docker images for the application, MySQL, and Redis. It will then deploy and run these images using Docker Compose.
    - Be sure mysql datasource is uncomment and cache.type=redis with redis credentials

Application uses port 8080 by default and can be accesses by http://localhost:8080
It uses simple index page, login page and the link to swagger UI
There are by default created 1 admin user and 1 superadmin user apart from other fake users, which credentials can be found and configured in application.properties(module-app)

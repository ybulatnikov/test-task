# test-task

---

To run application locally in Docker next commands should be run:

      mvn clean package

      docker-compose build

      docker-compose up

Then GET request to http://localhost:8080/users can be made.

Also http://localhost:8080/swagger-ui/index.html and http://localhost:8080/v3/api-docs are accessible.

---

The integration test `com.example.demo.service.UserServiceIntegrationTest` can be run using Maven command:

    mvn clean test

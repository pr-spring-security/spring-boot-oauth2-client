# spring-boot-oauth2-client

Oauth2 Resource Server with mongodb and swagger2.

The Oauth2 Authorization Server is [here]()
This application imploded the framework for apis named swagger2.The database uses mongodb.
You can change property named `spring.data.mongodb.uri` in `application.properties` to 
your own develop environment.

Check if swagger2 is OK:
[http://localhost:8080/v2/api-docs](http://localhost:8080/v2/api-docs)

Visit api documentation:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
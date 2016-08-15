# spring-boot-oauth2-client

Oauth2 Resource Server with mongodb, postgresql and swagger2.

The Oauth2 Authorization Server is [here](https://github.com/jeesun/spring-boot-oauth2-server)
This application has imploded the framework for apis named swagger2.The database uses mongodb and postgresql.
<br />
1. Postgresql saves the oauth information,since the data structure of oauth is assured.
<br />You can get the data in postgresql by JdbcTemplate.
<br />
2. MongoDB saves information except oauth's and user's information.
You can change properties in `application.properties` to your own develop environment.

Check if swagger2 is OK:
[http://localhost:8080/v2/api-docs](http://localhost:8080/v2/api-docs)

Visit api documentation:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
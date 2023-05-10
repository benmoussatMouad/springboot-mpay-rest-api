# Steps to Run Spring Boot Blog App
## 1. Maven Build the Project
If you have installed Maven on your machine then use the below command:
```
mvn clean package
```
If you haven't insatlled Maven on your machine then use below command:
```
./mvnw clean package
 ```
 Note: Go to root directory of the project and execute above command.
 ## 2. Create a Database
 Before running Spring boot  application, you need to create the PostgreSQL database.
 
 Use the below SQL database to create the MySQL database:
 ```sql
 create database mpay_test
 ```
 Database name - myblog
 ## 3. Run Spring Boot Project
 Use below command to run Spring boot application:
 ```
 mvn spring-boot:run
 ```
 Once you run Spring boot application, Hibernate will create the database tables autimatically.
 ## 4. Insert Data
User below Insert SQL statements to insert records into roles table:
```sql
INSERT INTO `mpay_test.roles` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER');
```
Now, Spring boot application is ready to use.

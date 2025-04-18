# Online-Examination-System
An API-based backend system that allows admins to manage exams, students, and questions. Students can register, log in, take exams, and view results.

# Modules & Features

### 1. User Authentication

Student & Admin roles

JWT-based login system (optional but recommended)


### 2. Admin Functionalities

Create/edit/delete exams

Add/edit/delete questions

View student performance


### 3. Student Functionalities

Register/login

View available exams

Take timed exams

Auto-graded results



---

# Technology Stack

Java 17

Spring Boot 3

Spring Data JPA (Hibernate)

MySQL

Lombok

Spring Security (JWT Authentication) [Optional]





# Project Structure

```
src/
├── main/
│   ├── java/com/onlineexam/
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── OnlineExamApplication.java
│   └── resources/
│       ├── application.properties
│       └── data.sql (optional test data)

```

# Entities

### User
```
@Entity

 public class User {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String role; // STUDENT or ADMIN
 }
```
### Exam
```
@Entity
public class Exam {
    @Id @GeneratedValue
    private Long id;
    private String title;
    private int durationMinutes;
    private LocalDateTime startTime;
}
```
### Question
```
@Entity
public class Question {
    @Id @GeneratedValue
    private Long id;
    private String text;
    private String optionA, optionB, optionC, optionD;
    private String correctAnswer;

    @ManyToOne
    private Exam exam;
}
```
### Submission
```
@Entity
public class Submission {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User student;

    @ManyToOne
    private Exam exam;

    private int score;
    private LocalDateTime submittedAt;
}
```

---

Endpoints Example


---

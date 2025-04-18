1. Project Setup

Dependencies to include in pom.xml:

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Optional: For security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>


---

2. application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/exam_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
server.port=8080


---

3. Main Class

package com.onlineexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineExamApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineExamApplication.class, args);
    }
}


---

4. Entity: User

package com.onlineexam.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role; // ADMIN, STUDENT
}


---

5. Entity: Exam

package com.onlineexam.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int durationMinutes;
    private LocalDateTime startTime;
}


---

6. Entity: Question

package com.onlineexam.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    @ManyToOne
    private Exam exam;
}


---

7. Entity: Submission

package com.onlineexam.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User student;

    @ManyToOne
    private Exam exam;

    private int score;
    private LocalDateTime submittedAt;
}


---

8. Repositories

package com.onlineexam.repository;

import com.onlineexam.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

public interface ExamRepository extends JpaRepository<Exam, Long> {}

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExamId(Long examId);
}

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudentId(Long studentId);
}


---

9. Services

Example: ExamService

package com.onlineexam.service;

import com.onlineexam.model.Exam;
import com.onlineexam.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {
    private final ExamRepository examRepo;

    public ExamService(ExamRepository examRepo) {
        this.examRepo = examRepo;
    }

    public List<Exam> getAllExams() {
        return examRepo.findAll();
    }

    public Exam createExam(Exam exam) {
        return examRepo.save(exam);
    }
}


---

10. Controllers

ExamController

package com.onlineexam.controller;

import com.onlineexam.model.Exam;
import com.onlineexam.service.ExamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public List<Exam> getAllExams() {
        return examService.getAllExams();
    }

    @PostMapping
    public Exam createExam(@RequestBody Exam exam) {
        return examService.createExam(exam);
    }
}


---

UserController (Simple Registration/Login)

package com.onlineexam.controller;

import com.onlineexam.model.User;
import com.onlineexam.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userRepo.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User u = userRepo.findByUsername(user.getUsername());
        if (u != null && u.getPassword().equals(user.getPassword())) {
            return "Login successful";
        }
        return "Invalid credentials";
    }
}


---

QuestionController

package com.onlineexam.controller;

import com.onlineexam.model.Question;
import com.onlineexam.repository.QuestionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionRepository questionRepo;

    public QuestionController(QuestionRepository questionRepo) {
        this.questionRepo = questionRepo;
    }

    @GetMapping("/exam/{examId}")
    public List<Question> getQuestionsByExam(@PathVariable Long examId) {
        return questionRepo.findByExamId(examId);
    }

    @PostMapping
    public Question addQuestion(@RequestBody Question question) {
        return questionRepo.save(question);
    }
}


---

SubmissionController

package com.onlineexam.controller;

import com.onlineexam.model.*;
import com.onlineexam.repository.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    private final SubmissionRepository submissionRepo;
    private final UserRepository userRepo;
    private final ExamRepository examRepo;
    private final QuestionRepository questionRepo;

    public SubmissionController(SubmissionRepository submissionRepo, UserRepository userRepo,
                                ExamRepository examRepo, QuestionRepository questionRepo) {
        this.submissionRepo = submissionRepo;
        this.userRepo = userRepo;
        this.examRepo = examRepo;
        this.questionRepo = questionRepo;
    }

    @PostMapping("/submit")
    public Submission submitExam(@RequestParam Long userId, @RequestParam Long examId,
                                 @RequestBody List<String> answers) {

        List<Question> questions = questionRepo.findByExamId(examId);
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectAnswer().equalsIgnoreCase(answers.get(i))) {
                score++;
            }
        }

        Submission submission = new Submission();
        submission.setExam(examRepo.findById(examId).orElse(null));
        submission.setStudent(userRepo.findById(userId).orElse(null));
        submission.setScore(score);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(submission);
    }

    @GetMapping("/student/{userId}")
    public List<Submission> getResults(@PathVariable Long userId) {
        return submissionRepo.findByStudentId(userId);
    }
}


---

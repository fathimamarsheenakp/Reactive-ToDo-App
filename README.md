# 📌 Reactive ToDo App

A **Reactive ToDo Application** built with **Java, Spring WebFlux, MongoDB, HTML, CSS, and JavaScript**.  
This project demonstrates **Reactive Programming** with **non-blocking REST APIs**, enabling high performance and scalability.

---

## 🚀 Features
- ➕ Add single or multiple tasks
- 📋 View all tasks
- 🔍 Search tasks by name
- ✏️ Update tasks (restricted if already completed)
- ✅ Mark tasks as completed
- ❌ Delete tasks
- 📊 Filter tasks by status (Completed / Pending)

---

## 🛠️ Tech Stack

**Frontend**
- HTML, CSS, JavaScript

**Backend**
- Java
- Spring Boot
- Spring WebFlux (Reactive, Non-blocking I/O)
- Project Reactor (`Mono`, `Flux`)

**Database**
- MongoDB (Reactive NoSQL database)
- Spring Data Reactive MongoDB

**Tools**
- Maven
- Git & GitHub
- Postman (API testing)

---

## ⚡ API Endpoints

### Task Management APIs

| Method   | Endpoint                 | Description                          | Request Body |
|----------|--------------------------|--------------------------------------|--------------|
| **POST** | `/todo/add`              | Add a new task                       | `TodoRequestDto` |
| **POST** | `/todo/add/bulk`         | Add multiple tasks                   | `Flux<TodoRequestDto>` |
| **GET**  | `/todo`                  | Get all tasks                        | – |
| **GET**  | `/todo/search?name=xyz`  | Search tasks by name (case-insensitive) | – |
| **PUT**  | `/todo/edit/{id}`        | Update a task (if not completed)     | `TodoRequestDto` |
| **PATCH**| `/todo/{id}/complete`    | Mark task as completed               | – |
| **DELETE** | `/todo/delete/{id}`    | Delete a task                        | – |
| **GET**  | `/todo/completed`        | Get all completed tasks              | – |
| **GET**  | `/todo/pending`          | Get all pending tasks                | – |


---

## 📂 Project Structure

        src/
        ├── main/
        │   ├── java/com/sony/todoapp/
        │   │   ├── controller/     # REST Controller (TodoController)
        │   │   ├── service/        # Business Logic (TodoService)
        │   │   ├── repository/     # Reactive Mongo Repositories
        │   │   ├── entity/         # Task Entity
        │   │   └── dto/            # Request/Response DTOs
        │   └── resources/          # Application config
        └── test/
        └── java/com/sony/todoapp/
        ├── controller/     # Unit/Integration tests for TodoController
        └── service/        # Unit tests for TodoService

---

## ▶️ Getting Started

### Prerequisites
- Java 17+
- Maven
- MongoDB

### Steps to Run
1. Clone the repository
   ```bash
   https://github.com/fathimamarsheenakp/Reactive-ToDo-App.git
   cd Reactive-ToDo-App

2. Start MongoDB locally (default port 27017)

3. Run the application
    ```bash
   mvn spring-boot:run

4. Test APIs using Postman or a browser:
    ```bash
   http://localhost:8080/todo

---

## 📖 Learning Highlights

- Built Reactive REST APIs with Spring WebFlux. 
- Hands-on with Mono and Flux for asynchronous programming. 
- Clean Controller-Service-Repository architecture. 
- Used DTOs and Mappers for structured request/response handling. 
- Integrated with MongoDB using Reactive Spring Data.
- Implemented unit and integration tests with JUnit5 and Mockito



# 📖 LosLibros - Book Service
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16%2B-blue.svg)](https://www.postgresql.org/)
[![MapStruct](https://img.shields.io/badge/MapStruct-1.6.3-red.svg)](https://mapstruct.org/)
The **Book Service** manages the book catalogue, inventory metadata, and book cover images for the LosLibros Library Management System.
---
## 🌟 Features
- **Book Catalog Management**: Full CRUD capabilities for books identified by unique ISBNs.
- **Multipart Cover Image Handling**: Uploads, validates (`@ValidImage`), stores, and retrieves cover images (`${user.home}/.ijse/eca/books`).
- **Relational Persistence**: Backed by **PostgreSQL** using Spring Data JPA & Hibernate.
- **Cloud Native**: Integrates with **Spring Cloud Config** for remote configuration and **Eureka** for dynamic service registration.
- **DTO Mapping & Validation**: Automated object mapping via MapStruct and input validation with Bean Validation.
---
## ⚙️ Configuration & Environment
- **Service Name**: `book-service`
- **Port**: Dynamic (`0` - registered to Eureka) or configured via Config Server.
- **Database**: PostgreSQL
    - Default URL: `jdbc:postgresql://localhost:5432/db-loslibros`
    - Default Username: `postgres`
    - Default Password: `psql`
- **File Storage Directory**: `${user.home}/.ijse/eca/books`
---
## 📡 API Endpoints (`/api/v1/books`)
| Method | Endpoint | Content-Type | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/books` | `multipart/form-data` | Creates a new book record with cover image |
| `PUT` | `/api/v1/books/{isbn}` | `multipart/form-data` | Updates an existing book and optionally its cover image |
| `GET` | `/api/v1/books/{isbn}` | `application/json` | Retrieves metadata for a specific book by ISBN |
| `GET` | `/api/v1/books` | `application/json` | Retrieves list of all books |
| `DELETE` | `/api/v1/books/{isbn}` | `application/json` | Deletes a book and associated cover image |
| `GET` | `/api/v1/books/{isbn}/cover`| `image/jpeg` | Streams the JPEG cover image for the specified book |
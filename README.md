# Application for collecting and securely sharing physical activity data

## Overview

The backend service is responsible for managing user authentication, relationships, and securely storing and sharing physical activity data. It provides a RESTful API for communication with the frontend application and ensures data security through authorization mechanisms.

## Features

- Authentication Module: User registration, login (JWT token generation), password reset and updating.
- User Module: Update user profile, change password, retrieve user data, search users, account deletion.
- Relationship Module: Send, accept, or reject friend requests, manage active relationships, view friend list and invitations.
- Key Module: Save and retrieve public keys, remove keys associated with a user.
- Message Module: Create, like, delete messages, retrieve user messages, and delete all messages.

## Technologies Used

- **Programming Language**: Java 17
- **Framework**: Spring Boot 3.3.4
- **Database**: PostgreSQL (SQL) & MongoDB (NoSQL)
- **Security**: Spring Security 6.3.3, JSON Web Tokens (JWT)
- **Communication**: REST API
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit 5, Testcontainers
- **Monitoring**: Spring Boot Actuator

## API Endpoints

### Authentication

- **POST /auth/register** - Register a new user
- **POST /auth/login** - Authenticate user
- **POST /auth/reset-password-request** - Send password reset verification code
- **POST /auth/reset-password** - Request password reset

### User

- **PUT /users** - Update user profile
- **PATCH /users/password** - Change user password
- **GET /users/me** - Get user profile
- **GET /users/{userId}** - Get user profile by ID
- **GET /users/search** - Search users by username
- **DELETE /users** - Delete user account

### Relationships

- **POST /relationships/send/{recipientUserId}** - Send relationship request
- **POST /relationships/accept/{relationshipId}** - Accept relationship request
- **POST /relationships/reject/{relationshipId}** - Reject relationship request
- **DELETE /relationships/delete/{relationshipId}** - Delete relationship
- **GET /relationships/{friendUserId}** - Get active relationship with a friend
- **GET /relationships/accepted** - Get all accepted relationships
- **GET /relationships/received** - Get received relationship requests
- **GET /relationships/sent** - Get sent relationship requests
- **GET /relationships/friends** - Get list of friends with keys

### Key Management

- **POST /keys** - Create public key for user
- **GET /keys/my** - Get user's public key
- **DELETE /keys** - Delete all public keys for user

### News

- **POST /news** - Create news
- **POST /news/reference** - Create reference news
- **PATCH /news/{id}/like** - Like news
- **GET /news/reference** - Get all published news
- **GET /news/received** - Get all received news
- **GET /news/received/{friendUserId}** - Get all received news from a friend
- **DELETE /news/{id}** - Delete news


## Installation and Deployment

### Running with Docker
Prerequisites: Docker & Docker Compose installed

```bash
cd /path/to/project
docker-compose up --build
```

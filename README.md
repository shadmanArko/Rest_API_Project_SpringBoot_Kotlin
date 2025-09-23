# Notes REST API - Spring Boot & MongoDB

[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)](https://jwt.io/)

A secure, production-ready RESTful API for managing personal notes, built with Spring Boot, Kotlin, and MongoDB. Features comprehensive authentication, authorization, and CRUD operations with enterprise-grade security practices.

## 🏗️ Architecture Overview

This application follows a **layered architecture** pattern with clear separation of concerns:

```
┌─────────────────────┐
│   Controller Layer  │ ← REST Endpoints & Request Handling
├─────────────────────┤
│   Security Layer    │ ← JWT Authentication & Authorization  
├─────────────────────┤
│   Service Layer     │ ← Business Logic & Token Management
├─────────────────────┤
│   Repository Layer  │ ← Data Access & MongoDB Integration
├─────────────────────┤
│   Model Layer       │ ← Domain Entities & Data Models
└─────────────────────┘
```

## 🚀 Key Features

### 🔐 Security & Authentication
- **JWT-based Authentication** with access and refresh token strategy
- **BCrypt Password Hashing** for secure password storage
- **Token Refresh Mechanism** with automatic cleanup of expired tokens
- **User Authorization** ensuring users can only access their own notes
- **Stateless Session Management** for horizontal scalability

### 📝 Notes Management
- **Full CRUD Operations** (Create, Read, Update, Delete)
- **User-specific Notes** with ownership validation
- **Color-coded Organization** for visual note categorization
- **Timestamp Tracking** for creation and modification audit trails

### 🎯 Technical Excellence
- **RESTful API Design** following industry best practices
- **MongoDB Integration** with Spring Data repositories
- **Kotlin Coroutines** ready architecture
- **Input Validation** and comprehensive error handling
- **Clean Code Principles** with separation of concerns

## 🛠️ Technology Stack

| Category | Technology | Purpose |
|----------|------------|---------|
| **Language** | Kotlin | Primary development language |
| **Framework** | Spring Boot 3.x | Application framework |
| **Security** | Spring Security | Authentication & authorization |
| **Database** | MongoDB | Document-based data storage |
| **Authentication** | JWT (JSON Web Tokens) | Stateless authentication |
| **Password Security** | BCrypt | Password hashing |
| **Build Tool** | Gradle | Dependency management |

## 📊 Database Schema

### Collections

#### Users Collection
```javascript
{
  "_id": ObjectId,
  "email": String,
  "hashedPassword": String
}
```

#### Notes Collection
```javascript
{
  "_id": ObjectId,
  "title": String,
  "content": String,
  "color": Long,
  "createdAt": ISODate,
  "ownerId": ObjectId
}
```

#### Refresh Tokens Collection
```javascript
{
  "_id": ObjectId,
  "userId": ObjectId,
  "hashedToken": String,
  "expiresAt": ISODate,
  "createdAt": ISODate
}
```

## 🔌 API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

#### User Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

#### Refresh Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

### Notes Endpoints

#### Create/Update Note
```http
POST /note
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "id": "optional-for-updates",
  "title": "My Important Note",
  "content": "This is the content of my note...",
  "color": 16777215
}
```

#### Get User's Notes
```http
GET /note
Authorization: Bearer {accessToken}
```

**Response:**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "title": "My Important Note",
    "content": "This is the content of my note...",
    "color": 16777215,
    "createdAt": "2024-01-15T10:30:00Z"
  }
]
```

#### Delete Note
```http
DELETE /note/{noteId}
Authorization: Bearer {accessToken}
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- MongoDB 4.4+
- Gradle 7.0+

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/notes-rest-api.git
   cd notes-rest-api
   ```

2. **Configure MongoDB**

   Update `application.properties` or `application.yml`:
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/notesdb
   jwt.secret=yourBase64EncodedSecretKey
   ```

3. **Generate JWT Secret**
   ```bash
   # Generate a secure base64 secret key
   openssl rand -base64 64
   ```

4. **Build the application**
   ```bash
   ./gradlew build
   ```

5. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

The API will be available at `http://localhost:8085`

### Docker Deployment

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-alpine
COPY build/libs/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
# Build and run with Docker
docker build -t notes-api .
docker run -p 8085:8085 notes-api
```

## 🧪 Testing the API

### Using cURL

1. **Register a new user:**
   ```bash
   curl -X POST http://localhost:8085/auth/register \
        -H "Content-Type: application/json" \
        -d '{"email":"test@example.com","password":"password123"}'
   ```

2. **Login and get tokens:**
   ```bash
   curl -X POST http://localhost:8085/auth/login \
        -H "Content-Type: application/json" \
        -d '{"email":"test@example.com","password":"password123"}'
   ```

3. **Create a note:**
   ```bash
   curl -X POST http://localhost:8085/note \
        -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
        -H "Content-Type: application/json" \
        -d '{"title":"Test Note","content":"This is a test","color":16777215}'
   ```

## 🔒 Security Features

### JWT Token Strategy
- **Access Tokens**: Short-lived (15 minutes) for API access
- **Refresh Tokens**: Long-lived (30 days) stored securely in database
- **Token Rotation**: New tokens generated on refresh to prevent replay attacks
- **Automatic Cleanup**: Expired tokens are automatically removed from database

### Password Security
- **BCrypt Hashing**: Industry-standard password hashing with salt
- **Password Validation**: Configurable password strength requirements
- **Credential Protection**: No plaintext passwords stored anywhere

### Authorization
- **User Isolation**: Users can only access their own notes
- **Owner Validation**: Every operation validates resource ownership
- **Stateless Authentication**: No server-side session storage

## 📈 Performance Considerations

- **MongoDB Indexing**: Optimized queries with proper indexing on `ownerId`
- **Token Expiration**: Automatic cleanup prevents database bloat
- **Stateless Design**: Horizontal scaling ready
- **Connection Pooling**: MongoDB connection optimization

## 🚀 Deployment Options

### Production Checklist
- [ ] Configure production MongoDB cluster
- [ ] Set strong JWT secret key
- [ ] Enable HTTPS/TLS
- [ ] Configure rate limiting
- [ ] Set up monitoring and logging
- [ ] Configure backup strategies
- [ ] Set up CI/CD pipeline

### Environment Variables
```bash
export MONGODB_URI=mongodb://localhost:27017/notesdb
export JWT_SECRET=your-base64-encoded-secret
export SERVER_PORT=8085
```



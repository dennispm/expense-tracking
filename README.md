# Personal Expense Tracker

A Spring Boot web application for tracking personal expenses with user authentication and admin capabilities.

## Features

- User authentication and authorization
- Add individual expense records with detailed attributes
- Search for expenses with multiple criteria
- Export search results to CSV
- Statistical dashboard with charts
- Admin user management
- REST API endpoint for adding expenses programmatically

## Technical Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security for authentication
- Spring Data JPA for database operations
- Thymeleaf for server-side templating
- H2 Database (can be replaced with any SQL database)
- Bootstrap 5 for UI
- Chart.js for data visualization

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```
   mvn spring-boot:run
   ```
4. Access the application at `http://localhost:8080`

### Default Users

The application is pre-configured with two users:

1. Admin User:
   - Username: admin
   - Password: admin123
   - Roles: ROLE_ADMIN, ROLE_USER

2. Regular User:
   - Username: user
   - Password: user123
   - Roles: ROLE_USER

## API Documentation

### Add Expense API

```
POST /expenses/api/add
```

**Headers:**
- Content-Type: application/json
- Authorization: Basic (Base64 encoded username:password)

**Request Body:**
```json
{
  "name": "Grocery Shopping",
  "amount": 45.75,
  "date": "2025-06-12",
  "category": "Food",
  "subCategory": "Groceries",
  "location": "Supermarket",
  "cardUsed": "Visa"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Grocery Shopping",
  "amount": 45.75,
  "date": "2025-06-12",
  "category": "Food",
  "subCategory": "Groceries",
  "location": "Supermarket",
  "cardUsed": "Visa"
}
```

## Database Configuration

The application uses H2 in-memory database by default. The database console is available at `http://localhost:8080/h2-console` with the following default settings:

- JDBC URL: jdbc:h2:file:./expensedb
- Username: sa
- Password: password

To use a different database, update the configuration in `application.properties`.

## License

This project is licensed under the MIT License.

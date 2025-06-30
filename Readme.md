# Hotel Reservation System

## Overview
This is a simplified Hotel Reservation System. 
The system manages hotel rooms, users, and bookings with comprehensive validation and exception handling.

## Features
- **Room Management**: Create and update hotel rooms with different types (standard, junior, suite)
- **User Management**: Create and manage users with balance tracking
- **Booking System**: Book rooms with date validation, availability checking, and balance verification
- **Data Persistence**: Uses ArrayLists as specified (no repositories)
- **Exception Handling**: Comprehensive custom exceptions for various error scenarios
- **Reporting**: Print all data with proper formatting and chronological ordering

## Project Structure
```
hotel-reservation-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── skypay/
│   │               └── hotel/
│   │                   ├── HotelReservationApplication.java  # Main application
│   │                   ├── exception/                        # Custom exceptions
│   │                   │   ├── InsufficientBalanceException.java
│   │                   │   ├── InvalidBookingDateException.java
│   │                   │   ├── RoomNotAvailableException.java
│   │                   │   ├── RoomNotFoundException.java
│   │                   │   └── UserNotFoundException.java
│   │                   ├── model/                           # Entity classes
│   │                   │   ├── Booking.java
│   │                   │   ├── Room.java
│   │                   │   ├── RoomType.java
│   │                   │   └── User.java
│   │                   └── service/                         # Service layer
│   │                       └── Service.java
│   └── test/
│       └── java/
│           └── com/
│               └── hotel/                                  # Test package
|                   ├── BaseTest.java                       # Base test class with common setup      
│                   ├── BookingManagementTest.java          # Booking functionality tests
│                   ├── CompleteScenarioTest.java           # End-to-end integration tests
│                   ├── DataOrderingTest.java               # Data ordering and reporting tests
│                   ├── EdgeCasesTest.java                  # Edge cases and boundary tests
│                   ├── RoomManagementTest.java             # Room management tests
│                   └── UserManagementTest.java             # User management tests
├── pom.xml                                                 # Maven configuration
└── README.md                                              # This file                                             # This file
```

## Prerequisites
- **Java 19** or higher
- **Maven 3.6+**
- Command line terminal

## How to Build and Run

### Method 1: Using Maven (Recommended)

1. **Clone or extract the project**
 ```bash
 git clone https://github.com/ismailastighfar/Hotel-Reservation-System.git
```

2. **Navigate to the project directory**
    ```bash
    cd hotel-reservation-system
    ```

3. **Compile the project**
    ```bash
    mvn clean compile
    ```

4. **Run the application**
    ```bash
    mvn exec:java
    ```


## Test Case Execution

The application automatically executes the specified test case when run:

1. **Creates 3 rooms:**
    - Room 1: Standard, 1000/night
    - Room 2: Junior, 2000/night  
    - Room 3: Suite, 3000/night

2. **Creates 2 users:**
    - User 1: Balance 5000
    - User 2: Balance 10000

3. **Executes booking attempts:**
    - User 1 → Room 2 (30/06/2026 to 07/07/2026) - Should fail due to insufficient balance
    - User 1 → Room 2 (07/07/2026 to 30/06/2026) - Should fail due to invalid dates
    - User 1 → Room 1 (07/07/2026 to 08/07/2026) - Should succeed
    - User 2 → Room 1 (07/07/2026 to 09/07/2026) - Should fail due to room unavailability
    - User 2 → Room 3 (07/07/2026 to 08/07/2026) - Should succeed

4. **Updates Room 1** to suite type with price 10000

5. **Displays final results** using `printAll()` and `printAllUsers()`

## Key Features Implemented

### Technical Requirements Compliance
- ✅ Users can book rooms if they have sufficient balance and room is available
- ✅ `setRoom()` doesn't impact previously created bookings
- ✅ `setRoom()` creates room if it doesn't exist, updates if it does
- ✅ `setUser()` creates user if it doesn't exist, updates if it does  
- ✅ `printAll()` shows all data from latest to oldest created
- ✅ `printAllUsers()` shows all users from latest to oldest created
- ✅ Uses ArrayLists instead of repositories
- ✅ Date handling considers only year, month, and day
- ✅ Comprehensive exception handling

### Entity Design
- **User**: ID, balance, creation timestamp
- **Room**: Number, type, price per night, creation timestamp
- **Booking**: Captures user and room details at booking time, prevents data inconsistency
- **RoomType**: Enum for standard, junior, suite types

### Exception Handling
- `InsufficientBalanceException`: When user doesn't have enough balance
- `RoomNotAvailableException`: When room is already booked for requested dates
- `InvalidBookingDateException`: When check-in date is after or equal to check-out date
- `UserNotFoundException`: When requested user doesn't exist
- `RoomNotFoundException`: When requested room doesn't exist

## Sample Output

The application will display detailed execution logs showing:
- Room and user creation confirmations
- Booking attempt results (success/failure with reasons)
- Final comprehensive reports with all data
- Formatted tables showing rooms, bookings, and users

## Design Decisions

### Booking Entity Design
The Booking entity captures snapshots of User and Room data at the time of booking to ensure data consistency even when rooms or users are updated later.

### Date Handling
Uses Java's LocalDate for precise date-only operations while maintaining compatibility with the Date API requirement.

### Exception Strategy
Implements custom exceptions for specific business logic failures while maintaining clear error messages for debugging.

## Testing
The project includes comprehensive test coverage with over 50+ test cases covering all business scenarios and edge cases.

### Test Structure
The test suite is organized into focused test classes:

- RoomManagementTest.java - Room creation, updates, and validation
- UserManagementTest.java - User creation, balance management, and validation
- BookingManagementTest.java - Booking scenarios, availability checks, and error handling
- CompleteScenarioTest.java - End-to-end integration test matching the main application flow
- DataOrderingTest.java - Data ordering and reporting functionality
- EdgeCasesTest.java - Concurrent bookings, exact balance scenarios, and boundary conditions

### Test Coverage
The tests cover:

- ✅ Happy Path Scenarios - All successful operations
- ✅ Validation Logic - Input validation and business rules
- ✅ Exception Handling - All custom exception scenarios
- ✅ Edge Cases - Boundary conditions and unusual scenarios
- ✅ Data Integrity - Ensuring updates don't affect existing bookings
- ✅ Business Logic - Room availability, balance checks, date validation

### Running Tests
**Run All Tests**
```bash
mvn test
```

**Run Specific Test Class**
```bash
mvn test -Dtest=RoomManagementTest
mvn test -Dtest=BookingManagementTest
mvn test -Dtest=CompleteScenarioTest
```


### Test Dependencies
The project uses:

- JUnit 5 - Testing framework
- AssertJ - Fluent assertion library
- Mockito - Mocking framework for time-based tests
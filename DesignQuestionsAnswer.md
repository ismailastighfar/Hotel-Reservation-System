# Design Questions Answers (Bonus)

## Question 1: Suppose we put all the functions inside the same service. Is this the recommended approach? Please explain.

**Answer: No, putting all functions in a single service is NOT the recommended approach for several reasons:**

### Problems with Single Service Approach:
1. **Violates Single Responsibility Principle (SRP)**: The Service class is handling room management, user management, booking operations, and reporting - too many responsibilities.

2. **Poor Maintainability**: As the system grows, the single service becomes a "God Class" that's difficult to maintain, test, and debug.

3. **Tight Coupling**: All functionalities are tightly coupled, making it hard to modify one area without affecting others.

4. **Testing Complexity**: Unit testing becomes difficult when all logic is in one place.

5. **Team Collaboration**: Multiple developers can't work on different features simultaneously without conflicts.

### Recommended Approach:
Split the service into specialized services following the Single Responsibility Principle:

```java
// Separate services for different concerns
public class RoomService {
    void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight);
    Room findRoom(int roomNumber);
    List<Room> getAllRooms();
}

public class UserService {
    void setUser(int userId, int balance);
    User findUser(int userId);
    List<User> getAllUsers();
    void printAllUsers();
}

public class BookingService {
    void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut);
    List<Booking> getAllBookings();
    boolean isRoomAvailable(int roomNumber, LocalDate start, LocalDate end);
}

public class ReportingService {
    void printAll();
    void generateBookingReport();
}

// Facade pattern to coordinate between services
public class HotelReservationFacade {
    private RoomService roomService;
    private UserService userService;
    private BookingService bookingService;
    private ReportingService reportingService;
}
```

### Benefits of Separated Services:
- **Better Maintainability**: Each service has a clear, focused responsibility
- **Easier Testing**: Can unit test each service independently
- **Flexibility**: Can modify or replace individual services without affecting others
- **Scalability**: Can scale different services independently if needed
- **Code Reusability**: Services can be reused in different contexts

---

## Question 2: In this design, we chose to have a function setRoom(..) that should not impact the previous bookings. What is another way? What is your recommendation? Please explain and justify.

**Answer: There are several alternative approaches to handle room updates:**

### Current Approach: Immutable Booking Data
**Description**: Store snapshots of room data in bookings at the time of booking.
**Pros**: 
- Booking history remains accurate and immutable
- Clear audit trail of what was actually booked
- No data inconsistency issues
**Cons**: 
- Data duplication
- Slightly more complex booking entity

## My Recommendation: Current Approach with Enhancements

**I recommend sticking with the current approach (immutable booking data) but with some enhancements:**

### Recommended Enhanced Solution:

```java
public class Booking {
    // Current booking data (immutable snapshot)
    private BookingSnapshot bookingSnapshot;
    
    // Reference to current room for future operations
    private int roomNumber;
    
    public Room getCurrentRoom() {
        // Get current room state for operations like cancellations
    }
    
    public BookingSnapshot getOriginalBookingDetails() {
        // Get immutable booking details
        return bookingSnapshot;
    }
}

public class BookingSnapshot {
    // Immutable data captured at booking time
    private final RoomType roomTypeAtBooking;
    private final int pricePerNightAtBooking;
    private final int userBalanceAtBooking;
    private final LocalDateTime bookingDateTime;
}
```

### Justification:

1. **Business Accuracy**: Customers should be charged what was agreed upon at booking time, not current prices.

2. **Legal Compliance**: Many jurisdictions require honoring the original booking terms.

3. **Customer Trust**: Changing booking terms after confirmation erodes customer confidence.

4. **Audit Trail**: Complete historical record for accounting and dispute resolution.

5. **Flexibility**: Can still access current room information when needed for operations like modifications or cancellations.

6. **Data Integrity**: Prevents data corruption from concurrent updates.

### Additional Enhancements:
- **Change Notifications**: Notify customers when room details change for future stays
- **Upgrade Opportunities**: Offer room upgrades when original room type is no longer available
- **Flexible Cancellation**: Allow customers to cancel and rebook at new rates if they prefer
- **Booking Modifications**: Separate service for handling booking changes with proper validations

This approach balances business requirements, technical feasibility, and customer satisfaction while maintaining data integrity and system reliability.
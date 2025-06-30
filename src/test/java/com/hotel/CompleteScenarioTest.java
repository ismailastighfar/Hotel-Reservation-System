package com.hotel;

import com.skypay.hotel.exception.InsufficientBalanceException;
import com.skypay.hotel.exception.InvalidBookingDateException;
import com.skypay.hotel.exception.RoomNotAvailableException;
import com.skypay.hotel.model.Booking;
import com.skypay.hotel.model.RoomType;
import com.skypay.hotel.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Complete Test Case Scenario")
class CompleteScenarioTest extends BaseTest {

    @Test
    @DisplayName("Should execute the complete test case as specified")
    void shouldExecuteCompleteTestCase() {
        // Step 1: Create 3 rooms
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setRoom(2, RoomType.JUNIOR, 2000);
        service.setRoom(3, RoomType.SUITE, 3000);

        // Step 2: Create 2 users
        service.setUser(1, 5000);
        service.setUser(2, 10000);

        // Step 3: Execute booking attempts
        Date checkIn1 = createDate(2026, 5, 30); // June 30, 2026
        Date checkOut1 = createDate(2026, 6, 7);  // July 7, 2026 (7 nights)
        Date checkIn2 = createDate(2026, 6, 7);   // July 7, 2026
        Date checkOut2 = createDate(2026, 5, 30); // June 30, 2026 (invalid)
        Date checkIn3 = createDate(2026, 6, 7);   // July 7, 2026
        Date checkOut3 = createDate(2026, 6, 8);  // July 8, 2026 (1 night)
        Date checkIn4 = createDate(2026, 6, 7);   // July 7, 2026
        Date checkOut4 = createDate(2026, 6, 9);  // July 9, 2026 (2 nights)
        Date checkIn5 = createDate(2026, 6, 7);   // July 7, 2026
        Date checkOut5 = createDate(2026, 6, 8);  // July 8, 2026 (1 night)

        // User 1 tries booking Room 2 (should fail - insufficient balance: 7*2000=14000 > 5000)
        assertThrows(InsufficientBalanceException.class,
                () -> service.bookRoom(1, 2, checkIn1, checkOut1));

        // User 1 tries booking Room 2 with invalid dates (should fail)
        assertThrows(InvalidBookingDateException.class,
                () -> service.bookRoom(1, 2, checkIn2, checkOut2));

        // User 1 books Room 1 (should succeed: 1*1000=1000 <= 5000)
        assertDoesNotThrow(() -> service.bookRoom(1, 1, checkIn3, checkOut3));

        // User 2 tries booking Room 1 (should fail - room not available)
        assertThrows(RoomNotAvailableException.class,
                () -> service.bookRoom(2, 1, checkIn4, checkOut4));

        // User 2 books Room 3 (should succeed: 1*3000=3000 <= 10000)
        assertDoesNotThrow(() -> service.bookRoom(2, 3, checkIn5, checkOut5));

        // Step 4: Update Room 1
        service.setRoom(1, RoomType.SUITE, 10000);

        // Verify final state
        assertThat(service.getRooms()).hasSize(3);
        assertThat(service.getUsers()).hasSize(2);
        assertThat(service.getBookings()).hasSize(2);

        // Verify user balances
        User user1 = service.getUsers().stream().filter(u -> u.getUserId() == 1).findFirst().orElseThrow();
        User user2 = service.getUsers().stream().filter(u -> u.getUserId() == 2).findFirst().orElseThrow();
        assertThat(user1.getBalance()).isEqualTo(4000); // 5000 - 1000
        assertThat(user2.getBalance()).isEqualTo(7000); // 10000 - 3000

        // Verify room update didn't affect bookings
        Booking room1Booking = service.getBookings().stream()
                .filter(b -> b.getRoomNumber() == 1)
                .findFirst()
                .orElseThrow();
        assertThat(room1Booking.getRoomTypeAtBooking()).isEqualTo(RoomType.STANDARD);
        assertThat(room1Booking.getRoomPricePerNightAtBooking()).isEqualTo(1000);
    }
}
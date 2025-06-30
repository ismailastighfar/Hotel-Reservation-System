package com.hotel;

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

@DisplayName("Edge Cases and Error Handling")
class EdgeCasesTest extends BaseTest {

    @Test
    @DisplayName("Should handle concurrent bookings gracefully")
    void shouldHandleConcurrentBookingsGracefully() {
        // Given
        service.setRoom(101, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        service.setUser(2, 5000);

        // When - First booking succeeds
        assertDoesNotThrow(() -> service.bookRoom(1, 101, checkInDate, checkOutDate));

        // Then - Second booking for same dates should fail
        assertThrows(RoomNotAvailableException.class,
                () -> service.bookRoom(2, 101, checkInDate, checkOutDate));
    }

    @Test
    @DisplayName("Should handle exact balance scenarios")
    void shouldHandleExactBalanceScenarios() {
        // Given - User with exact balance needed
        service.setRoom(101, RoomType.STANDARD, 1000);
        service.setUser(1, 2000); // Exactly enough for 2 nights

        // When & Then
        assertDoesNotThrow(() -> service.bookRoom(1, 101, checkInDate, checkOutDate));

        User user = service.getUsers().get(0);
        assertThat(user.getBalance()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle single night bookings")
    void shouldHandleSingleNightBookings() {
        // Given
        service.setRoom(101, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);

        Date singleNightCheckOut = createDate(2026, 6, 8); // One day after check-in

        // When
        assertDoesNotThrow(() -> service.bookRoom(1, 101, checkInDate, singleNightCheckOut));

        // Then
        Booking booking = service.getBookings().get(0);
        assertThat(booking.getNumberOfNights()).isEqualTo(1);
        assertThat(booking.getTotalAmount()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Should handle large number of nights")
    void shouldHandleLargeNumberOfNights() {
        // Given
        service.setRoom(101, RoomType.STANDARD, 100);
        service.setUser(1, 100000);

        Date longStayCheckOut = createDate(2026, 7, 6); // 30 days later

        // When
        assertDoesNotThrow(() -> service.bookRoom(1, 101, checkInDate, longStayCheckOut));

        // Then
        Booking booking = service.getBookings().get(0);
        assertThat(booking.getNumberOfNights()).isEqualTo(30);
        assertThat(booking.getTotalAmount()).isEqualTo(3000);
    }
}
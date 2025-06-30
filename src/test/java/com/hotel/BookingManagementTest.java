package com.hotel;

import com.skypay.hotel.exception.InsufficientBalanceException;
import com.skypay.hotel.exception.InvalidBookingDateException;
import com.skypay.hotel.exception.RoomNotFoundException;
import com.skypay.hotel.exception.RoomNotAvailableException;
import com.skypay.hotel.exception.UserNotFoundException;
import com.skypay.hotel.model.Booking;
import com.skypay.hotel.model.RoomType;
import com.skypay.hotel.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Booking Management Tests")
class BookingManagementTest extends BaseTest {

    @BeforeEach
    void setUpRoomAndUser() {
        super.setUp();
        service.setRoom(101, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
    }

    @Test
    @DisplayName("Should book room successfully when all conditions are met")
    void shouldBookRoomSuccessfully() {
        // When
        assertDoesNotThrow(() -> service.bookRoom(1, 101, checkInDate, checkOutDate));

        // Then
        List<Booking> bookings = service.getBookings();
        assertThat(bookings).hasSize(1);

        Booking booking = bookings.get(0);
        assertThat(booking.getUserId()).isEqualTo(1);
        assertThat(booking.getRoomNumber()).isEqualTo(101);
        assertThat(booking.getNumberOfNights()).isEqualTo(2);
        assertThat(booking.getTotalAmount()).isEqualTo(2000);
        assertThat(booking.getRoomTypeAtBooking()).isEqualTo(RoomType.STANDARD);
        assertThat(booking.getRoomPricePerNightAtBooking()).isEqualTo(1000);

        // User balance should be deducted
        User user = service.getUsers().get(0);
        assertThat(user.getBalance()).isEqualTo(3000); // 5000 - 2000
    }

    @Test
    @DisplayName("Should reject booking when user has insufficient balance")
    void shouldRejectBookingWhenInsufficientBalance() {
        // Given - User with low balance
        service.setUser(2, 1000); // Only 1000 balance

        // When & Then
        Exception exception = assertThrows(InsufficientBalanceException.class,
                () -> service.bookRoom(2, 101, checkInDate, checkOutDate));

        assertThat(exception.getMessage())
                .contains("insufficient balance")
                .contains("Required: 2000")
                .contains("Available: 1000");

        // No booking should be created
        assertThat(service.getBookings()).isEmpty();

        // User balance should remain unchanged
        User user = service.getUsers().stream()
                .filter(u -> u.getUserId() == 2)
                .findFirst()
                .orElseThrow();
        assertThat(user.getBalance()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Should reject booking when room is not available")
    void shouldRejectBookingWhenRoomNotAvailable() {
        // Given - Create another user and book the room
        service.setUser(2, 10000);
        service.bookRoom(1, 101, checkInDate, checkOutDate); // First booking

        // When & Then - Try to book the same room for overlapping dates
        Date overlappingCheckIn = createDate(2026, 6, 8);  // July 8, 2026
        Date overlappingCheckOut = createDate(2026, 6, 10); // July 10, 2026

        Exception exception = assertThrows(RoomNotAvailableException.class,
                () -> service.bookRoom(2, 101, overlappingCheckIn, overlappingCheckOut));

        assertThat(exception.getMessage())
                .contains("Room 101 is not available");

        // Only one booking should exist
        assertThat(service.getBookings()).hasSize(1);
    }

    @Test
    @DisplayName("Should allow booking same room for non-overlapping dates")
    void shouldAllowBookingSameRoomForNonOverlappingDates() {
        // Given - First booking
        service.setUser(2, 10000);
        service.bookRoom(1, 101, checkInDate, checkOutDate); // July 7-9

        // When - Book for non-overlapping dates
        Date nonOverlappingCheckIn = createDate(2026, 6, 10);  // July 10, 2026
        Date nonOverlappingCheckOut = createDate(2026, 6, 12); // July 12, 2026

        assertDoesNotThrow(() -> service.bookRoom(2, 101, nonOverlappingCheckIn, nonOverlappingCheckOut));

        // Then - Both bookings should exist
        assertThat(service.getBookings()).hasSize(2);
    }

    @Test
    @DisplayName("Should reject booking with invalid dates")
    void shouldRejectBookingWithInvalidDates() {
        // When & Then - Check-out before check-in
        Exception exception = assertThrows(InvalidBookingDateException.class,
                () -> service.bookRoom(1, 101, checkInDate, invalidCheckOutDate));

        assertThat(exception.getMessage())
                .contains("Check-in date")
                .contains("must be before check-out date");
    }

    @Test
    @DisplayName("Should reject booking with same check-in and check-out dates")
    void shouldRejectBookingWithSameDates() {
        // When & Then
        Exception exception = assertThrows(InvalidBookingDateException.class,
                () -> service.bookRoom(1, 101, checkInDate, checkInDate));

        assertThat(exception.getMessage())
                .contains("Check-in date")
                .contains("must be before check-out date");
    }

    @Test
    @DisplayName("Should reject booking for non-existent user")
    void shouldRejectBookingForNonExistentUser() {
        // When & Then
        Exception exception = assertThrows(UserNotFoundException.class,
                () -> service.bookRoom(999, 101, checkInDate, checkOutDate));

        assertThat(exception.getMessage()).contains("User with ID 999 not found");
    }

    @Test
    @DisplayName("Should reject booking for non-existent room")
    void shouldRejectBookingForNonExistentRoom() {
        // When & Then
        Exception exception = assertThrows(RoomNotFoundException.class,
                () -> service.bookRoom(1, 999, checkInDate, checkOutDate));

        assertThat(exception.getMessage()).contains("Room with number 999 not found");
    }

    @Test
    @DisplayName("Should handle null dates gracefully")
    void shouldHandleNullDatesGracefully() {
        // When & Then
        assertThrows(RuntimeException.class,
                () -> service.bookRoom(1, 101, null, checkOutDate));

        assertThrows(RuntimeException.class,
                () -> service.bookRoom(1, 101, checkInDate, null));
    }
}
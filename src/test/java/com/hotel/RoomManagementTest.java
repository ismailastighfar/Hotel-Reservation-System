package com.hotel;

import com.skypay.hotel.model.Booking;
import com.skypay.hotel.model.Room;
import com.skypay.hotel.model.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Room Management Tests")
class RoomManagementTest extends BaseTest {

    @Test
    @DisplayName("Should create new room successfully")
    void shouldCreateNewRoomSuccessfully() {
        // When
        service.setRoom(101, RoomType.STANDARD, 1500);

        // Then
        List<Room> rooms = service.getRooms();
        assertThat(rooms).hasSize(1);

        Room createdRoom = rooms.get(0);
        assertThat(createdRoom.getRoomNumber()).isEqualTo(101);
        assertThat(createdRoom.getRoomType()).isEqualTo(RoomType.STANDARD);
        assertThat(createdRoom.getPricePerNight()).isEqualTo(1500);
        assertThat(createdRoom.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update existing room without affecting previous bookings")
    void shouldUpdateExistingRoomWithoutAffectingBookings() {
        // Given - Create room and user
        service.setRoom(201, RoomType.JUNIOR, 2000);
        service.setUser(1, 10000);

        // Create a booking
        service.bookRoom(1, 201, checkInDate, checkOutDate);

        // When - Update room
        service.setRoom(201, RoomType.SUITE, 5000);

        // Then - Room should be updated
        List<Room> rooms = service.getRooms();
        Room updatedRoom = rooms.stream()
                .filter(r -> r.getRoomNumber() == 201)
                .findFirst()
                .orElseThrow();

        assertThat(updatedRoom.getRoomType()).isEqualTo(RoomType.SUITE);
        assertThat(updatedRoom.getPricePerNight()).isEqualTo(5000);

        // And bookings should remain unchanged
        List<Booking> bookingsAfterUpdate = service.getBookings();
        assertThat(bookingsAfterUpdate).hasSize(1);

        Booking booking = bookingsAfterUpdate.get(0);
        assertThat(booking.getRoomTypeAtBooking()).isEqualTo(RoomType.JUNIOR);
        assertThat(booking.getRoomPricePerNightAtBooking()).isEqualTo(2000);
        assertThat(booking.getTotalAmount()).isEqualTo(4000); // 2 nights * 2000
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid room parameters")
    @CsvSource({
            "0, STANDARD, 1000, Room number must be positive",
            "-1, JUNIOR, 2000, Room number must be positive",
            "1, STANDARD, 0, Price per night must be positive",
            "1, SUITE, -100, Price per night must be positive"
    })
    void shouldRejectInvalidRoomParameters(int roomNumber, RoomType roomType, int price, String expectedMessage) {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> service.setRoom(roomNumber, roomType, price));

        assertThat(exception.getMessage()).contains(expectedMessage.replace("Price per night", "Room price per night"));
    }

    @Test
    @DisplayName("Should reject null room type")
    void shouldRejectNullRoomType() {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> service.setRoom(1, null, 1000));

        assertThat(exception.getMessage()).contains("Room type cannot be null");
    }

    @Test
    @DisplayName("Should handle multiple rooms with same type but different prices")
    void shouldHandleMultipleRoomsWithSameTypeButDifferentPrices() {
        // When
        service.setRoom(101, RoomType.SUITE, 3000);
        service.setRoom(102, RoomType.SUITE, 4000);
        service.setRoom(103, RoomType.SUITE, 3500);

        // Then
        List<Room> rooms = service.getRooms();
        assertThat(rooms).hasSize(3);

        List<Room> suiteRooms = rooms.stream()
                .filter(r -> r.getRoomType() == RoomType.SUITE)
                .toList();

        assertThat(suiteRooms).hasSize(3);
        assertThat(suiteRooms)
                .extracting(Room::getPricePerNight)
                .containsExactlyInAnyOrder(3000, 4000, 3500);
    }
}
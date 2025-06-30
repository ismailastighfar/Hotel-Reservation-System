package com.hotel;

import com.skypay.hotel.model.Room;
import com.skypay.hotel.model.RoomType;
import com.skypay.hotel.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Data Ordering and Reporting Tests")
class DataOrderingTest extends BaseTest {

    @Test
    @DisplayName("Should maintain creation order for rooms (latest first)")
    void shouldMaintainCreationOrderForRooms() {
        // Given - Create rooms with slight delays to ensure different timestamps
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            LocalDateTime time1 = LocalDateTime.of(2026, 1, 1, 10, 0);
            LocalDateTime time2 = LocalDateTime.of(2026, 1, 1, 11, 0);
            LocalDateTime time3 = LocalDateTime.of(2026, 1, 1, 12, 0);

            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(time1);
            service.setRoom(101, RoomType.STANDARD, 1000);

            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(time2);
            service.setRoom(102, RoomType.JUNIOR, 2000);

            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(time3);
            service.setRoom(103, RoomType.SUITE, 3000);

            // When
            List<Room> rooms = service.getRooms();

            // Then - Should be in creation order in the internal list
            assertThat(rooms).hasSize(3);
            assertThat(rooms.get(0).getRoomNumber()).isEqualTo(101);
            assertThat(rooms.get(1).getRoomNumber()).isEqualTo(102);
            assertThat(rooms.get(2).getRoomNumber()).isEqualTo(103);
        }
    }

    @Test
    @DisplayName("Should maintain creation order for users (latest first)")
    void shouldMaintainCreationOrderForUsers() {
        // Given
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            LocalDateTime time1 = LocalDateTime.of(2026, 1, 1, 10, 0);
            LocalDateTime time2 = LocalDateTime.of(2026, 1, 1, 11, 0);

            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(time1);
            service.setUser(1, 5000);

            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(time2);
            service.setUser(2, 10000);

            // When
            List<User> users = service.getUsers();

            // Then
            assertThat(users).hasSize(2);
            assertThat(users.get(0).getUserId()).isEqualTo(1);
            assertThat(users.get(1).getUserId()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Should not throw exceptions when printing empty data")
    void shouldNotThrowExceptionsWhenPrintingEmptyData() {
        // When & Then
        assertDoesNotThrow(() -> service.printAll());
        assertDoesNotThrow(() -> service.printAllUsers());
    }
}
package com.hotel;

import com.skypay.hotel.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("User Management Tests")
class UserManagementTest extends BaseTest {

    @Test
    @DisplayName("Should create new user successfully")
    void shouldCreateNewUserSuccessfully() {
        // When
        service.setUser(1, 5000);

        // Then
        List<User> users = service.getUsers();
        assertThat(users).hasSize(1);

        User createdUser = users.get(0);
        assertThat(createdUser.getUserId()).isEqualTo(1);
        assertThat(createdUser.getBalance()).isEqualTo(5000);
        assertThat(createdUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update existing user balance")
    void shouldUpdateExistingUserBalance() {
        // Given
        service.setUser(1, 5000);

        // When
        service.setUser(1, 8000);

        // Then
        List<User> users = service.getUsers();
        assertThat(users).hasSize(1);

        User updatedUser = users.get(0);
        assertThat(updatedUser.getUserId()).isEqualTo(1);
        assertThat(updatedUser.getBalance()).isEqualTo(8000);
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid user parameters")
    @ValueSource(ints = {0, -1, -100})
    void shouldRejectInvalidUserId(int userId) {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> service.setUser(userId, 1000));

        assertThat(exception.getMessage()).contains("User ID must be positive");
    }

    @ParameterizedTest
    @DisplayName("Should reject negative balance")
    @ValueSource(ints = {-1, -100, -1000})
    void shouldRejectNegativeBalance(int balance) {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> service.setUser(1, balance));

        assertThat(exception.getMessage()).contains("User balance cannot be negative");
    }

    @Test
    @DisplayName("Should allow zero balance")
    void shouldAllowZeroBalance() {
        // When & Then
        assertDoesNotThrow(() -> service.setUser(1, 0));

        User user = service.getUsers().get(0);
        assertThat(user.getBalance()).isEqualTo(0);
    }
}
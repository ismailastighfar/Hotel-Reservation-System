package com.skypay.hotel.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity representing a hotel customer with balance for booking rooms.
 */
public class User {
    private int userId;
    private int balance;
    private LocalDateTime createdAt;

    /**
     * Creates a new user with the specified user ID and initial balance
     * @param userId the unique user identifier (must be positive)
     * @param balance the initial balance for the user (cannot be negative)
     * @throws IllegalArgumentException if userId is not positive or balance is negative
     */
    public User(int userId, int balance) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("User balance cannot be negative");
        }

        this.userId = userId;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public int getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    /**
     * Updates the user's balance
     * @param balance the new balance to set (cannot be negative)
     * @throws IllegalArgumentException if balance is negative
     */
    public void setBalance(int balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("User balance cannot be negative");
        }
        this.balance = balance;
    }

    /**
     * Deducts the specified amount from the user's balance
     * @param amount the amount to deduct (cannot be negative)
     * @throws IllegalArgumentException if amount is negative or exceeds current balance
     */
    public void deductBalance(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to deduct cannot be negative");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("Insufficient balance. Current balance: " +
                    this.balance + ", Required: " + amount);
        }
        this.balance -= amount;
    }

    /**
     * Check if user has sufficient balance
     * @param amount the amount to check against current balance
     * @return true if balance is sufficient, false otherwise
     */
    public boolean hasSufficientBalance(int amount) {
        return this.balance >= amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return String.format("User{userId=%d, balance=%d, createdAt=%s}",
                userId, balance, createdAt);
    }
}
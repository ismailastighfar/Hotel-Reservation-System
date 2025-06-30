package com.skypay.hotel.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Room entity representing a hotel room with type and pricing.
 */
public class Room {
    private int roomNumber;
    private RoomType roomType;
    private int pricePerNight;
    private LocalDateTime createdAt;

    /**
     * Creates a new room with the specified room number, type, and pricing
     * @param roomNumber the unique room number (must be positive)
     * @param roomType the type of the room
     * @param pricePerNight the price per night for this room (must be positive)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Room(int roomNumber, RoomType roomType, int pricePerNight) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Price per night must be positive");
        }

        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    /**
     * Updates the room type for this room
     * @param roomType the new room type to set
     * @throws IllegalArgumentException if roomType is null
     */
    public void setRoomType(RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        this.roomType = roomType;
    }

    /**
     * Updates the price per night for this room
     * @param pricePerNight the new price per night (must be positive)
     * @throws IllegalArgumentException if pricePerNight is not positive
     */
    public void setPricePerNight(int pricePerNight) {
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Price per night must be positive");
        }
        this.pricePerNight = pricePerNight;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Room room = (Room) obj;
        return roomNumber == room.roomNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }

    @Override
    public String toString() {
        return String.format("Room{roomNumber=%d, roomType=%s, pricePerNight=%d, createdAt=%s}",
                roomNumber, roomType, pricePerNight, createdAt);
    }
}
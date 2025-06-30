package com.skypay.hotel.model;

/**
 * Enum representing the different types of hotel rooms available.
 */
public enum RoomType {
    STANDARD("standard"),
    JUNIOR("junior"),
    SUITE("suite");

    private final String displayName;

    /**
     * Creates a RoomType enum constant with the specified display name
     * @param displayName the display name for this room type
     */
    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a string to RoomType enum, case-insensitive
     * @param type the string representation of the room type to parse
     * @return the corresponding RoomType enum constant
     * @throws IllegalArgumentException if type is null or does not match any valid room type
     */
    public static RoomType fromString(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }

        for (RoomType roomType : RoomType.values()) {
            if (roomType.displayName.equalsIgnoreCase(type.trim())) {
                return roomType;
            }
        }

        throw new IllegalArgumentException("Invalid room type: " + type +
                ". Valid types are: standard, junior, suite");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
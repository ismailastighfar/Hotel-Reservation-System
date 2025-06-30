package com.skypay.hotel.service;

import com.skypay.hotel.exception.*;
import com.skypay.hotel.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

/**
 * Service class that handles hotel reservation operations.
 * Manages rooms, users, and bookings using ArrayLists as specified.
 */
public class Service {
    private ArrayList<Room> rooms;
    private ArrayList<User> users;
    private ArrayList<Booking> bookings;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Default constructor that initializes empty ArrayLists for rooms, users, and bookings.
     */
    public Service() {
        this.rooms = new ArrayList<>();
        this.users = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    /**
     * Creates or updates a room. If room exists, updates its type and price.
     * If room doesn't exist, creates a new one.
     * This operation doesn't impact previously created bookings.
     * 
     * @param roomNumber the unique identifier for the room (must be positive)
     * @param roomType the type of the room (cannot be null)
     * @param roomPricePerNight the price per night for the room (must be positive)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        try {
            if (roomNumber <= 0) {
                throw new IllegalArgumentException("Room number must be positive");
            }
            if (roomType == null) {
                throw new IllegalArgumentException("Room type cannot be null");
            }
            if (roomPricePerNight <= 0) {
                throw new IllegalArgumentException("Room price per night must be positive");
            }

            Optional<Room> existingRoom = rooms.stream()
                    .filter(room -> room.getRoomNumber() == roomNumber)
                    .findFirst();

            if (existingRoom.isPresent()) {
                // Update existing room
                Room room = existingRoom.get();
                room.setRoomType(roomType);
                room.setPricePerNight(roomPricePerNight);
                System.out.println("Updated room " + roomNumber + " - Type: " + roomType +
                        ", Price: " + roomPricePerNight);
            } else {
                // Create new room
                Room newRoom = new Room(roomNumber, roomType, roomPricePerNight);
                rooms.add(newRoom);
                System.out.println("Created new room " + roomNumber + " - Type: " + roomType +
                        ", Price: " + roomPricePerNight);
            }
        } catch (Exception e) {
            System.err.println("Error setting room: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates a user if it doesn't exist, or updates the balance if it exists.
     * 
     * @param userId the unique identifier for the user (must be positive)
     * @param balance the initial or updated balance for the user (cannot be negative)
     * @throws IllegalArgumentException if userId is not positive or balance is negative
     */
    public void setUser(int userId, int balance) {
        try {
            if (userId <= 0) {
                throw new IllegalArgumentException("User ID must be positive");
            }
            if (balance < 0) {
                throw new IllegalArgumentException("User balance cannot be negative");
            }

            Optional<User> existingUser = users.stream()
                    .filter(user -> user.getUserId() == userId)
                    .findFirst();

            if (existingUser.isPresent()) {
                // Update existing user balance
                User user = existingUser.get();
                user.setBalance(balance);
                System.out.println("Updated user " + userId + " balance to: " + balance);
            } else {
                // Create new user
                User newUser = new User(userId, balance);
                users.add(newUser);
                System.out.println("Created new user " + userId + " with balance: " + balance);
            }
        } catch (Exception e) {
            System.err.println("Error setting user: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Books a room for a user for the specified period.
     * Validates user balance, room availability, and date constraints.
     * 
     * @param userId the ID of the user making the booking
     * @param roomNumber the number of the room to be booked
     * @param checkIn the check-in date
     * @param checkOut the check-out date (must be after check-in date)
     * @throws InvalidBookingDateException if check-in date is not before check-out date
     * @throws UserNotFoundException if the user with given ID doesn't exist
     * @throws RoomNotFoundException if the room with given number doesn't exist
     * @throws RoomNotAvailableException if the room is already booked for the specified period
     * @throws InsufficientBalanceException if the user doesn't have enough balance
     */
    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        try {
            // Convert Date objects to LocalDate (considering only year, month, day)
            LocalDate checkInDate = convertToLocalDate(checkIn);
            LocalDate checkOutDate = convertToLocalDate(checkOut);

            // Validate booking dates
            if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
                throw new InvalidBookingDateException(
                        "Check-in date (" + checkInDate.format(DATE_FORMATTER) +
                                ") must be before check-out date (" + checkOutDate.format(DATE_FORMATTER) + ")");
            }

            // Find and validate user existence
            User user = users.stream()
                    .filter(u -> u.getUserId() == userId)
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

            // Find and validate room existence
            Room room = rooms.stream()
                    .filter(r -> r.getRoomNumber() == roomNumber)
                    .findFirst()
                    .orElseThrow(() -> new RoomNotFoundException("Room with number " + roomNumber + " not found"));

            // Verify room availability for the specified period
            boolean isRoomAvailable = bookings.stream()
                    .filter(booking -> booking.getRoomNumber() == roomNumber)
                    .noneMatch(booking -> booking.overlaps(checkInDate, checkOutDate));

            if (!isRoomAvailable) {
                throw new RoomNotAvailableException(
                        "Room " + roomNumber + " is not available from " +
                                checkInDate.format(DATE_FORMATTER) + " to " + checkOutDate.format(DATE_FORMATTER));
            }

            // Calculate total booking cost
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            int totalCost = (int) (nights * room.getPricePerNight());

            // Verify user has sufficient balance
            if (!user.hasSufficientBalance(totalCost)) {
                throw new InsufficientBalanceException(
                        "User " + userId + " has insufficient balance. Required: " + totalCost +
                                ", Available: " + user.getBalance());
            }

            // Create booking and process payment
            Booking booking = new Booking(user, room, checkInDate, checkOutDate);
            user.deductBalance(totalCost);
            bookings.add(booking);

            System.out.println("Successfully booked Room " + roomNumber + " for User " + userId +
                    " from " + checkInDate.format(DATE_FORMATTER) + " to " + checkOutDate.format(DATE_FORMATTER) +
                    " (" + nights + " nights) - Total: " + totalCost);

        } catch (InvalidBookingDateException | UserNotFoundException |
                 RoomNotFoundException | RoomNotAvailableException |
                 InsufficientBalanceException e) {
            System.err.println("Booking failed: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during booking: " + e.getMessage());
            throw new RuntimeException("Booking failed due to unexpected error", e);
        }
    }

    /**
     * Prints all rooms and bookings data from latest created to oldest created.
     */
    public void printAll() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("HOTEL RESERVATION SYSTEM - ALL DATA");
        System.out.println("=".repeat(80));

        // Print rooms (latest to oldest)
        System.out.println("\nROOMS (Latest to Oldest):");
        System.out.println("-".repeat(50));
        if (rooms.isEmpty()) {
            System.out.println("No rooms available.");
        } else {
            rooms.stream()
                    .sorted(Comparator.comparing(Room::getCreatedAt).reversed())
                    .forEach(room -> {
                        System.out.printf("Room %d | Type: %-8s | Price/Night: %-6d | Created: %s%n",
                                room.getRoomNumber(),
                                room.getRoomType().toString(),
                                room.getPricePerNight(),
                                room.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        );
                    });
        }

        // Print bookings (latest to oldest)
        System.out.println("\nBOOKINGS (Latest to Oldest):");
        System.out.println("-".repeat(50));
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
        } else {
            bookings.stream()
                    .sorted(Comparator.comparing(Booking::getBookingDateTime).reversed())
                    .forEach(booking -> {
                        System.out.printf("Booking ID: %-3d | User: %-3d | Room: %-3d | %s to %s (%d nights)%n",
                                booking.getBookingId(),
                                booking.getUserId(),
                                booking.getRoomNumber(),
                                booking.getCheckInDate().format(DATE_FORMATTER),
                                booking.getCheckOutDate().format(DATE_FORMATTER),
                                booking.getNumberOfNights()
                        );
                        System.out.printf("                Room Type at Booking: %-8s | Price/Night at Booking: %-6d | Total: %-6d%n",
                                booking.getRoomTypeAtBooking().toString(),
                                booking.getRoomPricePerNightAtBooking(),
                                booking.getTotalAmount()
                        );
                        System.out.printf("                User Balance at Booking: %-6d | Booking Date: %s%n",
                                booking.getUserBalanceAtBooking(),
                                booking.getBookingDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        );
                        System.out.println();
                    });
        }
        System.out.println("=".repeat(80));
    }

    /**
     * Prints all users data from latest created to oldest created.
     */
    public void printAllUsers() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ALL USERS DATA (Latest to Oldest)");
        System.out.println("=".repeat(60));

        if (users.isEmpty()) {
            System.out.println("No users available.");
        } else {
            users.stream()
                    .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                    .forEach(user -> {
                        System.out.printf("User ID: %-3d | Balance: %-8d | Created: %s%n",
                                user.getUserId(),
                                user.getBalance(),
                                user.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        );
                    });
        }
        System.out.println("=".repeat(60));
    }

    /**
     * Helper method to convert Date to LocalDate
     */
    @SuppressWarnings("deprecation")
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        // Using deprecated methods as requested to consider only year, month, day
        return LocalDate.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    // Getter methods for testing purposes
    public ArrayList<Room> getRooms() {
        return new ArrayList<>(rooms);
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(users);
    }

    public ArrayList<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }
}
package com.skypay.hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Booking entity representing a hotel room reservation with user and room details
 * captured at the time of booking.
 */
public class Booking {
    private int bookingId;
    private int userId;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int totalAmount;
    private LocalDateTime bookingDateTime;

    // Snapshot of user and room details at the time of booking
    private int userBalanceAtBooking;
    private RoomType roomTypeAtBooking;
    private int roomPricePerNightAtBooking;

    private static int bookingCounter = 1;

    /**
     * Creates a new booking for a hotel room reservation
     * @param user the user making the booking
     * @param room the room being booked
     * @param checkInDate the check-in date for the reservation
     * @param checkOutDate the check-out date for the reservation
     * @throws IllegalArgumentException if any parameter is null or if dates are invalid
     */
    public Booking(User user, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        if (checkInDate == null) {
            throw new IllegalArgumentException("Check-in date cannot be null");
        }
        if (checkOutDate == null) {
            throw new IllegalArgumentException("Check-out date cannot be null");
        }
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        this.bookingId = bookingCounter++;
        this.userId = user.getUserId();
        this.roomNumber = room.getRoomNumber();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingDateTime = LocalDateTime.now();

        // Calculate number of nights and total amount
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        this.totalAmount = (int) (nights * room.getPricePerNight());

        // Capture snapshot of user and room details at booking time
        this.userBalanceAtBooking = user.getBalance();
        this.roomTypeAtBooking = room.getRoomType();
        this.roomPricePerNightAtBooking = room.getPricePerNight();
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public int getUserBalanceAtBooking() {
        return userBalanceAtBooking;
    }

    public RoomType getRoomTypeAtBooking() {
        return roomTypeAtBooking;
    }

    public int getRoomPricePerNightAtBooking() {
        return roomPricePerNightAtBooking;
    }

    /**
     * Calculates the number of nights for this booking
     * @return the number of nights between check-in and check-out dates
     */
    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    /**
     * Checks if this booking overlaps with the given date range
     * @param startDate the start date of the range to check
     * @param endDate the end date of the range to check
     * @return true if this booking overlaps with the specified date range, false otherwise
     */
    public boolean overlaps(LocalDate startDate, LocalDate endDate) {
        return !(checkOutDate.isBefore(startDate) || checkOutDate.isEqual(startDate) ||
                checkInDate.isAfter(endDate) || checkInDate.isEqual(endDate));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return String.format(
                "Booking{bookingId=%d, userId=%d, roomNumber=%d, checkIn=%s, checkOut=%s, " +
                        "nights=%d, totalAmount=%d, userBalanceAtBooking=%d, roomTypeAtBooking=%s, " +
                        "roomPriceAtBooking=%d, bookingDate=%s}",
                bookingId, userId, roomNumber, checkInDate, checkOutDate,
                getNumberOfNights(), totalAmount, userBalanceAtBooking,
                roomTypeAtBooking, roomPricePerNightAtBooking, bookingDateTime
        );
    }
}
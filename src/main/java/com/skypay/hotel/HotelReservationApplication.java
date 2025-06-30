package com.skypay.hotel;

import com.skypay.hotel.model.RoomType;
import com.skypay.hotel.service.Service;

import java.util.Date;

/**
 * Main application class that demonstrates the Hotel Reservation System
 * by executing the provided test case.
 */
public class HotelReservationApplication {

    public static void main(String[] args) {
        System.out.println("Starting Hotel Reservation System...");
        System.out.println("Executing Test Case as specified in requirements...\n");

        // Initialize the service
        Service service = new Service();

        try {
            // Test Case Execution
            executeTestCase(service);
        } catch (Exception e) {
            System.err.println("Error during test case execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes the complete test case as specified in the requirements
     */
    private static void executeTestCase(Service service) {
        System.out.println("=".repeat(80));
        System.out.println("EXECUTING TEST CASE");
        System.out.println("=".repeat(80));

        // Step 1: Create 3 rooms
        System.out.println("\n1. Creating 3 rooms:");
        System.out.println("-".repeat(40));
        service.setRoom(1, RoomType.STANDARD, 1000);  // Room 1: standard, 1000
        service.setRoom(2, RoomType.JUNIOR, 2000);    // Room 2: junior, 2000
        service.setRoom(3, RoomType.SUITE, 3000);     // Room 3: suite, 3000

        // Step 2: Create 2 users
        System.out.println("\n2. Creating 2 users:");
        System.out.println("-".repeat(40));
        service.setUser(1, 5000);   // User 1 with balance 5000
        service.setUser(2, 10000);  // User 2 with balance 10000

        // Step 3: Execute booking attempts
        System.out.println("\n3. Executing booking attempts:");
        System.out.println("-".repeat(40));

        // User 1 tries booking Room 2 from 30/06/2026 to 07/07/2026 (7 nights)
        System.out.println("\nAttempt 1: User 1 booking Room 2 (30/06/2026 to 07/07/2026):");
        try {
            Date checkIn1 = createDate(2026, 5, 30);  // Month 5 = June (0-indexed)
            Date checkOut1 = createDate(2026, 6, 7);  // Month 6 = July
            service.bookRoom(1, 2, checkIn1, checkOut1);
        } catch (Exception e) {
            System.out.println("Expected result: " + e.getMessage());
        }

        // User 1 tries booking Room 2 from 07/07/2026 to 30/06/2026 (invalid dates)
        System.out.println("\nAttempt 2: User 1 booking Room 2 (07/07/2026 to 30/06/2026 - Invalid dates):");
        try {
            Date checkIn2 = createDate(2026, 6, 7);   // July 7
            Date checkOut2 = createDate(2026, 5, 30); // June 30 (before check-in)
            service.bookRoom(1, 2, checkIn2, checkOut2);
        } catch (Exception e) {
            System.out.println("Expected result: " + e.getMessage());
        }

        // User 1 tries booking Room 1 from 07/07/2026 to 08/07/2026 (1 night)
        System.out.println("\nAttempt 3: User 1 booking Room 1 (07/07/2026 to 08/07/2026):");
        try {
            Date checkIn3 = createDate(2026, 6, 7);   // July 7
            Date checkOut3 = createDate(2026, 6, 8);  // July 8
            service.bookRoom(1, 1, checkIn3, checkOut3);
        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        // User 2 tries booking Room 1 from 07/07/2026 to 09/07/2026 (2 nights)
        System.out.println("\nAttempt 4: User 2 booking Room 1 (07/07/2026 to 09/07/2026):");
        try {
            Date checkIn4 = createDate(2026, 6, 7);   // July 7
            Date checkOut4 = createDate(2026, 6, 9);  // July 9
            service.bookRoom(2, 1, checkIn4, checkOut4);
        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        // User 2 tries booking Room 3 from 07/07/2026 to 08/07/2026 (1 night)
        System.out.println("\nAttempt 5: User 2 booking Room 3 (07/07/2026 to 08/07/2026):");
        try {
            Date checkIn5 = createDate(2026, 6, 7);   // July 7
            Date checkOut5 = createDate(2026, 6, 8);  // July 8
            service.bookRoom(2, 3, checkIn5, checkOut5);
        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        // Step 4: Update Room 1 to suite type with price 10000
        System.out.println("\n4. Updating Room 1:");
        System.out.println("-".repeat(40));
        service.setRoom(1, RoomType.SUITE, 10000);

        // Step 5: Print all data
        System.out.println("\n5. Final Results:");
        System.out.println("-".repeat(40));

        service.printAll();
        service.printAllUsers();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST CASE EXECUTION COMPLETED");
        System.out.println("=".repeat(80));
    }

    /**
     * Helper method to create Date objects
     */
    @SuppressWarnings("deprecation")
    private static Date createDate(int year, int month, int day) {
        return new Date(year - 1900, month, day);  // Year is 1900-based, month is 0-based
    }
}

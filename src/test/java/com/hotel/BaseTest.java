package com.hotel;

import com.skypay.hotel.service.Service;
import org.junit.jupiter.api.BeforeEach;

import java.util.Date;

/**
 * Base test class containing common setup and utility methods
 */
public abstract class BaseTest {

    protected Service service;
    protected Date checkInDate;
    protected Date checkOutDate;
    protected Date invalidCheckOutDate;

    @BeforeEach
    void setUp() {
        service = new Service();

        // Setup test dates
        checkInDate = createDate(2026, 6, 7);      // July 7, 2026
        checkOutDate = createDate(2026, 6, 9);     // July 9, 2026
        invalidCheckOutDate = createDate(2026, 6, 6); // July 6, 2026 (before check-in)
    }

    /**
     * Helper method to create Date objects for testing
     */
    @SuppressWarnings("deprecation")
    protected Date createDate(int year, int month, int day) {
        return new Date(year - 1900, month, day);
    }
}
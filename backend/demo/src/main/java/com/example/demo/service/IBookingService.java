package com.example.demo.service;

import com.example.demo.pojo.Booking;
import java.util.List;


public interface IBookingService {
    void cancelBooking(Long bookingId);

    List<Booking> getAllBookingsByRoomId(Long roomId);

    String saveBooking(Long roomId, Booking bookingRequest);

    Booking findByBookingConfirmationCode(String confirmationCode);

    List<Booking> getAllBookings();

    List<Booking> getBookingsByUserEmail(String email);
}


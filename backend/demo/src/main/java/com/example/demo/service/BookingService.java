package com.example.demo.service;

import com.example.demo.exception.InvalidBookingRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.pojo.Booking;
import com.example.demo.pojo.Room;
import com.example.demo.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final BookingRepository bookingRepository;
    private final IRoomService roomService;


    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }


    @Override
    public List<Booking> getBookingsByUserEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public List<Booking> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public String saveBooking(Long roomId, Booking bookingRequest) {
        // Validate that the check-in date is before the check-out date
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must come before check-out date.");
        }

        // Get the room by ID
        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new InvalidBookingRequestException("Room not found with ID: " + roomId));

        // Get existing bookings for the room
        List<Booking> existingBookings = room.getBookings();
        
        // Check if the room is available for the selected dates
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if (!roomIsAvailable) {
            throw new InvalidBookingRequestException("Sorry, This room is not available for the selected dates.");
        }

        // Generate and set the confirmation code
        String confirmationCode = generateConfirmationCode();
        bookingRequest.setBookingConfirmationCode(confirmationCode);

        // Add the booking to the room's booking list
        room.addBooking(bookingRequest);

        // Save the booking in the repository
        bookingRepository.save(bookingRequest);

        // Log the confirmation code for debugging purposes
        System.out.println("Generated Confirmation Code: " + confirmationCode);

        // Return the confirmation code
        return confirmationCode;
    }

    // Generate a unique confirmation code (this can be customized)
    private String generateConfirmationCode() {
        return UUID.randomUUID().toString();  // Example using UUID, or you can use other methods
    }


    @Override
    public Booking findByBookingConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("No booking found with booking code :"+confirmationCode));

    }


    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }




}


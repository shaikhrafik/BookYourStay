package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.InvalidBookingRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.pojo.Booking;
import com.example.demo.pojo.Room;
import com.example.demo.response.BookingResponse;
import com.example.demo.response.RoomResponse;
import com.example.demo.service.IBookingService;
import com.example.demo.service.IRoomService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final IBookingService bookingService;
    private final IRoomService roomService;

    @GetMapping("/all-bookings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings){
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody Booking bookingRequest) {
        try {
            // Save booking and get confirmation code
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);

            // Return a successful response with confirmation code
            return ResponseEntity.ok("Room booked successfully. Your booking confirmation code is: " + confirmationCode);

        } catch (InvalidBookingRequestException e) {
            // Handle invalid booking request (e.g., invalid dates or room unavailability)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{
            Booking booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email) {
        List<Booking> bookings = bookingService.getBookingsByUserEmail(email);
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
    }

    private BookingResponse getBookingResponse(Booking booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice());
        return new BookingResponse(
                booking.getBookingId(), booking.getCheckInDate(),
                booking.getCheckOutDate(),booking.getGuestFullName(),
                booking.getGuestEmail(), booking.getNumOfAdults(),
                booking.getNumOfChildren(), booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(), room);
    }
}

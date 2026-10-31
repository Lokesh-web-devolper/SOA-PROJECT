package com.parkgrid.booking.controller;

import com.parkgrid.booking.dto.ApiResponse;
import com.parkgrid.booking.dto.BookingDto;
import com.parkgrid.booking.dto.CreateBookingRequest;
import com.parkgrid.booking.dto.SlotAvailabilityDto;
import com.parkgrid.booking.entity.BookingStatus;
import com.parkgrid.booking.security.JwtUtil;
import com.parkgrid.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking & Reservations", description = "Endpoints for reserving parking slots, check-in, check-out, and booking lifecycle management")
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    public BookingController(BookingService bookingService, JwtUtil jwtUtil) {
        this.bookingService = bookingService;
        this.jwtUtil = jwtUtil;
    }

    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return jwtUtil.getUserIdFromToken(header.substring(7));
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

    private String extractUserEmail(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return jwtUtil.getEmailFromToken(header.substring(7));
        }
        return "customer@parkgrid.com";
    }

    private String extractUserRole(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return jwtUtil.getRoleFromToken(header.substring(7));
        }
        return "ROLE_CUSTOMER";
    }

    private String extractCustomerName(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return jwtUtil.getFullNameFromToken(header.substring(7));
        }
        return null;
    }

    @GetMapping("/availability")
    @Operation(summary = "Check unavailable slot IDs for a given facility and time window based on active reservations")
    public ResponseEntity<ApiResponse<SlotAvailabilityDto>> checkAvailability(
            @RequestParam Long facilityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        SlotAvailabilityDto availability = bookingService.checkAvailability(facilityId, startTime, endTime);
        return ResponseEntity.ok(ApiResponse.success("Slot availability fetched successfully", availability));
    }

    @PostMapping
    @Operation(summary = "Create a new advance slot booking (concurrency-safe)")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        String userEmail = extractUserEmail(servletRequest);
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            request.setCustomerName(extractCustomerName(servletRequest));
        }
        BookingDto booking = bookingService.createBooking(userId, userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking confirmed successfully", booking));
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get booking history for authenticated customer")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getMyBookings(HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        List<BookingDto> bookings = bookingService.getMyBookings(userId);
        return ResponseEntity.ok(ApiResponse.success("Bookings fetched successfully", bookings));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details by ID (with ownership verification)")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingById(@PathVariable Long id, HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        String role = extractUserRole(servletRequest);
        BookingDto booking = bookingService.getBookingById(id, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Booking fetched successfully", booking));
    }

    @GetMapping
    @Operation(summary = "Admin: View all reservations with optional filters")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getAllBookings(
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) BookingStatus status) {
        List<BookingDto> bookings = bookingService.getAllBookings(facilityId, status);
        return ResponseEntity.ok(ApiResponse.success("All bookings retrieved successfully", bookings));
    }

    @PutMapping("/{id}/check-in")
    @Operation(summary = "Customer check-in upon arrival at parking slot")
    public ResponseEntity<ApiResponse<BookingDto>> checkIn(@PathVariable Long id, HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        String role = extractUserRole(servletRequest);
        BookingDto booking = bookingService.checkIn(id, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Checked in successfully. Slot occupied.", booking));
    }

    @PutMapping("/{id}/check-out")
    @Operation(summary = "Customer check-out upon exit (triggers automated invoice creation)")
    public ResponseEntity<ApiResponse<BookingDto>> checkOut(@PathVariable Long id, HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        String role = extractUserRole(servletRequest);
        BookingDto booking = bookingService.checkOut(id, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Checked out successfully. Slot released and billing finalized.", booking));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel upcoming or pending reservation")
    public ResponseEntity<ApiResponse<BookingDto>> cancelBooking(@PathVariable Long id, HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        String role = extractUserRole(servletRequest);
        BookingDto booking = bookingService.cancelBooking(id, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }
}

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(CreateBookingDto createBookingDto, Long userId, Long itemId);

    BookingDto update(Long userId, Long bookingId, boolean approved);

    BookingDto getById(Long id, Long userId);

    List<BookingDto> getAllByBookerId(Long userId, String state);

    List<BookingDto> getAllByOwnerId(Long userId, String state);
}
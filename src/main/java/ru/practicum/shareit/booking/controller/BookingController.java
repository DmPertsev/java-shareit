package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              @RequestHeader(name = Variables.HEADER) Long bookerId) {
        log.info("Получен запрос GET /bookings/{}.", bookingId);

        return bookingService.getById(bookingId, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(name = Variables.HEADER) Long ownerId,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /bookings/owner?state={}&from={}&size={}.", state, from, size);

        return bookingService.getAllByOwnerId(ownerId, state, from, size);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader(name = Variables.HEADER) Long bookerId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /bookings?state={}&from={}&size={}.", state, from, size);

        return bookingService.getAllByBookerId(bookerId, state, from, size);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(name = Variables.HEADER) Long userId,
                             @Valid @RequestBody CreateBookingDto createBookingDto) {
        log.info("Получен запрос POST /bookings");

        return bookingService.create(createBookingDto, userId, createBookingDto.getItemId());
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(name = Variables.HEADER) Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam boolean approved) {
        log.info("Получен запрос PATCH /bookings/{}.", bookingId);

        return bookingService.update(userId, bookingId, approved);
    }
}
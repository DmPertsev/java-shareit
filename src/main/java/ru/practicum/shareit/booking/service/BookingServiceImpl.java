package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException("Такого бронирования нет!"));

        if (booking.getBooker().getId().equals(userId)
                || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toDto(booking);
        } else {
            throw new UserNotFoundException("Пользователь не является арендатором или владельцем вещи!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwnerId(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Такого пользователя нет");
        }

        try {
            List<Booking> bookings;
            switch (State.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findAllByItemOwnerId(userId,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId,
                            LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
            return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByBookerId(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Такого пользователя нет!");
        }

        try {

            List<Booking> bookings;
            switch (State.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findAllByBookerId(userId,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                            LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
            return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }


    @Transactional
    @Override
    public BookingDto create(CreateBookingDto createBookingDto, Long userId, Long itemId) {
        Booking booking = BookingMapper.toBooking(createBookingDto);
        setUserAndItemForBooking(booking, userId, itemId);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public BookingDto update(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Такого бронирования нет!"));

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new UnavailableException("Бронирование уже подтверждено!");
        }

        if (!itemRepository.findById(booking.getItem().getId()).orElseThrow().getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователь не является владельцем вещи");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return BookingMapper.toDto(booking);
    }

    private void setUserAndItemForBooking(Booking booking, Long userId, Long itemId) {
        booking.setBooker(userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Такого пользователя нет!")));

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Такой вещи нет!"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }

        if (userId.equals(item.getOwner().getId())) {
            throw new ItemNotFoundException("Вещь не может быть забронировать ее владелец");
        }

        booking.setItem(item);
    }
}
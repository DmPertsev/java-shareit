package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
                () -> new ObjectNotFoundException("Такого бронирования нет!"));

        if (booking.getBooker().getId().equals(userId)
                || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toDto(booking);
        } else {
            throw new ObjectNotFoundException("Пользователь не является арендатором или владельцем вещи!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwnerId(Long userId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователя нет: " + userId);
        }

        try {
            List<Booking> bookings;
            Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
            switch (State.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findAllByItemOwnerId(userId, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId,
                            LocalDateTime.now(), LocalDateTime.now(), pageable);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, LocalDateTime.now(),
                            pageable);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, LocalDateTime.now(),
                            pageable);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, pageable);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, pageable);
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
    public List<BookingDto> getAllByBookerId(Long userId, String state, Integer from, Integer size) {

        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователя нет: " + userId);
        }

        try {

            List<Booking> bookings;
            Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
            switch (State.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findAllByBookerId(userId, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                            LocalDateTime.now(), pageable);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), pageable);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, pageable);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, pageable);
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
                () -> new ObjectNotFoundException("Бронирования нет: " + bookingId));

        if (booking.getItem().getOwner().getId().equals(userId)) {
            Status status = booking.getStatus();
            switch (status) {
                case APPROVED:
                    throw new ValidationException("Статус 'APPROVED'");
                case CANCELED:
                    throw new ValidationException("Статус 'CANCELED'");
                default:
                    if (approved) {
                        booking.setStatus(Status.APPROVED);
                    } else {
                        booking.setStatus(Status.REJECTED);
                    }
            }
        } else {
            throw new ObjectNotFoundException("Пользователь не является владельцем Item");
        }
        return BookingMapper.toDto(booking);
    }

    private void setUserAndItemForBooking(Booking booking, Long userId, Long itemId) {
        booking.setBooker(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователя нет: " + userId)));

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Вещи нет: " + itemId));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }

        if (userId.equals(item.getOwner().getId())) {
            throw new ObjectNotFoundException("Вещь не может быть забронировать ее владелец");
        }

        booking.setItem(item);
    }
}
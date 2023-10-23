package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long userId, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime t1,
                                                             LocalDateTime t2, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long userId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime t1,
                                                                LocalDateTime t2, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long userId, Status waiting, Sort sort);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStatus(Long itemId, Status approved,Sort sort);

    Optional<Booking> findFirstByItemIdInAndStartLessThanEqualAndStatus(List<Long> idItems, LocalDateTime now,
                                                                        Status approved, Sort sort);

    Optional<Booking> findFirstByItemIdInAndStartAfterAndStatus(List<Long> idItems, LocalDateTime now,
                                                                Status approved, Sort sort);
}
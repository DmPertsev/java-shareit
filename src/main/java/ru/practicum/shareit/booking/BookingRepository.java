package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime t1, LocalDateTime t2,
                                                             Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long userId, Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime t1, LocalDateTime t2,
                                                                Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(Long userId, Status waiting, Pageable pageable);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStatus(Long itemId, Status approved, Sort sort);

    Optional<Booking> findFirstByItemIdInAndStartLessThanEqualAndStatus(List<Long> idItems, LocalDateTime now,
                                                                        Status approved, Sort sort);

    Optional<Booking> findFirstByItemIdInAndStartAfterAndStatus(List<Long> idItems, LocalDateTime now,
                                                                Status approved, Sort sort);
}
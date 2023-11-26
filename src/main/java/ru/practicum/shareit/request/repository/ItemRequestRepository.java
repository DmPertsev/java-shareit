package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(Long userId, Pageable pageable);

    @Query("SELECT i FROM ItemRequest i " +
            "WHERE i.requester.id <> ?1 ")
    List<ItemRequest> findAllByUserId(Long userId, Pageable pageable);
}
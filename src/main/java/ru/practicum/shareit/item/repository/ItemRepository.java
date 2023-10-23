package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner(User owner);

    @Query("SELECT i from Item i " +
            "WHERE upper(i.name) like upper(concat('%', ?1, '%')) " +
            "OR upper(i.description) like upper(concat('%', ?1, '%')) " +
            "AND i.available = true ")
    List<Item> search(String text);
}
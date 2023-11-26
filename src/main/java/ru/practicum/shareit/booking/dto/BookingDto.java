package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Item item;

    private Booker booker;

    private Status status;

    @Data
    public static class Booker {
        private final Long id;
        private final String name;
    }

    @Data
    public static class Item {
        private final Long id;
        private final String name;
    }
}
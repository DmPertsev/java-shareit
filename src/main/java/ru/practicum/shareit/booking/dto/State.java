package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.exception.InvalidStatusException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State validateState(String value) throws InvalidStatusException  {
        try {
            return State.valueOf(value);
        } catch (RuntimeException exception) {
            throw new InvalidStatusException("Unknown state: " + value);
        }
    }
}
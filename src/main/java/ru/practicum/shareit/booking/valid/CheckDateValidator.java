package ru.practicum.shareit.booking.valid;

import ru.practicum.shareit.booking.dto.CreateBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, CreateBookingDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreateBookingDto createBookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = createBookingDto.getStart();
        LocalDateTime end = createBookingDto.getEnd();
        if ((start == null)
                || (end == null)
                || (start == end)) {
            return false;
        }

        return start.isBefore(end);
    }
}


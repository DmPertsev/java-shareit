package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор бронирования

    @Column(name = "start_date")
    private LocalDateTime start; // дата и время начала бронирования

    @Column(name = "end_date")
    private LocalDateTime end; // дата и время конца бронирования

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; // вещь которую пользователь бронирует

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker; // пользователь, который осуществляет бронирование

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status; // статус бронирования

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        return id != null && (id.equals(((Booking) o).getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
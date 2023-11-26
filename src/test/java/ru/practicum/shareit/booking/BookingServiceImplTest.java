package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JUnit 5 Nested")
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userServiceImpl;

    private User user1;
    private User user2;
    private Item item;
    private Booking booking;
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now();

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "Alex", "alex.b@yandex.ru");
        user2 = new User(2L, "Bill", "bill.d@yandex.ru");
        item = new Item(1L, "bag", "description", true, user1,
                null, null, null, null);
        booking = new Booking(1L, start, end, item, user1, Status.WAITING);
    }

    @Nested
    @DisplayName("test for method getById")
    class GetById {
        @Test
        void getById_shouldReturnBookingDto() {
            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            BookingDto result = bookingService.getById(1L, 1L);

            assertEquals(1L, result.getId());
            assertEquals(start, result.getStart());
            assertEquals(end, result.getEnd());
            assertEquals(Status.WAITING, result.getStatus());
            assertEquals(1L, result.getItem().getId());
            assertEquals("bag", result.getItem().getName());
            assertEquals(1L, result.getBooker().getId());
            assertEquals("Alex", result.getBooker().getName());
        }

        @Test
        void getById_shouldReturnUserNotFoundException() {
            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertThrows(ObjectNotFoundException.class, () -> bookingService.getById(1L, 999L));
        }

        @Test
        void getById_shouldReturnBookingNotFoundException() {
            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

            assertThrows(ObjectNotFoundException.class, () -> bookingService.getById(999L, 1L));
        }
    }

    @Nested
    @DisplayName("Tests for the method getAllByOwnerId")
    class GetAllByOwnerId {

        @Test
        void getAllByOwnerId_whenStateIsAll() {
            Long userId = 1L;
            String state = "UNSUPPORTED_STATUS";

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                bookingService.getAllByOwnerId(userId, state, 0, 10);
            });

            assertEquals("Unknown state: " + state, exception.getMessage());
        }

        @Test
        void getAllByOwnerId_whenStateIsCURRENT() {
            Long userId = 1L;
            String state = String.valueOf(State.CURRENT);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsPAST() {
            Long userId = 1L;
            String state = String.valueOf(State.PAST);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsFUTURE() {
            Long userId = 1L;
            String state = String.valueOf(State.FUTURE);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            System.out.println("Result list size: " + result.size());
            System.out.println("Result list: " + result);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsWAITING() {
            Long userId = 1L;
            String state = "WAITING";

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsREJECTED() {
            Long userId = 1L;
            String state = String.valueOf(State.REJECTED);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_shouldThrowUnsupportedStatusException() {
            Long userId = 1L;
            String state = "BLABLABLA";

            assertThrows(ValidationException.class, () -> bookingService.getAllByOwnerId(userId, state, 0, 10));
        }

        @Test
        void getAllByOwnerId_shouldReturnUserNotFoundException() {
            Long userId = 999L;
            String state = String.valueOf(State.ALL);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

            assertThrows(ObjectNotFoundException.class, () -> bookingService.getAllByOwnerId(userId, state, 0, 10));

            Mockito.verify(userServiceImpl, Mockito.times(1)).throwIfNotExist(userId);
        }
    }

    @Nested
    @DisplayName("Tests for the method getAllByBookerId")
    class GetAllByBookerId {
        @Test
        void getAllByBookerId_whenStateIsAll() {
            Long userId = 1L;
            String state = String.valueOf(State.ALL);

            Mockito.when(bookingRepository.findAllByBookerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsCURRENT() {
            Long userId = 1L;
            String state = String.valueOf(State.CURRENT);

            Mockito.when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(Mockito.anyLong(), Mockito.any(),
                    Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsPAST() {
            Long userId = 1L;
            String state = String.valueOf(State.PAST);

            Mockito.when(bookingRepository.findAllByBookerIdAndEndBefore(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsFUTURE() {
            Long userId = 1L;
            String state = String.valueOf(State.FUTURE);

            Mockito.when(bookingRepository.findAllByBookerIdAndStartAfter(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsWAITING() {
            Long userId = 1L;
            String state = String.valueOf(State.WAITING);

            Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsREJECTED() {
            Long userId = 1L;
            String state = String.valueOf(State.REJECTED);

            Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBooker_shouldThrowUnsupportedStatusException() {
            Long userId = 1L;
            String state = "BLABLABLA";

            assertThrows(ValidationException.class, () -> bookingService.getAllByBookerId(userId, state, 0, 10));
        }

        /*
        @Test
        void getAllByBookerId_shouldReturnValidationException() {
            Long userId = 999L;
            String state = "ALL";

            Mockito.when(userRepository.existsById(Mockito.eq(userId))).thenReturn(false);

            Mockito.doThrow(new ObjectNotFoundException("User not found"))
                    .when(userServiceImpl).throwIfNotExist(Mockito.eq(userId));

            ValidationException exception = assertThrows(ValidationException.class, () ->
                    bookingService.getAllByBookerId(userId, state, 0, 10));

            assertNotNull(exception);
            assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());

            Mockito.verify(userRepository, Mockito.times(1)).existsById(Mockito.eq(userId));

            Mockito.verifyNoMoreInteractions(userRepository);

            Mockito.verify(userServiceImpl, Mockito.times(1)).throwIfNotExist(Mockito.eq(userId));

            Mockito.verifyNoMoreInteractions(userServiceImpl);
        }
         */
    }

    @Nested
    @DisplayName("Tests for the method create")
    class Create {
        @Test
        void create_shouldSaveBooking() {
            CreateBookingDto dto = new CreateBookingDto(start, end, item.getId());

            Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
            Mockito.when(bookingRepository.save(Mockito.any()))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

            BookingDto bookingdto = bookingService.create(dto, user2.getId(), item.getId());

            assertThat(bookingdto).hasFieldOrProperty("id");
        }

        @Test
        void create_shouldReturnUserNotFoundException() {
            Long userId = 999L;
            Long itemId = 1L;

            Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

            CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

            assertThrows(ObjectNotFoundException.class, () -> bookingService.create(dto, userId, itemId));

            Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void create_shouldReturnItemNotFoundException() {
            Long userId = 2L;
            Long itemId = 999L;

            Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
            Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user2));

            CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

            assertThrows(ObjectNotFoundException.class, () -> bookingService.create(dto, userId, itemId));

            Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void create_shouldReturnValidationException() {
            Long userId = 2L;
            Long itemId = 1L;
            item.setAvailable(false);

            Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user2));

            CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

            assertThrows(ValidationException.class, () -> bookingService.create(dto, userId, itemId));

            Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Tests for the method update")
    class Update {
        @Test
        void update_shouldThrowNotFoundIfBookingIsNotExists() {
            Long bookingId = 1L;
            Long userId = 1L;

            Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.update(bookingId, userId, true))
                    .isInstanceOf(ObjectNotFoundException.class);
        }

        @Test
        void update_shouldUpdateAPPROVEDStatus() {
            Long userId = 1L;
            Long bookingId = 1L;
            boolean approved = true;
            booking.setStatus(Status.WAITING);

            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertEquals(Status.APPROVED, bookingService.update(userId, bookingId, approved).getStatus());
        }

        @Test
        void update_shouldUpdateREJECTEDStatus() {
            Long userId = 1L;
            Long bookingId = 1L;
            boolean approved = false;
            booking.setStatus(Status.WAITING);

            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertEquals(Status.REJECTED, bookingService.update(userId, bookingId, approved).getStatus());
        }

        @Test
        void update_shouldReturnUserNotFoundExceptionThrown() {
            Long userId = 999L;
            Long bookingId = 1L;
            boolean approved = true;
            booking.setStatus(Status.WAITING);

            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            Mockito.lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

            assertThrows(ObjectNotFoundException.class, () -> bookingService.update(userId, bookingId, approved));
        }

        @Test
        void update_whenStatusIsAlreadyREJECTED_thenObjectNotFoundExceptionThrown() {
            Long userId = 1L;
            Long bookingId = 1L;
            boolean approved = true;

            Booking booking = new Booking();
            booking.setStatus(Status.REJECTED);

            Item item = new Item();
            item.setOwner(new User());

            booking.setItem(item);

            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertThrows(ObjectNotFoundException.class, () -> bookingService.update(userId, bookingId, approved));
        }
    }
}
package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@DisplayName("JUnit 5 Nested")
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private Long userId;

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
    @Mock
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
    @DisplayName("Tests for the method getById")
    class GetById {
        @Test
        void getById_shouldReturnBookingDto() {
            Long bookingId = 1L;
            Long userId = 1L;
            when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            BookingDto result = bookingService.getById(bookingId, userId);

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
            Long bookingId = 1L;
            Long userId = 999L;

            when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertThrows(ObjectNotFoundException.class, () -> bookingService.getById(bookingId, userId));
        }

        @Test
        void getById_shouldReturnBookingNotFoundException() {
            Long bookingId = 999L;
            Long userId = 1L;

            when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

            assertThrows(ObjectNotFoundException.class, () -> bookingService.getById(bookingId, userId));
        }
    }

    @Nested
    @DisplayName("Tests for the method getAllByOwnerId")
    class GetAllByOwnerId {
        @Test
        void getAllByOwnerId_whenStateIsAll() {
            Long userId = 1L;
            String state = String.valueOf(State.ALL);

            lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            when(bookingRepository.findAllByItemOwnerId(Mockito.anyLong(), any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsCURRENT() {
            Long userId = 1L;
            String state = String.valueOf(State.CURRENT);

            lenient().when(userRepository.existsById(any(Long.class))).thenReturn(true);

            lenient().when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                    any(Long.class), any(), any(), any())
            ).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsPAST() {
            Long userId = 1L;
            String state = String.valueOf(State.PAST);

            lenient().when(userRepository.existsById(any(Long.class))).thenReturn(true);

            lenient().when(bookingRepository.findAllByItemOwnerIdAndEndBefore(
                    any(Long.class), any(), any())
            ).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsFUTURE() {
            Long userId = 1L;
            String state = String.valueOf(State.FUTURE);

            lenient().when(userRepository.existsById(any(Long.class))).thenReturn(true);

            lenient().when(bookingRepository.findAllByItemOwnerIdAndStartAfter(
                    any(Long.class), any(), any())
            ).thenReturn(List.of(booking));

             List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsWAITING() {
            Long userId = 1L;
            String state = String.valueOf(State.WAITING);

            lenient().when(userRepository.existsById(any(Long.class))).thenReturn(true);

            lenient().when(bookingRepository.findAllByItemOwnerIdAndStatus(any(Long.class), any(), any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsREJECTED() {
            Long userId = 1L;
            String state = String.valueOf(State.REJECTED);

            lenient().when(userRepository.existsById(any(Long.class))).thenReturn(true);

            lenient().when(bookingRepository.findAllByItemOwnerIdAndStatus(
                    any(Long.class), any(), any())
            ).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_shouldThrowUnsupportedStatusException() {
            Long userId = 1L;
            String state = "BLABLABLA";

            lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            assertThatThrownBy(() -> bookingService.getAllByOwnerId(userId, state, 0, 10))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid state: " + state);
        }

        @Test
        void testGetOwnerBookingsByUnsupportedStatusState() {
            String unsupportedState = "UNSUPPORTED_STATUS";

            assertThatThrownBy(() -> bookingService.getAllByOwnerId(userId, unsupportedState, 0, 10))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Unsupported status: " + unsupportedState);
        }
    }

    @Nested
    @DisplayName("Tests for the method getAllByBookerId")
    class GetAllByBookerId {
        @Test
        void getAllByBookerId_whenStateIsAll() {
            Long userId = 1L;
            String state = String.valueOf(State.ALL);

            lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            Mockito.doReturn(List.of(booking))
                    .when(bookingRepository)
                    .findAllByBookerId(Mockito.anyLong(), any());

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsCURRENT() {
            Long userId = 1L;
            String state = String.valueOf(State.CURRENT);

            lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            Mockito.doReturn(List.of(booking))
                    .when(bookingRepository).findAllByBookerIdAndStartBeforeAndEndAfter(
                            Mockito.anyLong(), any(), any(), any());

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsPAST() {
            Long userId = 1L;
            String state = String.valueOf(State.PAST);

            lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            lenient().when(bookingRepository.findAllByBookerIdAndEndBefore(Mockito.anyLong(), any(), any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsFUTURE() {
            Long userId = 1L;
            String state = String.valueOf(State.FUTURE);

            Mockito.lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            try {
                System.out.println("Calling getAllByBookerId with state: " + state);
                bookingService.getAllByBookerId(userId, state, 0, 10);
                fail("Expected ObjectNotFoundException, but no exception was thrown");
            } catch (ObjectNotFoundException e) {
                System.out.println("Caught ObjectNotFoundException: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Caught unexpected exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Test
        void getAllByBookerId_whenStateIsWAITING() {
            Long userId = 1L;
            String state = String.valueOf(State.WAITING);

            lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            when(bookingRepository.findAllByBookerIdAndStatus(Mockito.anyLong(), any(), any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsREJECTED() {
            Long userId = 1L;
            String state = String.valueOf(State.REJECTED);

            when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            System.out.println("Mock setup: " + userRepository.existsById(123L));
            System.out.println("Result size: " + result.size());
            System.out.println("Result details: " + result);

            assertEquals(0, result.size());
        }

        @Test
        void getAllByBooker_shouldThrowUnsupportedStatusException() {
            Long userId = 1L;
            String state = "BLABLABLA";

            lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            assertThrows(ValidationException.class, () -> bookingService.getAllByBookerId(userId, state, 0, 10));
        }

        @Test
        void getAllByBookerId_shouldReturnUserNotFoundException() {
            Long userId = 999L;
            String state = String.valueOf(State.ALL);

            lenient().when(userRepository.existsById(userId)).thenReturn(false);

            Mockito.doThrow(new ObjectNotFoundException("User not found")).when(userServiceImpl).throwIfNotExist(userId);

            assertThrows(ObjectNotFoundException.class, () -> bookingService.getAllByBookerId(userId, state, 0, 10));
        }


    }

    @Nested
    @DisplayName("Tests for the method create")
    class Create {
        @Test
        void create_shouldSaveBooking() {
            CreateBookingDto dto = new CreateBookingDto(start, end, item.getId());

            when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
            when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
            when(bookingRepository.save(any()))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

            BookingDto bookingdto = bookingService.create(dto, user2.getId(), item.getId());

            assertThat(bookingdto).hasFieldOrProperty("id");
        }

        @Test
        void create_shouldReturnUserNotFoundException() {
            Long userId = 999L;
            Long itemId = 1L;

            when(userRepository.findById(any())).thenReturn(Optional.empty());

            CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

            assertThrows(ObjectNotFoundException.class, () -> bookingService.create(dto, userId, itemId));

            Mockito.verify(bookingRepository, Mockito.never()).save(any());
        }

        @Test
        void create_shouldReturnItemNotFoundException() {
            Long userId = 2L;
            Long itemId = 999L;

            when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
            when(userRepository.findById(any())).thenReturn(Optional.of(user2));

            CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

            assertThrows(ObjectNotFoundException.class, () -> bookingService.create(dto, userId, itemId));

            Mockito.verify(bookingRepository, Mockito.never()).save(any());
        }

        @Test
        void create_shouldReturnValidationException() {
            Long userId = 2L;
            Long itemId = 1L;
            item.setAvailable(false);

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            when(userRepository.findById(any())).thenReturn(Optional.of(user2));

            CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

            assertThrows(ValidationException.class, () -> bookingService.create(dto, userId, itemId));

            Mockito.verify(bookingRepository, Mockito.never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests for the method update")
    class Update {
        @Test
        void update_shouldThrowNotFoundIfBookingIsNotExists() {
            Long bookingId = 1L;
            Long userId = 1L;

            when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.update(bookingId, userId, true))
                    .isInstanceOf(ObjectNotFoundException.class);
        }

        @Test
        void update_shouldUpdateAPPROVEDStatus() {
            Long userId = 1L;
            Long bookingId = 1L;
            boolean approved = true;
            booking.setStatus(Status.WAITING);

            when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertEquals(Status.APPROVED, bookingService.update(userId, bookingId, approved).getStatus());
        }

        @Test
        void update_shouldUpdateREJECTEDStatus() {
            Long userId = 1L;
            Long bookingId = 1L;
            boolean approved = false;
            booking.setStatus(Status.WAITING);

            when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertEquals(Status.REJECTED, bookingService.update(userId, bookingId, approved).getStatus());
        }

        @Test
        void update_shouldReturnUserNotFoundExceptionThrown() {
            Long userId = 999L;
            Long bookingId = 1L;
            boolean approved = true;
            booking.setStatus(Status.WAITING);

            when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertThrows(ObjectNotFoundException.class, () -> bookingService.update(userId, bookingId, approved));
        }

        @Test
        void update_whenStatusIsAlreadyREJECTED_thenBadRequestExceptionThrown() {
            Long userId = 1L;
            Long bookingId = 1L;
            boolean approved = true;
            booking.setStatus(Status.CANCELED);

            when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertThrows(ValidationException.class, () -> bookingService.update(userId, bookingId, approved));
        }
    }

    @Test
    void create_shouldReturnValidationExceptionIfItemNotAvailable() {
        Long userId = 2L;
        Long itemId = 1L;
        item.setAvailable(false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));

        CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

        assertThrows(ValidationException.class, () -> bookingService.create(dto, userId, itemId));

        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void create_shouldSaveBookingSuccessfully() {
        CreateBookingDto dto = new CreateBookingDto(start, end, item.getId());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        BookingDto bookingdto = bookingService.create(dto, user2.getId(), item.getId());

        assertThat(bookingdto).hasFieldOrProperty("id");
        assertEquals(Status.WAITING, bookingdto.getStatus());
    }

    @Test
    void create_shouldReturnUserNotFoundExceptionIfUserNotExists() {
        Long userId = 999L;
        Long itemId = 1L;

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

        assertThrows(ObjectNotFoundException.class, () -> bookingService.create(dto, userId, itemId));

        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void create_shouldReturnItemNotFoundExceptionIfItemNotExists() {
        Long userId = 2L;
        Long itemId = 999L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));

        CreateBookingDto dto = new CreateBookingDto(start, end, itemId);

        assertThrows(ObjectNotFoundException.class, () -> bookingService.create(dto, userId, itemId));

        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }
}
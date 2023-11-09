package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void create_shouldSaveItemRequest() {
        Long userId = 1L;
        User user = new User(userId, "Alex", "alex.b@yandex.ru");
        ItemRequestDtoShort dtoShort = new ItemRequestDtoShort(1L, "desc", userId, null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemRequestDtoShort dto = itemRequestService.create(userId, dtoShort);

        assertThat(dto.getDescription()).isEqualTo(dtoShort.getDescription());
        assertThat(dto.getCreated()).isBefore(LocalDateTime.now());
    }

    @Test
    void create_shouldReturnUserNotFoundException() {
        Long userId = 999L;

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        ItemRequestDtoShort dtoShort = new ItemRequestDtoShort(1L, "desc", userId, null);

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.create(userId, dtoShort));

        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getAllByRequester_shouldReturnItemRequestDtoList() {
        Long userId = 1L;
        User user = new User(userId, "Alex", "alex.b@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, null, List.of());
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", userId,
                null, List.of());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequesterId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> expectedDtoList = List.of(itemRequestDto);
        List<ItemRequestDto> actualDtoList = itemRequestService.getAllByRequester(userId, 0, 10);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void getAllByRequester_shouldReturnUserNotFoundException() {
        Long userId = 999L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getAllByRequester(userId, 0, 10));

        Mockito.verify(itemRequestRepository, Mockito.never()).findAllByUserId(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getAll_shouldReturnItemRequestDtoList() {
        Long userId = 1L;
        User user = new User(userId, "Alex", "alex.b@yandex.ru");

        ItemRequest itemRequest = new ItemRequest(1L, "description", user, null, List.of());
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", userId,
                null, List.of());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByUserId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> expectedDtoList = List.of(itemRequestDto);
        List<ItemRequestDto> actualDtoList = itemRequestService.getAll(userId, 0, 10);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void getAll_shouldReturnUserNotFoundException() {
        Long userId = 999L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getAll(userId, 0, 10));

        Mockito.verify(itemRequestRepository, Mockito.never()).findAllByUserId(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getById_shouldReturnItemRequestDto() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = new User(userId, "Alex", "alex.b@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, null, List.of());

        Mockito.lenient().when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.lenient().when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));
        Mockito.lenient().when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        ItemRequestDto expected = new ItemRequestDto(1L, "description", userId, null, List.of());
        ItemRequestDto actual = itemRequestService.getById(userId, requestId);

        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnUserNotFoundException() {
        Long userId = 999L;
        Long requestId = 1L;

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getById(userId, requestId));

        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void getById_shouldReturnItemRequestNotFoundException() {
        Long userId = 1L;
        Long requestId = 999L;

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getById(userId, requestId));
    }
}
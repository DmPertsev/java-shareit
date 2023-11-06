package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private CreateItemDto createItemDto;
    private UpdateItemDto updateItemDto;
    private ItemDto itemDto;
    private CreateCommentDto commentDto;
    private Item item;
    private Comment comment;
    private Booking booking;
    private ItemRequest itemRequest;
    private ItemDtoResponse itemDtoResponse;

    @BeforeEach
    void create() {
        user = new User(1L, "Alex", "alex.b@yandex.ru");

        item = new Item(1L, "bag", "description", true, user,
                null, null, null, null);

        createItemDto = new CreateItemDto(1L, "name", "description", true, 1L);

        updateItemDto = new UpdateItemDto(1L, "new name", "new description", false, 1L);

        itemDto = new ItemDto(1L, "name", "description", true, null,
                null, null, 1L);

        itemDtoResponse = new ItemDtoResponse(1L, "name", "description", true, null,
                null, null);

        itemRequest = new ItemRequest(1L, "description", new User(), null, null);

        booking = new Booking(1L, null, null, item, user, Status.WAITING);

        comment = new Comment(1L, "comment", item, user, null);

        commentDto = new CreateCommentDto("comment");
    }


    @Test
    void getByUserId_shouldReturnItemDtoList() {

        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<Item> page = new PageImpl<>(List.of(item));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Mockito.when(itemRepository.findAllByOwner(user, pageable)).thenReturn(page.toList());

        Mockito.when(bookingRepository.findFirstByItemIdInAndStartLessThanEqualAndStatus(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(commentRepository.findByItemIdIn(Mockito.any(), Mockito.any())).thenReturn(List.of(comment));

        itemDtoResponse = ItemMapper.toDtoResponse(item);
        itemDtoResponse.setComments(List.of(CommentMapper.toDtoResponse(comment)));
        itemDtoResponse.setLastBooking(BookingMapper.toDtoShortResponse(booking));
        itemDtoResponse.setNextBooking(BookingMapper.toDtoShortResponse(booking));

        List<ItemDtoResponse> expectedDtoList = List.of(itemDtoResponse);
        List<ItemDtoResponse> actualDtoList = itemService.getByUserId(userId, 0, 10);

        Assertions.assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void getByUserId_shouldReturnUserNotFoundException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Long userId = 999L;

        assertThrows(ObjectNotFoundException.class, () -> itemService.getByUserId(userId, 0, 10));
    }

    @Test
    void getById_shouldReturnItemDto() {
        Long userId = 1L;
        Long itemId = 1L;

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findAllByItemIdAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(commentRepository.findByItemId(Mockito.anyLong())).thenReturn(Collections.emptyList());

        assertThat(itemService.getById(itemId, userId)).isEqualTo(ItemMapper.toDto(item));
    }

    @Test
    void getById_shouldReturnItemNotFoundException() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Long itemId = 999L;
        Long userId = 1L;

        assertThrows(ObjectNotFoundException.class, () -> itemService.getById(itemId, userId));
    }

    @Test
    void search() {
        Mockito.when(itemRepository.search(Mockito.anyString(), Mockito.any()))
                .thenReturn(List.of(item));

        List<ItemDto> expectedDtoList = List.of(ItemMapper.toDto(item));
        List<ItemDto> actualDtoList = itemService.search("text", 0, 10);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void getByIdWithUserIsNotOwner_shouldFoundItemDtoWithoutBooking() {
        Long userId = 1L;
        Long itemId = 1L;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItemId(Mockito.any())).thenReturn(List.of(comment));

        itemDto = ItemMapper.toDto(item);
        itemDto.setComments(List.of(CommentMapper.toDto(comment)));


        ItemDto expectedDto = itemDto;
        ItemDto actualDto = itemService.getById(itemId, userId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void create_shouldSaveItemWithItemRequest() {
        Long userId = 1L;

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto expectedDto = new ItemDto(1L, "name", "description", true,
                null, null, null, 1L);
        ItemDto actualDto = itemService.create(userId, createItemDto);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void createWithOwnerNotFound_shouldReturnUserNotFoundException() {
        Long userId = 999L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.create(userId, createItemDto));

        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void update_shouldReturnUserNotFoundExceptionIfUserIsNotExists() {
        Long itemId = 1L;
        Long userId = 999L;

        Mockito.when(userRepository.findById(userId)).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() -> itemService.update(itemId, userId, null))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void update_shouldUpdateItemName() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getName()).isEqualTo("new name");
    }

    @Test
    void update_shouldUpdateItemDescription() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getDescription()).isEqualTo("new description");
    }

    @Test
    void update_shouldUpdateItemAvailable() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getAvailable()).isFalse();
    }

    @Test
    void createComment_shouldSaveComment() {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(List.of(new Booking()));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);

        CommentDto actualDdo = itemService.createComment(itemId, userId, commentDto);

        assertEquals(1L, actualDdo.getId());
        assertEquals("comment", actualDdo.getText());
        assertEquals("Alex", actualDdo.getAuthorName());
        assertNull(actualDdo.getCreated());
    }

    @Test
    void createCommentWithOwnerNotFound_shouldReturnUserNotFoundException() {
        Long itemId = 1L;
        Long userId = 999L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.createComment(itemId, userId, commentDto));

        Mockito.verify(itemRepository, Mockito.never()).findById(itemId);
        Mockito.verify(bookingRepository, Mockito.never())
                .findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createCommentWithItemNotFound_shouldReturnItemNotFoundException() {
        Long userId = 1L;
        Long itemId = 999L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.createComment(itemId, userId, commentDto));

        Mockito.verify(bookingRepository, Mockito.never())
                .findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createCommentWithBookingNotFound_shouldReturnUnavailableException() {
        Long userId = 1L;
        Long itemId = 999L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any())).thenReturn(List.of());

        assertThrows(UnavailableException.class, () -> itemService.createComment(itemId, userId, commentDto));

        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void delete_shouldDeleteItemAndReturnDeletedItem() {
        Long itemId = 1L;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThat(itemService.delete(itemId)).isEqualTo(ItemMapper.toDto(item));
    }
}
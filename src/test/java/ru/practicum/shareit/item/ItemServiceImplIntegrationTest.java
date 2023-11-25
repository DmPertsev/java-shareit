package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final ItemServiceImpl itemService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private Long itemId;
    private Long userId;
    private Long commentId;
    private User user;
    private Item item;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(null, "Alex", "alex.b@yandex.ru");
        user = userRepository.save(user);
        userId = user.getId();

        item = new Item(null, "item bag", "description", true, user,
                null, null, null, null);
        item = itemRepository.save(item);
        itemId = item.getId();

        booking = new Booking(null, LocalDateTime.now(), LocalDateTime.now(), item, user, Status.APPROVED);
        booking = bookingRepository.save(booking);

        comment = new Comment(null, "comment", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);
        commentId = comment.getId();
    }

    @Test
    void getByUserId() {
        Integer from = 0;
        Integer size = 10;

        ItemDtoResponse itemDtoResponse = ItemMapper.toDtoResponse(item);
        itemDtoResponse.setComments(List.of(CommentMapper.toDtoResponse(comment)));
        itemDtoResponse.setLastBooking(BookingMapper.toDtoShortResponse(booking));

        List<ItemDtoResponse> actualDtoList = itemService.getByUserId(userId, from, size);
        List<ItemDtoResponse> expectedDtoList = List.of(itemDtoResponse);

        Assertions.assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void search() {
        Integer from = 0;
        Integer size = 10;
        String text = "desc";

        ItemDto itemDto = ItemMapper.toDto(item);

        List<ItemDto> actualDtoList = itemService.search(text, from, size);
        List<ItemDto> expectedDtoList = List.of(itemDto);

        Assertions.assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void getById() {
        ItemDto itemDto = ItemMapper.toDto(item);
        itemDto.setComments(List.of(CommentMapper.toDto(comment)));
        itemDto.setLastBooking(BookingMapper.toDtoShort(booking));

        ItemDto actualDto = itemService.getById(itemId, userId);

        Assertions.assertEquals(itemDto, actualDto);
    }

    @Test
    void create() {
        CreateItemDto createItemDto = new CreateItemDto(null, "new", "description",
                true, null);

        ItemDto saveItem = itemService.create(userId, createItemDto);

        Assertions.assertEquals(itemId + 1, saveItem.getId());
        Assertions.assertEquals("new", saveItem.getName());
        Assertions.assertEquals(true, saveItem.getAvailable());
    }

    @Test
    void delete() {
        CreateItemDto createItemDto = new CreateItemDto(null, "new", "description",
                true, null);
        ItemDto saveItem = itemService.create(userId, createItemDto);
        Long id = saveItem.getId();

        itemService.delete(id);

        assertThatThrownBy(() -> itemService.getById(id, userId)).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void createComment() {
        comment = new Comment(null, "comment", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);

        Assertions.assertEquals(commentId + 1, comment.getId());
        Assertions.assertEquals("comment", comment.getText());
        Assertions.assertEquals(item, comment.getItem());
        Assertions.assertEquals(user, comment.getAuthor());
    }
}
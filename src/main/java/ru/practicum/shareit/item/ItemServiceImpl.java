package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoShortResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.CommentDtoResponse;
import ru.practicum.shareit.item.comment.CreateCommentDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CreateItemDto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional(readOnly = true)
    public List<ItemDtoResponse> getByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        List<ItemDtoResponse> itemsDtoResponse = itemRepository.findAllByOwner(userRepository.findById(userId)
                        .orElseThrow(() -> new ObjectNotFoundException("Такого пользователя нет!")), pageable)
                .stream().map(ItemMapper::toDtoResponse)
                .collect(Collectors.toList());

        List<Long> idItems = itemsDtoResponse.stream().map(ItemDtoResponse::getId).collect(Collectors.toList());

        Map<Long, BookingDtoShortResponse> lastBookings = bookingRepository.findFirstByItemIdInAndStartLessThanEqualAndStatus(
                        idItems, LocalDateTime.now(), Status.APPROVED, Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .map(BookingMapper::toDtoShortResponse)
                .collect(Collectors.toMap(BookingDtoShortResponse::getItemId, Function.identity()));
        itemsDtoResponse.forEach(i -> i.setLastBooking(lastBookings.get(i.getId())));

        Map<Long, BookingDtoShortResponse> nextBookings = bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(
                        idItems, LocalDateTime.now(), Status.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .map(BookingMapper::toDtoShortResponse)
                .collect(Collectors.toMap(BookingDtoShortResponse::getItemId, Function.identity()));
        itemsDtoResponse.forEach(i -> i.setNextBooking(nextBookings.get(i.getId())));

        Map<Long, List<CommentDtoResponse>> comments = commentRepository.findByItemIdIn(
                        idItems, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(CommentMapper::toDtoResponse)
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
        itemsDtoResponse.forEach(i -> i.setComments(comments.get(i.getId())));

        return itemsDtoResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text.toLowerCase(), pageable).stream()
                .map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Такой вещи нет!"));

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(itemId, Status.APPROVED,
                    Sort.by(Sort.Direction.ASC, "start"));
            setNextAndLastBooking(bookings, item);
        }

        addComments(item);

        return ItemMapper.toDto(item);
    }

    @Transactional
    @Override
    public ItemDto create(Long userId, CreateItemDto itemDto) {
        Item item = ItemMapper.createItemDtoToItem(itemDto);

        item.setOwner(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Такого пользователя нет!")));

        Long requestId = itemDto.getRequestId();

        if (requestId != null) {
            item.setItemRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ObjectNotFoundException("Такого запроса нет")));
        }

        itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(Long itemId, Long userId, UpdateItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Такого пользователя нет!"));

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Такой вещи нет!"));

        if (!user.equals(item.getOwner())) {
            throw new ObjectNotFoundException("Пользователь не является владельцем вещи!");
        }

        if ((itemDto.getName() != null) && (!itemDto.getName().isBlank())) {
            item.setName(itemDto.getName());
        }

        if ((itemDto.getDescription() != null) && (!itemDto.getDescription().isBlank())) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toDto(item);
    }

    @Transactional
    @Override
    public ItemDto delete(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Такой вещи нет!"));
        itemRepository.deleteById(itemId);
        return ItemMapper.toDto(item);
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CreateCommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Такого пользователя нет"));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Такой вещи нет"));

        if (!bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .isEmpty()) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());

            return CommentMapper.toDto(commentRepository.save(comment));
        } else {
            throw new UnavailableException("Невозможно оставить комментарий");
        }
    }

    private Booking getNextBooking(List<Booking> bookings) {
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        return filteredBookings.isEmpty() ? null : filteredBookings.get(0);
    }

    private Booking getLastBooking(List<Booking> bookings) {
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking -> !booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        return filteredBookings.isEmpty() ? null : filteredBookings.get(filteredBookings.size() - 1);
    }

    private void setNextAndLastBooking(List<Booking> bookings, Item item) {
        Booking nextBooking = getNextBooking(bookings);
        Booking lastBooking = getLastBooking(bookings);
        item.setNextBooking(nextBooking != null ? BookingMapper.toDtoShort(nextBooking) : null);
        item.setLastBooking(lastBooking != null ? BookingMapper.toDtoShort(lastBooking) : null);
    }

    private void addComments(Item item) {
        item.setComments(
                commentRepository.findByItemId(item.getId())
                        .stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList())
        );
    }
}
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentService commentService;

    static final Sort SORT_ASC = Sort.by(Sort.Direction.ASC, "end");
    static final Sort SORT_DESC = Sort.by(Sort.Direction.DESC, "end");


    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {

        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        Item repoItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("item not found"));
        if (!repoItem.getOwner().getId().equals(owner.getId()))  {
            throw new ItemNotFoundException("item not found");
        }

        itemDto.setId(itemId);
        Item item = ItemMapper.matchItem(itemDto, repoItem);
        item.setOwner(owner);

        itemRepository.save(item);
        item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("item not found"));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto get(Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        Item repoItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("item not found"));
        User owner = repoItem.getOwner();

        ItemDto itemDto = ItemMapper.toItemDto(repoItem);
        itemDto.setOwner(owner.getId());

        List<CommentDto> commentDtos = commentService.commentDtos(itemId);
        itemDto.setComments(commentDtos);

        if (!user.getId().equals(owner.getId())) {return itemDto;}

        Optional<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                itemId, LocalDateTime.now(), Status.APPROVED, SORT_DESC);

        itemDto.setLastBooking(lastBooking.isEmpty() ? null : LastBookingDto.builder()
                .id(lastBooking.get().getId())
                .bookerId(lastBooking.get().getBooker().getId())
                .start(lastBooking.get().getStart())
                .end(lastBooking.get().getEnd())
                .build());


        Optional<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                itemId, LocalDateTime.now(), Status.APPROVED, SORT_ASC);

        itemDto.setNextBooking(nextBooking.isEmpty() ? null : NextBookingDto.builder()
                .id(nextBooking.get().getId())
                .bookerId(nextBooking.get().getBooker().getId())
                .start(nextBooking.get().getStart())
                .end(nextBooking.get().getEnd())
                .build());
        return itemDto;
    }

    @Override
    public List<ItemDto> get(Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        List<Item> repoItems = itemRepository.findAllByOwnerId(userId);
        if (repoItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemDto> itemDtoList = repoItems.stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> itemDto.setOwner(owner.getId()))
                .collect(Collectors.toList());

        for (ItemDto itemDto : itemDtoList) {

            List<CommentDto> commentDtos = commentService.commentDtos(itemDto.getId());
            itemDto.setComments(commentDtos);

            Optional<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), Status.APPROVED, SORT_DESC);

            itemDto.setLastBooking(lastBooking.map(booking -> LastBookingDto.builder()
                    .id(booking.getId())
                    .bookerId(booking.getBooker().getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .build()).orElse(null));

            Optional<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), Status.APPROVED, SORT_ASC);

            itemDto.setNextBooking(nextBooking.map(booking -> NextBookingDto.builder()
                    .id(booking.getId())
                    .bookerId(booking.getBooker().getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .build()).orElse(null));
        }

        itemDtoList.sort(Comparator.comparing(o -> o.getLastBooking().getStart(),
                Comparator.nullsLast(Comparator.reverseOrder())));

        for (ItemDto itemDto : itemDtoList) {
            if (itemDto.getLastBooking() != null && itemDto.getLastBooking().getBookerId() == null) {
                itemDto.setLastBooking(null);
            }
            if (itemDto.getNextBooking() != null && itemDto.getNextBooking().getBookerId() == null) {
                itemDto.setNextBooking(null);
            }
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        User repoUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        if (text.isEmpty()) {return Collections.emptyList();}
        List<Item> searchItems = itemRepository.searchAvailableByText(text);
        List<ItemDto> searchItemDto = new ArrayList<>();
        for (Item item : searchItems) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setOwner(item.getOwner().getId());
            searchItemDto.add(itemDto);
        }
        return searchItemDto.isEmpty() ? Collections.emptyList() : searchItemDto;
    }
}
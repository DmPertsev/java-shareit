package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Такого пользователя нет!");
        }
        return ItemRequestMapper.toDto(itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException("Такого запроса нет")));
    }

    @Transactional
    @Override
    public ItemRequestDtoShort create(Long userId, ItemRequestDtoShort itemRequestDtoShort) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoShort);

        itemRequest.setRequester(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Такого пользователя нет!")));

        itemRequest.setCreated(LocalDateTime.now());

        itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toShortDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Такого пользователя нет!");
        }
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));
        return ItemRequestMapper.toDtoList(itemRequestRepository.findAllByUserId(userId, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllByRequester(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Такого пользователя нет!");
        }
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));
        return ItemRequestMapper.toDtoList(itemRequestRepository.findAllByRequesterId(userId, pageable));
    }
}
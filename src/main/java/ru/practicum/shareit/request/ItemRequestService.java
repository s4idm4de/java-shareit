package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemRequestService {
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    public ItemRequestDto putRequest(ItemRequestDto requestDto,
                                     Long userId, LocalDateTime current) {
        try {
            User requestor = userRepository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого пользователя"));
            ItemRequest request = ItemRequestMapper.toRequest(requestDto);
            request.setRequestor(requestor);
            request.setCreated(current);
            log.info("RequestService putRequest {}", request);
            return ItemRequestMapper.toRequestDto(requestRepository.save(request));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    //список своих запросов с ответами на них
    public List<ItemRequestDto> getRequests(Long userId) {
        try {
            userRepository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого пользователя"));
            List<ItemRequestDto> requestsDto = ItemRequestMapper
                    .toRequestDto(requestRepository.findAllByRequestor_Id(userId));
            requestsDto.forEach(requestDto -> {
                List<ItemDto> itemsDto = ItemMapper.toItemDto(
                        itemRepository.findAllByRequest_Id(requestDto.getId()));
                requestDto.setItems(itemsDto);
                log.info("RequestService getRequests {} {}", itemsDto, requestDto.getItems());
            });
            return requestsDto;
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    public List<ItemRequestDto> getRequestsAll(Long userId, Integer from,
                                               Integer size) {
        try {
            userRepository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого репозитория"));
            List<ItemRequestDto> requestsDto;
            if (from != null && size != null) {
                requestsDto = ItemRequestMapper.toRequestDto(requestRepository.findAll(
                                PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created")))).stream()
                        .filter(requestDto -> requestDto.getRequestorId() != userId).collect(Collectors.toList());
            } else {
                requestsDto = ItemRequestMapper.toRequestDto(requestRepository.findAll(
                                Sort.by(Sort.Direction.ASC, "created"))).stream()
                        .filter(requestDto -> requestDto.getRequestorId() != userId).collect(Collectors.toList());
            }
            requestsDto.forEach(requestDto -> setItems(requestDto, userId));
            return requestsDto;
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    public ItemRequestDto getRequest(Long requestId, Long userId) {
        try {
            userRepository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого пользователя"));
            List<Item> items = itemRepository.findAllByRequest_Id(requestId);
            log.info("RequestService getRequest {}", items);
            ItemRequestDto requestDto = ItemRequestMapper.toRequestDto(requestRepository.findById(requestId).orElseThrow(()
                    -> new NotFoundException("нет такого запроса")));
            if (items != null) requestDto.setItems(ItemMapper.toItemDto(items));
            return requestDto;
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    private void setItems(ItemRequestDto requestDto, Long userId) {
        if (requestDto.getRequestorId() != userId) {
            List<ItemDto> itemsDto = ItemMapper.toItemDto(
                    itemRepository.findAllByRequest_Id(requestDto.getId()));
            requestDto.setItems(itemsDto);
            log.info("RequestService getRequests {} {}", itemsDto, requestDto.getItems());
        }

    }
}

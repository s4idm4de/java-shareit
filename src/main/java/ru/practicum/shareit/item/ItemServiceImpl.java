package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final ItemRepository repository;
    @Autowired
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto putItem(ItemDto item, Long userId) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("нет такого пользователя"));
        item.setOwner(UserMapper.toUserDto(user));
        Item itemForSave = ItemMapper.toItem(item);
        if (item.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(item.getRequestId()).orElseThrow(()
                    -> new NotFoundException("нет такого реквеста"));
            itemForSave.setRequest(request);
        }
        repository.save(itemForSave);
        log.info("ItemService putItem {}", itemForSave);
        return ItemMapper.toItemDto(itemForSave);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto item) throws NotFoundException {
        userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("нет такого пользователя"));
        Item oldItem = repository.findById(itemId).orElseThrow(()
                -> new NotFoundException("нет такой вещи"));
        @Valid Item itemForAdd = Item.builder()
                .id(itemId)
                .owner(item.getOwner() == null ? oldItem.getOwner() : UserMapper.toUser(item.getOwner()))
                .name(item.getName() == null ? oldItem.getName() : item.getName())
                .available(item.getAvailable() == null ? oldItem.isAvailable() : item.isAvailable())
                .request(item.getRequest() == null ? oldItem.getRequest() : ItemRequestMapper.toRequest(item.getRequest()))
                .description(item.getDescription() == null ? oldItem.getDescription() : item.getDescription())
                .build();
        Item itemForDate = repository.save(itemForAdd);
        log.info("ItemService updateItem {}", itemForDate);
        return ItemMapper.toItemDto(itemForDate);
    }


    @Override
    public ItemDto getItemById(Long itemId, Long userId) throws NotFoundException {
        Item item = repository.findById(itemId).orElseThrow(()
                -> new NotFoundException("нет такого item"));
        List<Comment> comments = commentRepository.findAllByItem(item, Sort.by(Sort.Direction.ASC, "created"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookingsLast = bookingRepository.findLastBooking(item, LocalDateTime.now());
            if (!bookingsLast.isEmpty()) itemDto.setLastBooking(BookingMapper.toBookingDto(bookingsLast.get(0)));
            if (itemDto.getLastBooking() != null) {
                List<Booking> bookingsNext = bookingRepository.findFirstBooking(item, LocalDateTime.now())
                        .stream()
                        .filter(booking -> booking.getStart().isAfter(itemDto.getLastBooking()
                                .getEnd())).collect(Collectors.toList());
                if (!bookingsNext.isEmpty()) itemDto.setNextBooking(BookingMapper.toBookingDto(bookingsNext.get(0)));
            }
        }
        if (comments == null) {
            itemDto.setComments(new ArrayList<>());
        } else {
            itemDto.setComments(CommentMapper.toCommentDto(comments));
        }

        return itemDto;

    }


    @Override
    public List<ItemDto> getItemOfUser(Long userId) throws NotFoundException {
        userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("нет такого пользователя"));
        List<Item> items = repository.findByOwner_Id(userId);
        return items.stream().map(item -> {
            List<Booking> bookingsLast = bookingRepository.findLastBooking(item, LocalDateTime.now());
            List<Booking> bookingsNext = bookingRepository.findFirstBooking(item, LocalDateTime.now());
            List<Comment> comments = commentRepository.findAllByItem(item, Sort.by(Sort.Direction.ASC, "created"));
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (comments == null) {
                itemDto.setComments(new ArrayList<>());
            } else {
                itemDto.setComments(CommentMapper.toCommentDto(comments));
            }
            if (!bookingsLast.isEmpty()) itemDto.setLastBooking(BookingMapper.toBookingDto(bookingsLast.get(0)));
            if (!bookingsNext.isEmpty()) itemDto.setNextBooking(BookingMapper.toBookingDto(bookingsNext.get(0)));
            return itemDto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<ItemDto> getSearch(String text) {
        if (!(text == null || text.isBlank())) {
            return ItemMapper.toItemDto(repository.search(text));
        } else {
            log.info("Storage getSearch text is null");
            List<ItemDto> empty = new ArrayList<>();
            return empty;
        }
    }

    @Override
    public CommentDto putComment(Long itemId, Long userId, CommentDto comment, LocalDateTime created) {
        try {
            List<Booking> bookings = bookingRepository
                    .findAllByBooker_IdAndEndIsBefore(userId, created,
                            Sort.by(Sort.Direction.DESC, "end"));
            Item item = repository.findById(itemId).orElseThrow(()
                    -> new NotFoundException("нет такой вещи"));
            User user = userRepository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого пользователя"));
            if (bookings != null && bookings.size() > 0) {
                return CommentMapper.toCommentDto(commentRepository.save(Comment.builder()
                        .author(user)
                        .item(item)
                        .text(comment.getText())
                        .created(created)
                        .build()));
            } else {
                throw new ValidationException("нельзя оставить отзыв на неиспользованную вещь");
            }
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}

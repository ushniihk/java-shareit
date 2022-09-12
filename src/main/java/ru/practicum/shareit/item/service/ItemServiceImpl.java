package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.exceptions.UpdateException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoWithAuthorAndItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public List<ItemDtoWithBooking> getAllByUser(long userId, int from, int size) {
        if (from < 0 || size <= 0)
            throw new IncorrectParameterException("bad size or index");
        PageRequest pageRequest = PageRequest.of(0, size);
        return itemRepository.findAllByOwnerOrderById(userId, from, pageRequest).stream()
                .map(item -> setBookingsForItem(item, userId))
                .map(this::addCommentsForItem).collect(Collectors.toList());
    }

    @Override
    public ItemDtoWithBooking get(long userId, long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundParameterException("bad Id");
        }
        ItemDtoWithBooking item = setBookingsForItem(itemRepository.getReferenceById(itemId), userId);
        return addCommentsForItem(item);
    }

    @Override
    public List<ItemDto> search(long userId, String text, int from, int size) {
        if (text.isEmpty())
            return new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, size);
        return itemRepository.findAll(from, pageRequest).stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().equals(true))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoWithBooking addNew(long userId, ItemDto itemDto) {
        checkItem(itemDto);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundParameterException("bad user id");
        }
        itemDto.setOwner(userId);
        return itemMapper.toItemDtoWithBooking(itemRepository.save(itemMapper.toItem(itemDto)));

    }

    @Override
    public void delete(long userId, long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (get(userId, itemId).getOwner() != userId) {
            throw new UpdateException("bad user id");
        }
        ItemDto oldItemDto = itemMapper.toItemDto(itemRepository.getReferenceById(itemId));
        if (itemDto.getName() != null) {
            oldItemDto.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItemDto.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(itemMapper.toItem(oldItemDto));
        return oldItemDto;
    }

    @Override
    public CommentDtoWithAuthorAndItem addComment(long userId, long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new CreatingException("comment is empty");
        }

        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndBookerIdAndStartBefore(itemId, userId, LocalDateTime.now());
        if (bookings.size() < 1) {
            throw new CreatingException("user didn't order this item");
        }
        commentDto.setAuthorId(userId);
        commentDto.setItemId(itemId);
        commentDto.setCreated(LocalDate.now());
        CommentDtoWithAuthorAndItem comment = commentMapper
                .toCommentDtoWithAuthorAndItem(commentRepository.save(commentMapper.toComment(commentDto)));
        comment.setAuthorName(userRepository.getReferenceById(commentDto.getAuthorId()).getName());
        return comment;
    }

    private void checkItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null)
            throw new CreatingException("ha-ha, try better, bad item");
    }

    private ItemDtoWithBooking setBookingsForItem(Item item, long userId) {
        ItemDtoWithBooking itemDtoWithBooking = itemMapper.toItemDtoWithBooking(item);
        List<Booking> lastBookings = bookingRepository
                .findAllByItemIdAndStartIsBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())
                .stream().filter(booking -> booking.getBookerId() != userId).collect(Collectors.toList());
        List<Booking> nextBookings = bookingRepository
                .findAllByItemIdAndStartIsAfterOrderByStartAsc(item.getId(), LocalDateTime.now())
                .stream().filter(booking -> booking.getBookerId() != userId).collect(Collectors.toList());
        if (lastBookings.size() > 0)
            itemDtoWithBooking.setLastBooking(bookingMapper.toBookingDto(lastBookings.get(0)));
        if (nextBookings.size() > 0)
            itemDtoWithBooking.setNextBooking(bookingMapper.toBookingDto(nextBookings.get(0)));
        return itemDtoWithBooking;
    }

    private ItemDtoWithBooking addCommentsForItem(ItemDtoWithBooking item) {
        List<Comment> comments = commentRepository.findAllByItem(item.getId());
        List<CommentDtoWithAuthorAndItem> commentDtoWithAuthorAndItemList = new ArrayList<>();
        for (Comment c : comments) {
            CommentDtoWithAuthorAndItem commentDtoWithAuthorAndItem = commentMapper.toCommentDtoWithAuthorAndItem(c);
            commentDtoWithAuthorAndItem.setAuthorName(userRepository.getReferenceById(c.getAuthor()).getName());
            commentDtoWithAuthorAndItemList.add(commentDtoWithAuthorAndItem);
        }
        item.setComments(commentDtoWithAuthorAndItemList);
        return item;
    }


}

package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.exceptions.UpdateException;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoWithAuthorAndItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    public List<ItemDtoWithBooking> getAllByUser(long userId) {
        return itemStorage.findAllByOwnerOrderById(userId).stream()
                .map(item -> setBookingsForItem(item, userId))
                .map(this::addCommentsForItem).collect(Collectors.toList());
    }

    @Override
    public ItemDtoWithBooking get(long userId, long itemId) {
        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundParameterException("bad Id");
        }
        ItemDtoWithBooking item = setBookingsForItem(itemStorage.getReferenceById(itemId), userId);
        return addCommentsForItem(item);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        if (text.isEmpty())
            return new ArrayList<>();
        return itemStorage.findAll().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addNew(long userId, ItemDto itemDto) {
        checkItem(itemDto);
        if (!userStorage.existsById(userId)) {
            throw new NotFoundParameterException("bad user id");
        }
        itemDto.setOwner(userId);
        return ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(itemDto)));

    }

    @Override
    public void delete(long userId, long itemId) {
        itemStorage.deleteById(itemId);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (get(userId, itemId).getOwner() != userId) {
            throw new UpdateException("bad user id");
        }
        ItemDto oldItemDto = ItemMapper.toItemDto(itemStorage.getReferenceById(itemId));
        if (itemDto.getName() != null) {
            oldItemDto.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItemDto.setAvailable(itemDto.getAvailable());
        }
        itemStorage.save(ItemMapper.toItem(oldItemDto));
        return oldItemDto;
    }

    @Override
    public CommentDtoWithAuthorAndItem addComment(long userId, long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new CreatingException("comment is empty");
        }

        List<Booking> bookings = bookingStorage
                .findAllByItemIdAndBookerIdAndStartBefore(itemId, userId, LocalDateTime.now());
        if (bookings.size() < 1) {
            throw new CreatingException("user didn't order this item");
        }
        commentDto.setAuthorId(userId);
        commentDto.setItemId(itemId);
        commentDto.setCreated(LocalDate.now());
        CommentDtoWithAuthorAndItem comment = CommentMapper
                .toCommentDtoWithAuthorAndItem(commentStorage.save(CommentMapper.toComment(commentDto)));
        comment.setAuthorName(userStorage.getReferenceById(commentDto.getAuthorId()).getName());
        return comment;
    }

    private void checkItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null)
            throw new CreatingException("ha-ha, try better, bad item");
    }

    private ItemDtoWithBooking setBookingsForItem(Item item, long userId) {
        ItemDtoWithBooking itemDtoWithBooking = ItemMapper.toItemDtoWithBooking(item);
        List<Booking> lastBookings = bookingStorage
                .findAllByItemIdAndStartIsBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())
                .stream().filter(booking -> booking.getBookerId() != userId).collect(Collectors.toList());
        List<Booking> nextBookings = bookingStorage
                .findAllByItemIdAndStartIsAfterOrderByStartAsc(item.getId(), LocalDateTime.now())
                .stream().filter(booking -> booking.getBookerId() != userId).collect(Collectors.toList());
        if (lastBookings.size() > 0)
            itemDtoWithBooking.setLastBooking(BookingMapper.toBookingDto(lastBookings.get(0)));
        if (nextBookings.size() > 0)
            itemDtoWithBooking.setNextBooking(BookingMapper.toBookingDto(nextBookings.get(0)));
        return itemDtoWithBooking;
    }

    private ItemDtoWithBooking addCommentsForItem(ItemDtoWithBooking item) {
        List<Comment> comments = commentStorage.findAllByItem(item.getId());
        List<CommentDtoWithAuthorAndItem> commentDtoWithAuthorAndItemList = new ArrayList<>();
        for (Comment c : comments) {
            CommentDtoWithAuthorAndItem commentDtoWithAuthorAndItem = CommentMapper.toCommentDtoWithAuthorAndItem(c);
            commentDtoWithAuthorAndItem.setAuthorName(userStorage.getReferenceById(c.getAuthor()).getName());
            commentDtoWithAuthorAndItemList.add(commentDtoWithAuthorAndItem);
        }
        item.setComments(commentDtoWithAuthorAndItemList);
        return item;
    }


}

package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoWithAuthorAndItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private Booking booking;
    private UserDto booker;
    private ItemDto item;
    private Item itemReal;
    private Comment comment;

    private CommentDto commentDto;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    private ItemService service;

    private MockitoSession mockitoSession;

    @BeforeEach
    void init() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        service = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);

        booking = new Booking(
                1,
                LocalDateTime.of(2023, 6, 1, 12, 0),
                LocalDateTime.of(2023, 6, 2, 12, 0),
                2,
                3,
                Booking.Status.WAITING
        );

        booker = new UserDto(3, "Bob", "bob@mail.ru");
        item = new ItemDto(2, "knife", "make to cut", true, 3, 1);
        itemReal = new Item(2, "knife", "make to cut", true, 3, 1);
        comment = new Comment(4, "comment text", 2, 3, LocalDate.of(2023, 6, 1));
        commentDto = new CommentDto(4, "comment text", 2, 3, LocalDate.of(2023, 6, 1));
    }

    @AfterEach
    void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    void shouldGetAllByUser() {
        List<Item> items = List.of(itemReal);
        Page<Item> pageItems = new PageImpl<>(items);
        List<Booking> bookings = List.of(booking);
        List<Comment> comments = List.of(comment);
        doReturn(pageItems).when(itemRepository).findAllByOwnerOrderById(any(Long.class), any(PageRequest.class));
        doReturn(bookings).when(bookingRepository).findAllByItemIdAndStartIsBeforeOrderByStartDesc(any(Long.class), any(LocalDateTime.class));
        doReturn(comments).when(commentRepository).findAllByItem(anyLong());
        doReturn(UserMapper.toUser(booker)).when(userRepository).getReferenceById(any());

        List<ItemDtoWithBooking> list = service.getAllByUser(booker.getId(), 0, 10);

        MatcherAssert.assertThat(list.get(0).getId(), equalTo(item.getId()));
        MatcherAssert.assertThat(list.get(0).getName(), equalTo(item.getName()));
        MatcherAssert.assertThat(list.get(0).getDescription(), equalTo(item.getDescription()));
        MatcherAssert.assertThat(list.get(0).getAvailable(), equalTo(item.getAvailable()));
        MatcherAssert.assertThat(list.get(0).getComments().size(), equalTo(1));

    }

    @Test
    void shouldGet() {
        List<Comment> comments = List.of(comment);
        doReturn(comments).when(commentRepository).findAllByItem(anyLong());
        doReturn(UserMapper.toUser(booker)).when(userRepository).getReferenceById(any());
        doReturn(true).when(itemRepository).existsById(any());
        doReturn(itemReal).when(itemRepository).getReferenceById(any());

        ItemDtoWithBooking itemDtoWithBooking = service.get(booker.getId(), item.getId());

        MatcherAssert.assertThat(itemDtoWithBooking.getId(), equalTo(item.getId()));
        MatcherAssert.assertThat(itemDtoWithBooking.getName(), equalTo(item.getName()));
        MatcherAssert.assertThat(itemDtoWithBooking.getDescription(), equalTo(item.getDescription()));
        MatcherAssert.assertThat(itemDtoWithBooking.getAvailable(), equalTo(item.getAvailable()));
        MatcherAssert.assertThat(itemDtoWithBooking.getComments().size(), equalTo(1));
    }

    @Test
    void shouldSearch() {
        List<Item> items = List.of(itemReal);
        Page<Item> pageItems = new PageImpl<>(items);

        doReturn(pageItems).when(itemRepository).findAll(any(PageRequest.class));

        List<ItemDto> list = service.search(booker.getId(), "cut", 0, 10);

        MatcherAssert.assertThat(list.get(0).getId(), equalTo(item.getId()));
        MatcherAssert.assertThat(list.get(0).getName(), equalTo(item.getName()));
        MatcherAssert.assertThat(list.get(0).getDescription(), equalTo(item.getDescription()));
        MatcherAssert.assertThat(list.get(0).getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void shouldDelete() {
        service.delete(booker.getId(), item.getId());

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(item.getId());
    }

    @Test
    void shouldAddNew() {
        doReturn(true).when(userRepository).existsById(anyLong());
        doReturn(itemReal).when(itemRepository).save(any(Item.class));

        service.addNew(booker.getId(), item);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(itemReal);
    }

    @Test
    void shouldUpdate() {
        List<Comment> comments = List.of(comment);

        doReturn(comments).when(commentRepository).findAllByItem(anyLong());
        doReturn(UserMapper.toUser(booker)).when(userRepository).getReferenceById(any());
        doReturn(true).when(itemRepository).existsById(anyLong());
        doReturn(itemReal).when(itemRepository).getReferenceById(anyLong());
        doReturn(itemReal).when(itemRepository).save(any(Item.class));


        service.update(booker.getId(), item.getId(), item);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(itemReal);
    }

    @Test
    void shouldAddComment() {
        List<Booking> bookings = List.of(booking);

        doReturn(comment).when(commentRepository).save(any(Comment.class));
        doReturn(UserMapper.toUser(booker)).when(userRepository).getReferenceById(any());
        doReturn(bookings).when(bookingRepository)
                .findAllByItemIdAndBookerIdAndStartBefore(anyLong(), anyLong(), any(LocalDateTime.class));

        CommentDtoWithAuthorAndItem commentDtoWithAuthorAndItem =
                service.addComment(booker.getId(), item.getId(), commentDto);

        MatcherAssert.assertThat(commentDtoWithAuthorAndItem.getId(), equalTo(comment.getId()));
        MatcherAssert.assertThat(commentDtoWithAuthorAndItem.getAuthorName(), equalTo(booker.getName()));
        MatcherAssert.assertThat(commentDtoWithAuthorAndItem.getText(), equalTo(comment.getText()));
        MatcherAssert.assertThat(commentDtoWithAuthorAndItem.getCreated(), equalTo(comment.getCreated()));

    }

    @Test
    void shouldThrowUpdateException() {
        List<Comment> comments = List.of(comment);
        doReturn(comments).when(commentRepository).findAllByItem(anyLong());
        doReturn(UserMapper.toUser(booker)).when(userRepository).getReferenceById(any());
        doReturn(true).when(itemRepository).existsById(any());
        doReturn(itemReal).when(itemRepository).getReferenceById(any());

        final Exception exceptionNotFound = Assertions.assertThrows(
                Exception.class,
                () -> service.update(9, booking.getId(), item));

        MatcherAssert.assertThat("bad user id", equalTo(exceptionNotFound.getMessage()));
    }

}
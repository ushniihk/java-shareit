package ru.practicum.shareit.booking.service;

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
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemAndUser;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private BookingDto dto;
    private Booking booking;
    private UserDto booker;
    private Item itemReal;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private BookingService bookingService;

    private MockitoSession mockitoSession;

    @BeforeEach
    void init() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository,
                userRepository);

        booking = new Booking(
                1,
                LocalDateTime.of(2023, 6, 1, 12, 0),
                LocalDateTime.of(2023, 6, 2, 12, 0),
                2,
                3,
                Booking.Status.WAITING
        );

        dto = new BookingDto(
                1,
                LocalDateTime.of(2023, 6, 1, 12, 0),
                LocalDateTime.of(2023, 6, 2, 12, 0),
                2,
                3,
                Booking.Status.WAITING
        );

        booker = new UserDto(3, "Bob", "bob@mail.ru");
        itemReal = new Item(2, "knife", "make to cut", true, 3, 1);

    }

    @AfterEach
    void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    void shouldAdd() {
        when(itemRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(any()))
                .thenReturn(itemReal);
        when(userRepository.existsById(any()))
                .thenReturn(true);
        when(userRepository.getReferenceById(any()))
                .thenReturn(UserMapper.toUser(booker));
        when(bookingRepository.findBookingByStartAndEndAndBookerIdAndItemId(any(), any(), any(), any()))
                .thenReturn(BookingMapper.toBooking(dto));

        BookingDtoWithItemAndUser bookingTest = bookingService.add(2, dto);

        MatcherAssert.assertThat(bookingTest.getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(bookingTest.getStart(), equalTo(dto.getStart()));
        MatcherAssert.assertThat(bookingTest.getEnd(), equalTo(dto.getEnd()));
        MatcherAssert.assertThat(bookingTest.getStatus(), equalTo(Booking.Status.WAITING));

    }

    @Test
    void shouldApprove() {
        when(itemRepository.getReferenceById(any()))
                .thenReturn(itemReal);
        when(userRepository.getReferenceById(any()))
                .thenReturn(UserMapper.toUser(booker));
        when(bookingRepository.getReferenceById(any()))
                .thenReturn(BookingMapper.toBooking(dto));
        when(bookingRepository.findBookingByStartAndEndAndBookerIdAndItemId(any(), any(), any(), any()))
                .thenReturn(BookingMapper.toBooking(dto));

        BookingDtoWithItemAndUser bookingTest = bookingService.approve(3, 1, true);

        MatcherAssert.assertThat(bookingTest.getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(bookingTest.getStart(), equalTo(dto.getStart()));
        MatcherAssert.assertThat(bookingTest.getEnd(), equalTo(dto.getEnd()));
    }

    @Test
    void shouldGet() {
        when(itemRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(any()))
                .thenReturn(itemReal);
        when(userRepository.existsById(any()))
                .thenReturn(true);
        when(userRepository.getReferenceById(any()))
                .thenReturn(UserMapper.toUser(booker));
        when(bookingRepository.existsById(dto.getId()))
                .thenReturn(true);
        when(bookingRepository.getReferenceById(any()))
                .thenReturn(BookingMapper.toBooking(dto));
        when(bookingRepository.findBookingByStartAndEndAndBookerIdAndItemId(any(), any(), any(), any()))
                .thenReturn(BookingMapper.toBooking(dto));

        BookingDtoWithItemAndUser bookingTest = bookingService.get(booker.getId(), dto.getId());

        MatcherAssert.assertThat(bookingTest.getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(bookingTest.getStart(), equalTo(dto.getStart()));
        MatcherAssert.assertThat(bookingTest.getEnd(), equalTo(dto.getEnd()));
        MatcherAssert.assertThat(bookingTest.getStatus(), equalTo(Booking.Status.WAITING));
    }

    @Test
    void shouldFindAll() {
        List<Booking> bookings = List.of(booking);
        Page<Booking> page = new PageImpl<>(bookings);

        when(itemRepository.getReferenceById(any()))
                .thenReturn(itemReal);
        when(userRepository.existsById(any()))
                .thenReturn(true);
        when(userRepository.getReferenceById(any()))
                .thenReturn(UserMapper.toUser(booker));
        when(bookingRepository.findBookingByStartAndEndAndBookerIdAndItemId(any(), any(), any(), any()))
                .thenReturn(BookingMapper.toBooking(dto));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(any(long.class), any(int.class), any(PageRequest.class)))
                .thenReturn(page);

        List<BookingDtoWithItemAndUser> list = bookingService.findAll(booker.getId(), "ALL", 0, 2);

        MatcherAssert.assertThat(list.get(0).getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(list.get(0).getStart(), equalTo(dto.getStart()));
        MatcherAssert.assertThat(list.get(0).getEnd(), equalTo(dto.getEnd()));
        MatcherAssert.assertThat(list.get(0).getStatus(), equalTo(Booking.Status.WAITING));

    }

    @Test
    void shouldFindAllByItemOwner() {
        List<Booking> bookings = List.of(booking);
        Page<Booking> page = new PageImpl<>(bookings);

        when(itemRepository.getReferenceById(any()))
                .thenReturn(itemReal);
        when(itemRepository.findAllByOwnerOrderById(booker.getId()))
                .thenReturn(List.of(itemReal));
        when(userRepository.existsById(any()))
                .thenReturn(true);
        when(userRepository.getReferenceById(any()))
                .thenReturn(UserMapper.toUser(booker));
        when(bookingRepository.findBookingByStartAndEndAndBookerIdAndItemId(any(), any(), any(), any()))
                .thenReturn(BookingMapper.toBooking(dto));
        when(bookingRepository.findAllByItemIdOrderByStartDesc(any(long.class), any(int.class), any(PageRequest.class)))
                .thenReturn(page);

        List<BookingDtoWithItemAndUser> list = bookingService.findAllByItemOwner(booker.getId(), "ALL", 0, 2);

        MatcherAssert.assertThat(list.get(0).getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(list.get(0).getStart(), equalTo(dto.getStart()));
        MatcherAssert.assertThat(list.get(0).getEnd(), equalTo(dto.getEnd()));
        MatcherAssert.assertThat(list.get(0).getStatus(), equalTo(Booking.Status.WAITING));
    }

    @Test
    void shouldThrowIncorrectParameterException() {
        booking.setStatus(Booking.Status.APPROVED);
        when(bookingRepository.getReferenceById(any()))
                .thenReturn(booking);

        final Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> bookingService.approve(booker.getId(), booking.getId(), true));

        MatcherAssert.assertThat("status has already been approved", equalTo(exception.getMessage()));
    }

    @Test
    void shouldSetStatus() {
        List<Booking> bookings = List.of(booking);
        List<Booking> listFUTURE = ReflectionTestUtils.invokeMethod(bookingService, "checkStatus", bookings, "FUTURE");
        List<Booking> listWAITING = ReflectionTestUtils.invokeMethod(bookingService, "checkStatus", bookings, "WAITING");
        booking.setStart(LocalDateTime.now().minusYears(10));
        booking.setEnd(LocalDateTime.now().minusYears(9));
        List<Booking> listPAST = ReflectionTestUtils.invokeMethod(bookingService, "checkStatus", bookings, "PAST");
        booking.setStatus(Booking.Status.REJECTED);
        List<Booking> listREJECTED = ReflectionTestUtils.invokeMethod(bookingService, "checkStatus", bookings, "REJECTED");
        Booking booking1 = booking;
        booking1.setEnd(LocalDateTime.now().plusYears(9));
        List<Booking> bookings1 = List.of(booking1);
        List<Booking> listCURRENT = ReflectionTestUtils.invokeMethod(bookingService, "checkStatus", bookings1, "CURRENT");

        MatcherAssert.assertThat(listFUTURE.get(0).getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(listREJECTED.get(0).getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(listWAITING.get(0).getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(listPAST.get(0).getId(), equalTo(dto.getId()));
        MatcherAssert.assertThat(listCURRENT.get(0).getId(), equalTo(dto.getId()));

        final Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> ReflectionTestUtils.invokeMethod(bookingService, "checkStatus", bookings, "NOT SUPPORT"));

        MatcherAssert.assertThat("Unknown state: UNSUPPORTED_STATUS", equalTo(exception.getMessage()));
    }
}
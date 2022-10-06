package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemAndUser;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDtoWithItemAndUser add(long userId, BookingDto bookingDto) {
        checkUser(userId);
        checkItem(bookingDto.getItemId());
        Item item = itemRepository.getReferenceById(bookingDto.getItemId());
        if (userId == item.getOwner())
            throw new NotFoundParameterException("user can't make a request for his item");
        bookingDto.setStatus(Booking.Status.WAITING);
        bookingDto.setBookerId(userId);
        bookingRepository.save(BookingMapper.toBooking(bookingDto));
        return getBookingDtoWithItemAndUser(bookingDto);
    }

    @Override
    public BookingDtoWithItemAndUser approve(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (booking.getStatus().equals(Booking.Status.APPROVED))
            throw new IncorrectParameterException("status has already been approved");
        Item item = itemRepository.getReferenceById(booking.getItemId());
        if (item.getOwner() != userId)
            throw new NotFoundParameterException("user is not the owner");
        if (approved) {
            booking.setStatus(Booking.Status.APPROVED);
        } else booking.setStatus(Booking.Status.REJECTED);
        bookingRepository.save(booking);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        return getBookingDtoWithItemAndUser(bookingDto);
    }

    @Override
    public BookingDtoWithItemAndUser get(long userId, long bookingId) {
        checkUser(userId);
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundParameterException("bad booking id");
        }
        BookingDto bookingDto = BookingMapper.toBookingDto(bookingRepository.getReferenceById(bookingId));
        Item item = itemRepository.getReferenceById(bookingDto.getItemId());
        if (bookingDto.getBookerId() != userId && item.getOwner() != userId)
            throw new NotFoundParameterException("bad user id");
        checkItem(bookingDto.getItemId());
        return getBookingDtoWithItemAndUser(bookingDto);
    }

    @Override
    public List<BookingDtoWithItemAndUser> findAll(long userId, String state, int from, int size) {
        checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("start").descending());
        if (state.equals("ALL")) {
            return listOfBookingDtoWithItemAndUsers(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest).toList());
        }
        return listOfBookingDtoWithItemAndUsers(checkStatus(
                new ArrayList<>(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest).toList()), state));
    }

    @Override
    public List<BookingDtoWithItemAndUser> findAllByItemOwner(long userId, String state, int from, int size) {
        checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Item> items = itemRepository.findAllByOwnerOrderById(userId);
        List<Booking> bookings = new ArrayList<>();
        for (Item i : items) {
            bookings.addAll(bookingRepository.findAllByItemIdOrderByStartDesc(i.getId(), pageRequest).toList());
        }
        if (state.equals("ALL")) {
            return listOfBookingDtoWithItemAndUsers(bookings);
        }
        return listOfBookingDtoWithItemAndUsers(checkStatus(bookings, state));
    }

    private void checkItem(Long itemId) {
        if (!itemRepository.existsById(itemId))
            throw new NotFoundParameterException("bad itemId");
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getAvailable().equals(false)) {
            throw new CreatingException("Item is unavailable");
        }
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundParameterException("bad user id");
    }

    private BookingDtoWithItemAndUser getBookingDtoWithItemAndUser(BookingDto bookingDto) {
        BookingDtoWithItemAndUser bookingDtoWithItemAndUser = BookingMapper.toBookingDtoWithItemAndUser(
                BookingMapper.toBookingDto(bookingRepository.findBookingByStartAndEndAndBookerIdAndItemId(
                        bookingDto.getStart(), bookingDto.getEnd(), bookingDto.getBookerId(), bookingDto.getItemId())));
        UserDto bookerDto = UserMapper.toUserDto(userRepository.getReferenceById(bookingDto.getBookerId()));
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.getReferenceById(bookingDto.getItemId()));
        bookingDtoWithItemAndUser.setBooker(bookerDto);
        bookingDtoWithItemAndUser.setItem(itemDto);
        return bookingDtoWithItemAndUser;
    }

    private List<Booking> checkStatus(List<Booking> bookings, String state) {
        List<Booking> bs = new ArrayList<>();
        for (Booking b : bookings) {
            switch (state) {
                case "CURRENT":
                    if (b.getEnd().isAfter(LocalDateTime.now())
                            && b.getStart().isBefore(LocalDateTime.now()))
                        bs.add(b);
                    break;
                case "WAITING":
                    if (b.getStatus().equals(Booking.Status.WAITING))
                        bs.add(b);
                    break;
                case "REJECTED":
                    if (b.getStatus().equals(Booking.Status.REJECTED))
                        bs.add(b);
                    break;
                case "PAST":
                    if (b.getEnd().isBefore(LocalDateTime.now()))
                        bs.add(b);
                    break;
                case "FUTURE":
                    if (b.getStart().isAfter(LocalDateTime.now()))
                        bs.add(b);
                    break;
                default:
                    throw new IncorrectParameterException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        return bs;
    }

    private List<BookingDtoWithItemAndUser> listOfBookingDtoWithItemAndUsers(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto)
                .map(this::getBookingDtoWithItemAndUser).collect(Collectors.toList());
    }
}

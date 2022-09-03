package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findBookingByStartAndEndAndBookerIdAndItemId(LocalDateTime start, LocalDateTime end, Long userId, Long itemId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findAllByItemIdAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndStartIsBeforeOrderByStartDesc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndBookerIdAndStartBefore(long itemId, long bookerId, LocalDateTime start);

}

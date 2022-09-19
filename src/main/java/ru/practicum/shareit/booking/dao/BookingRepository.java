package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findBookingByStartAndEndAndBookerIdAndItemId(LocalDateTime start, LocalDateTime end, Long userId, Long itemId);

    @Query(value = "select b from Booking b where b.bookerId = ?1")
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    @Query(value = "select b from Booking b where b.itemId = ?1")
    Page<Booking> findAllByItemIdOrderByStartDesc(Long itemId, Pageable pageable);

    List<Booking> findAllByItemIdAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndStartIsBeforeOrderByStartDesc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndBookerIdAndStartBefore(long itemId, long bookerId, LocalDateTime start);

}

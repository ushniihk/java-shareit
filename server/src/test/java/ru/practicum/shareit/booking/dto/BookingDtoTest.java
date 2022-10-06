package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testUserDto() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.of(2022, 6, 1, 12, 0),
                LocalDateTime.of(2022, 6, 2, 12, 0),
                2,
                3,
                Booking.Status.WAITING
        );
        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-06-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-06-02T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
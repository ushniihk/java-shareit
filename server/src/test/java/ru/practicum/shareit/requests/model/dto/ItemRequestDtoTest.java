package ru.practicum.shareit.requests.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto dto = new ItemRequestDto("some description", 1);

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("some description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(1);
    }
}
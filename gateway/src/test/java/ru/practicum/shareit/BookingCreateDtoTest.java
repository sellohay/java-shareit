package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void testDeserializeBookingCreateDto() throws Exception {
        String jsonContent = "{\n" +
                "  \"itemId\": 1,\n" +
                "  \"start\": \"2026-06-27T10:00:00\",\n" +
                "  \"end\": \"2026-06-28T10:00:00\"\n" +
                "}";

        BookingCreateDto dto = json.parse(jsonContent).getObject();
        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStartDate()).isEqualTo(LocalDateTime.of(2026, 6, 27, 10, 0, 0));
        assertThat(dto.getEndDate()).isEqualTo(LocalDateTime.of(2026, 6, 28, 10, 0, 0));
    }
}
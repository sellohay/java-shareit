package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void createBooking_shouldReturnBookingDto() throws Exception {
        BookingCreateDto inputDto = new BookingCreateDto();
        inputDto.setItemId(1L);

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);

        when(bookingService.createBooking(eq(1L), any(BookingCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);

        when(bookingService.approveBooking(1L, 1L, true)).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getBooking_shouldReturnBookingDto() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        when(bookingService.getBooking(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getBookingsForUser_shouldReturnListOfBookings() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        when(bookingService.getBookingsForUser(1L, "ALL")).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getItemBookingsOfUser_shouldReturnListOfBookings() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        when(bookingService.getItemBookingsOfUser(1L, "PAST")).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L)
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
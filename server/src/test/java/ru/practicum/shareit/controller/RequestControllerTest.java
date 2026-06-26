package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void createRequest_shouldReturnItemRequestDto() throws Exception {
        NewItemRequest req = new NewItemRequest();
        req.setDescription("Nuzhen mel.");

        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Nuzhen mel.");

        when(itemRequestService.createRequest(any(NewItemRequest.class), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .content(mapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Nuzhen mel."));
    }

    @Test
    void getAllRequests_shouldReturnListOfRequestsWithResponses() throws Exception {
        ItemRequestDtoWithResponses dto = new ItemRequestDtoWithResponses();
        dto.setId(1L);

        when(itemRequestService.getAllRequests(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllRequestsByOthers_shouldReturnListOfRequests() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);

        when(itemRequestService.getAllRequestsByOthers(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getRequestById_shouldReturnRequestWithResponses() throws Exception {
        ItemRequestDtoWithResponses dto = new ItemRequestDtoWithResponses();
        dto.setId(2L);

        when(itemRequestService.getRequestById(1L, 2L)).thenReturn(dto);

        mockMvc.perform(get("/requests/2")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }
}
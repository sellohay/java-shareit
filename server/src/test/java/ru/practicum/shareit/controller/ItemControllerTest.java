package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void getItems_shouldReturnListOfItems() throws Exception {
        ItemDetailedDto dto = new ItemDetailedDto();
        dto.setId(1L);
        dto.setName("Mel");

        when(itemService.getItems(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Mel"));
    }

    @Test
    void createItem_shouldReturnItemDto() throws Exception {
        ItemDto inputDto = new ItemDto();
        inputDto.setName("Mel");

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Mel");

        when(itemService.createItem(eq(1L), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1L)
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mel"));
    }

    @Test
    void patchItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Novyy Mel");

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Novyy Mel");

        when(itemService.editItem(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header(HEADER, 1L)
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novyy Mel"));
    }

    @Test
    void getItem_shouldReturnItemDetailedDto() throws Exception {
        ItemDetailedDto dto = new ItemDetailedDto();
        dto.setId(1L);

        when(itemService.getItemById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void searchItem_shouldReturnListOfItems() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Mel");

        when(itemService.searchItems("Mel")).thenReturn(List.of(dto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Mel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mel"));
    }

    @Test
    void createComment_shouldReturnCommentDto() throws Exception {
        CommentDto inputDto = new CommentDto();
        inputDto.setText("COOL MEL");

        CommentDto responseDto = new CommentDto();
        responseDto.setId(1L);
        responseDto.setText("COOL MEL");

        when(itemService.createComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER, 1L)
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("COOL MEL"));
    }
}
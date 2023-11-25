package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void getById() {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestDto itemRequestDtoLong = new ItemRequestDto(1L, "description", userId,
                null, List.of());
        Mockito.when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemRequestDtoLong);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(Variables.HEADER, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoLong), result);
    }

    @SneakyThrows
    @Test
    void getAll() {
        Integer from = 0;
        Integer size = 20;
        Long userId = 1L;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", userId,
                null, List.of());
        Mockito.when(itemRequestService.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDto));

        String result = mockMvc.perform(get("/requests/all")
                        .header(Variables.HEADER, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDto)), result);
    }

    @SneakyThrows
    @Test
    void getAllByRequester() {
        Integer from = 0;
        Integer size = 20;
        Long userId = 1L;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", userId,
                null, List.of());
        Mockito.when(itemRequestService.getAllByRequester(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDto));

        String result = mockMvc.perform(get("/requests")
                        .header(Variables.HEADER, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDto)), result);
    }

    @SneakyThrows
    @Test
    void create() {
        Long userId = 1L;

        ItemRequestDtoShort dtoShortRequestShort = new ItemRequestDtoShort(1L, "description",
                null, null);
        ItemRequestDtoShort dtoShortResponse = new ItemRequestDtoShort(null, "description", userId,
                null);

        Mockito.when(itemRequestService.create(Mockito.any(), Mockito.any())).thenReturn(dtoShortResponse);

        String result = mockMvc.perform(post("/requests")
                        .header(Variables.HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dtoShortRequestShort)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoShortResponse), result);
    }
}
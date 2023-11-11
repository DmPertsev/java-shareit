package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingServiceImpl bookingService;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = start.plusDays(2);

    @SneakyThrows
    @Test
    void getById() {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(1L, start, end, null, null, Status.WAITING);

        Mockito.when(bookingService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Variables.HEADER, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getAllByOwner() {
        BookingDto bookingDto = new BookingDto(1L, start, end,  null, null, Status.WAITING);

        Mockito.when(bookingService.getAllByOwnerId(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header(Variables.HEADER, 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldReturnList() {
        BookingDto bookingDto = new BookingDto(1L, start, end, null, null, Status.WAITING);

        Mockito.when(bookingService.getAllByBookerId(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings")
                        .header(Variables.HEADER, 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @SneakyThrows
    @Test
    void create() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateBookingDto createBookingDto = new CreateBookingDto(start, end, itemId);
        BookingDto bookingDto = new BookingDto(1L, start, end, null, null, Status.WAITING);

        Mockito.when(bookingService.create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header(Variables.HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void create_whenStartIsNotFutureOrPresent_shouldReturnException() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateBookingDto createBookingDto = new CreateBookingDto(start.minusDays(3), end, itemId);
        BookingDto bookingDto = new BookingDto(1L, start, end,null, null, Status.WAITING);

        Mockito.when(bookingService.create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(Variables.HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void create_whenEndIsNotFuture_shouldReturnException() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateBookingDto createBookingDto = new CreateBookingDto(start.minusDays(3), end, itemId);
        BookingDto bookingDto = new BookingDto(1L, start, end,null, null, Status.WAITING);

        Mockito.when(bookingService.create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(Variables.HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void create_whenEndIsNull_shouldReturnException() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateBookingDto createBookingDto = new CreateBookingDto(start.minusDays(3), end, itemId);
        BookingDto bookingDto = new BookingDto(1L, start, end,null, null, Status.WAITING);

        Mockito.when(bookingService.create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(Variables.HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void create_whenEndIsNow_shouldReturnException() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateBookingDto createBookingDto = new CreateBookingDto(start.minusDays(3), end, itemId);
        BookingDto bookingDto = new BookingDto(1L, start, end,null, null, Status.WAITING);

        Mockito.when(bookingService.create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(Variables.HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void update() {
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto(1L, start, end,null, null, Status.WAITING);

        Mockito.when(bookingService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(Variables.HEADER, userId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @Test
    void create_whenServiceThrowsException_shouldReturnInternalServerError() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CreateBookingDto createBookingDto = new CreateBookingDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                itemId);

        Mockito.when(bookingService.create(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header(Variables.HEADER, userId)
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void getAll() {
        Integer from = 0;
        Integer size = 20;
        mockMvc.perform(get("/users")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userService).getAll(from, size);
    }

    @SneakyThrows
    @Test
    void getAll_whenRequestParamIsDefault() {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(userService).getAll(0, 10);
    }

    @SneakyThrows
    @Test
    void getById() {
        Long userId = 1L;
        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void create_whenUserIsValid_shouldReturnedOk() {
        CreateUserDto createUserDto = new CreateUserDto(null, "Alex", "alex.b@yandex.ru");
        Mockito.when(userService.create(createUserDto)).thenReturn(UserMapper.createUserDtoToUserDto(createUserDto));

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(createUserDto), result);
    }

    @SneakyThrows
    @Test
    void create_whenUserNameIsNotValid_shouldReturnedBadRequest() {
        Long userId = 1L;
        CreateUserDto createUserDto = new CreateUserDto(null, null, "alex.b@yandex.ru");

        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        createUserDto.setName(" ");
        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).create(createUserDto);
    }

    @SneakyThrows
    @Test
    void create_whenUserEmailIsNotValid_shouldReturnedBadRequest() {
        Long userId = 1L;
        CreateUserDto createUserDto = new CreateUserDto(null, "Alex", "hello");

        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        createUserDto.setEmail(" ");
        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        createUserDto.setEmail(null);
        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).create(createUserDto);
    }

    @SneakyThrows
    @Test
    void update_whenUserIsValid_shouldReturnedOk() {
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto(null, "Alex", "alex.b@yandex.ru");
        Mockito.when(userService.update(userId, updateUserDto))
                .thenReturn(UserMapper.updateUserDtoToUserDto(updateUserDto));

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(updateUserDto), result);
    }

    @SneakyThrows
    @Test
    void update_whenUserNameIsNotValid_shouldReturnedBadRequest() {
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto(null, null, "alex");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).update(userId, updateUserDto);
    }

    @SneakyThrows
    @Test
    void update_whenUserEmailIsNotValid_shouldReturnedBadRequest() {
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto(null, "Alex", "hello");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).update(userId, updateUserDto);
    }

    @SneakyThrows
    @Test
    void deleteById() {
        Long userId = 1L;
        mockMvc.perform(delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteById(userId);
    }
}
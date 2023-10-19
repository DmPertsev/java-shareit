package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class UserDto {

    private Long id;

    @Pattern(regexp = "\\S*$")
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}
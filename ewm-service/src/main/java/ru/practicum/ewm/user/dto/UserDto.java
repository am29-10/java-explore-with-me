package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @Positive(message = "id должен быть положительным")
    private Long id;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неккоректно введен email")
    private String email;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
}

package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserShortDto {
    @Positive(message = "id должен быть положительным")
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
}

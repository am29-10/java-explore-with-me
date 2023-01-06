package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.dto.EventShortDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationDto {
    @Positive(message = "id должен быть положительным")
    private Long id;
    private Boolean pinned;
    @NotEmpty(message = "Название не может быть пустым")
    private String title;
    private List<EventShortDto> events = new ArrayList<>();
}

package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.location.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @Positive(message = "id должен быть положительным")
    private Long eventId;
    @NotEmpty(message = "Краткое описание не может быть пустым")
    private String annotation;
    private Long category;
    @NotEmpty(message = "Полное описание не может быть пустым")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Событие должно состояться в будущем времени")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private String title;
}

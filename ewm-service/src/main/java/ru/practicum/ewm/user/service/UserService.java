package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(User user);

    UserDto update(Long userId, User user);

    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    UserDto get(Long userId);

    void delete(Long userId);
}

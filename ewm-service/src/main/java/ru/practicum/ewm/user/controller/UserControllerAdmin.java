package ru.practicum.ewm.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class UserControllerAdmin {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /admin/users");
        User user = UserMapper.toUser(userDto);
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /admin/users/{}", userId);
        User user = UserMapper.toUser(userDto);
        return userService.update(userId, user);
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam List<Long> ids,
                                @RequestParam(defaultValue = "0") Integer from,
                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /admin/users?from={}&size={}", from, size);
        return userService.getAll(ids, from, size);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        log.info("Получен запрос GET /admin/users/{}", userId);
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Получен запрос DELETE /admin/users/{}", userId);
        userService.delete(userId);
    }
}

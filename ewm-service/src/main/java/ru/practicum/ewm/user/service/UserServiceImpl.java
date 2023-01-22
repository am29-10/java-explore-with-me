package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new ConflictException(String.format("Пользователь с именем %s уже есть в базе", user.getName()));
        }
        User createUser = userRepository.save(user);
        log.info("Пользователь с id = {} создан", createUser.getId());
        return UserMapper.toUserDto(createUser);
    }

    @Override
    public UserDto update(Long userId, User user) {
        User updateUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Пользователь с id = %d не может быть обновлен, т.к. он отсутствует в базе", userId)));
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new ConflictException(String.format("Пользователь с именем %s уже есть в базе", user.getName()));
        }
        updateUser.setEmail(user.getEmail());
        updateUser.setName(user.getName());
        log.info("Пользователь с id = {} обновлен", updateUser.getId());
        return UserMapper.toUserDto(userRepository.save(updateUser));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users = userRepository.findAllByIdIn(ids, pageable).toList();
        log.info("Получен список всех пользователей");
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto get(Long userId) {
        User getUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %d не может быть получен, т.к. он отсутствует в базе", userId)));
        log.info("Получен пользователь с id = {}", userId);
        return UserMapper.toUserDto(getUser);

    }

    @Override
    public void delete(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %d не может быть удален, т.к. он отсутствует в базе", userId)));
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удален", userId);
    }
}

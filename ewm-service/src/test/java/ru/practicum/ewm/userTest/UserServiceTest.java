package ru.practicum.ewm.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserDto userDto;

    private User user;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Артур")
                .email("artur99@mail.ru")
                .build();

        user = User.builder()
                .id(1L)
                .name("Артур")
                .email("artur99@mail.ru")
                .build();
    }

    @Test
    public void create() {
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        UserDto newUser = userService.create(user);

        assertEquals(userDto, newUser);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void update() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        user.setName("Коля");
        UserDto updateUser = userService.update(1L, user);

        assertEquals("Коля", updateUser.getName());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void updateFail() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(1L, user));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getAll() {
        Mockito
                .when(userRepository.findAllByIdIn(anyList(), any()))
                .thenReturn(new PageImpl<>(List.of(user)));
        List<UserDto> users = userService.getAll(List.of(1L),0, 10);

        assertEquals(users.size(), 1);

        verify(userRepository, times(1)).findAllByIdIn(anyList(), any());
    }

    @Test
    public void get() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDto getUser = userService.get(user.getId());

        assertEquals(userDto, getUser);

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getFail() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.get(user.getId()));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void delete() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        userService.delete(user.getId());
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.delete(user.getId()));

        verify(userRepository, times(2)).findById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}

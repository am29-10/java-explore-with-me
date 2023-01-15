package ru.practicum.ewm.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    @Autowired
    private JacksonTester<User> jsonUser;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Артур")
                .email("artur289@mail.ru")
                .build();

        user = User.builder()
                .id(2L)
                .name("Петя")
                .email("prtr98@mail.ru")
                .build();
    }

    @Test
    void toUserDto() throws IOException {
        userDto = UserMapper.toUserDto(user);
        JsonContent<UserDto> result = jsonUserDto.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Петя");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("prtr98@mail.ru");
    }

    @Test
    void toUser() throws IOException {
        user = UserMapper.toUser(userDto);
        JsonContent<User> result = jsonUser.write(user);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Артур");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("artur289@mail.ru");
    }
}

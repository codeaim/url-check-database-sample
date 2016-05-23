package com.codeaim.urlcheck.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDtoTest
{
    @Test
    public void build() {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("name")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .modified(Instant.now())
                .version(1)
                .build();

        Assert.assertEquals(1, userDto.getId());
        Assert.assertEquals("name", userDto.getName());
        Assert.assertEquals("email@example.com", userDto.getEmail());
        Assert.assertEquals("resetToken", userDto.getResetToken());
        Assert.assertEquals("accessToken", userDto.getAccessToken());
        Assert.assertEquals("password", userDto.getPassword());
        Assert.assertEquals(true, userDto.isEmailVerified());
        Assert.assertNotNull(userDto.getCreated());
        Assert.assertNotNull(userDto.getModified());
        Assert.assertEquals(1, userDto.getVersion());
    }

    @Test
    public void buildFrom() {
        UserDto userDto = UserDto.buildFrom(UserDto.builder()
                .id(1)
                .name("name")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .created(Instant.now())
                .modified(Instant.now())
                .version(1)
                .build())
                .build();

        Assert.assertEquals(1, userDto.getId());
        Assert.assertEquals("name", userDto.getName());
        Assert.assertEquals("email@example.com", userDto.getEmail());
        Assert.assertEquals("resetToken", userDto.getResetToken());
        Assert.assertEquals("accessToken", userDto.getAccessToken());
        Assert.assertEquals("password", userDto.getPassword());
        Assert.assertEquals(true, userDto.isEmailVerified());
        Assert.assertNotNull(userDto.getCreated());
        Assert.assertNotNull( userDto.getModified());
        Assert.assertEquals(1, userDto.getVersion());
    }
}
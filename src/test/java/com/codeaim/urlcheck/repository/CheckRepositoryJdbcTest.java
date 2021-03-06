package com.codeaim.urlcheck.repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.codeaim.urlcheck.Application;
import com.codeaim.urlcheck.domain.CheckDto;
import com.codeaim.urlcheck.domain.State;
import com.codeaim.urlcheck.domain.Status;
import com.codeaim.urlcheck.domain.UserDto;

import okhttp3.HttpUrl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest
public class CheckRepositoryJdbcTest
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CheckRepository checkRepository;

    @Test
    public void save()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto firstCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url(HttpUrl.parse("http://www.example2.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        Collection<CheckDto> savedCheckDtos = checkRepository.save(
                Arrays.asList(
                        firstCheckDto,
                        secondCheckDto));

        userRepository.delete(savedUserDto);

        savedCheckDtos
                .stream()
                .forEach(savedCheckDto -> checkRepository.delete(savedCheckDto));

        Assert.assertEquals(2, savedCheckDtos.size());
    }

    @Test
    public void findOne()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto checkDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedCheckDto = checkRepository.save(checkDto);

        Optional<CheckDto> foundCheckDto = checkRepository.findOne(savedCheckDto.getId());

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedCheckDto);

        Assert.assertTrue(foundCheckDto.isPresent());
    }

    @Test
    public void exists()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto checkDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedCheckDto = checkRepository.save(checkDto);

        boolean exists = checkRepository.exists(savedCheckDto.getId());

        userRepository.delete(savedUserDto);
        checkRepository.delete(checkDto);

        Assert.assertTrue(exists);
    }

    @Test
    public void count()
    {
        Long checkCount = checkRepository.count();

        Assert.assertNotNull(checkCount);
    }

    @Test
    public void findAll()
    {
        Collection<CheckDto> checks = checkRepository.findAll();

        Assert.assertNotNull(checks);
    }

    @Test
    public void findAllByIds()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto firstCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url(HttpUrl.parse("http://www.example2.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedFirstCheckDto = checkRepository.save(firstCheckDto);
        CheckDto savedSecondCheckDto = checkRepository.save(secondCheckDto);
        Collection<CheckDto> foundCheckDtos = checkRepository.findAll(
                Arrays.asList(
                        savedFirstCheckDto.getId(),
                        savedSecondCheckDto.getId()));

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedFirstCheckDto);
        checkRepository.delete(savedSecondCheckDto);

        Assert.assertEquals(2, foundCheckDtos.size());
    }

    @Test
    public void insert()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto checkDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedCheckDto = checkRepository.save(checkDto);

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedCheckDto);

        Assert.assertNotNull(savedCheckDto);
    }

    @Test
    public void update()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto checkDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedCheckDto = checkRepository.save(checkDto);

        CheckDto updatedCheckDto = CheckDto.buildFrom(savedCheckDto)
                .name("updated")
                .build();

        CheckDto savedUpdatedCheckDto = checkRepository.save(updatedCheckDto);

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedUpdatedCheckDto);

        Assert.assertEquals("updated", savedUpdatedCheckDto.getName());
    }

    @Test
    public void findElectableChecks()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto firstCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .refresh(Instant
                        .now()
                        .minus(Duration
                                .ofMinutes(1)))
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url(HttpUrl.parse("http://www.example2.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .refresh(Instant
                        .now()
                        .minus(Duration
                                .ofMinutes(1)))
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto thirdCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name3")
                .url(HttpUrl.parse("http://www.example3.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .refresh(Instant
                        .now()
                        .minus(Duration
                                .ofMinutes(1)))
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto fourthCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name4")
                .url(HttpUrl.parse("http://www.example4.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .refresh(Instant
                        .now()
                        .minus(Duration
                                .ofMinutes(1)))
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto fifthCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name5")
                .url(HttpUrl.parse("http://www.example5.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .refresh(Instant
                        .now()
                        .minus(Duration
                                .ofMinutes(1)))
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto sixthCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name6")
                .url(HttpUrl.parse("http://www.example6.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .refresh(Instant
                        .now()
                        .minus(Duration
                                .ofMinutes(1)))
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedFirstCheckDto = checkRepository.save(firstCheckDto);
        CheckDto savedSecondCheckDto = checkRepository.save(secondCheckDto);
        CheckDto savedThirdCheckDto = checkRepository.save(thirdCheckDto);
        CheckDto savedFourthCheckDto = checkRepository.save(fourthCheckDto);
        CheckDto savedFifthCheckDto = checkRepository.save(fifthCheckDto);
        CheckDto savedSixthCheckDto = checkRepository.save(sixthCheckDto);

        Collection<CheckDto> electableChecks = checkRepository
                .findElectableChecks(
                        "probe",
                        false,
                        Instant.now(),
                        5);

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedFirstCheckDto);
        checkRepository.delete(savedSecondCheckDto);
        checkRepository.delete(savedThirdCheckDto);
        checkRepository.delete(savedFourthCheckDto);
        checkRepository.delete(savedFifthCheckDto);
        checkRepository.delete(savedSixthCheckDto);

        Assert.assertTrue(electableChecks.size() == 5);
    }

    @Test
    public void markChecksElected()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto firstCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url(HttpUrl.parse("http://www.example2.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedFirstCheckDto = checkRepository.save(firstCheckDto);
        CheckDto savedSecondCheckDto = checkRepository.save(secondCheckDto);

        Collection<CheckDto> electedCheckDtos = checkRepository.markChecksElected(
                Arrays.asList(
                        savedFirstCheckDto,
                        savedSecondCheckDto));

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedFirstCheckDto);
        checkRepository.delete(savedSecondCheckDto);

        Assert.assertEquals(2, electedCheckDtos.size());
        electedCheckDtos
                .stream()
                .forEach(check -> Assert.assertEquals(
                        State.ELECTED,
                        check.getState()));
    }

    @Test
    public void markChecksElectedEmpty()
    {
        Collection<CheckDto> electedCheckDtos = checkRepository.markChecksElected(Collections.emptyList());
        Assert.assertEquals(0, electedCheckDtos.size());
    }

    @Test
    public void batchUpdate()
    {
        UserDto userDto = UserDto.builder()
                .username("username")
                .email("email@example.com")
                .resetToken("resetToken")
                .accessToken("accessToken")
                .password("password")
                .emailVerified(true)
                .build();

        UserDto savedUserDto = userRepository.save(userDto);

        CheckDto firstCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name")
                .url(HttpUrl.parse("http://www.example.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url(HttpUrl.parse("http://www.example2.com/"))
                .probe(Optional.of("probe"))
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedFirstCheckDto = checkRepository.save(firstCheckDto);
        CheckDto savedSecondCheckDto = checkRepository.save(secondCheckDto);

        CheckDto firstUpdatedCheckDto = CheckDto.buildFrom(savedFirstCheckDto)
                .name("updated")
                .build();

        CheckDto secondUpdatedCheckDto = CheckDto.buildFrom(savedSecondCheckDto)
                .name("updated2")
                .build();

        int updatedCount =
                checkRepository.batchUpdate(
                        Arrays.asList(
                                firstUpdatedCheckDto,
                                secondUpdatedCheckDto));

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedFirstCheckDto);
        checkRepository.delete(savedSecondCheckDto);

        Assert.assertEquals(2, updatedCount);
    }

    @Test
    public void batchUpdateEmpty()
    {
        int updatedCount = checkRepository.batchUpdate(Collections.emptyList());
        Assert.assertEquals(0, updatedCount);
    }

    @Test
    public void deleteAll()
    {
        checkRepository.deleteAll();
        Collection<CheckDto> checkDtos = checkRepository.findAll();

        Assert.assertEquals(0, checkDtos.size());
    }
}

package com.codeaim.urlcheck.repository;

import com.codeaim.urlcheck.Application;
import com.codeaim.urlcheck.domain.CheckDto;
import com.codeaim.urlcheck.domain.State;
import com.codeaim.urlcheck.domain.Status;
import com.codeaim.urlcheck.domain.UserDto;
import com.codeaim.urlcheck.repository.jdbc.CheckRepositoryJdbc;
import com.codeaim.urlcheck.repository.jdbc.UserRepositoryJdbc;
import com.opentable.db.postgres.embedded.FlywayPreparer;
import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.PreparedDbRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
public class CheckRepositoryJdbcTest
{
    private UserRepository userRepository;
    private CheckRepository checkRepository;

    @Rule
    public PreparedDbRule db = EmbeddedPostgresRules.preparedDatabase(
            FlywayPreparer.forClasspathLocation("db/migration"));

    @Before
    public void setup()
    {
        userRepository = new UserRepositoryJdbc(
                new JdbcTemplate(db.getTestDatabase()),
                new NamedParameterJdbcTemplate(db.getTestDatabase()));
        userRepository.deleteAll();

        checkRepository = new CheckRepositoryJdbc(
                new JdbcTemplate(db.getTestDatabase()),
                new NamedParameterJdbcTemplate(db.getTestDatabase()));
        checkRepository.deleteAll();
    }

    @Test
    public void save()
    {
        UserDto userDto = UserDto.builder()
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url("http://www.example2.com")
                .probe("probe")
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
        checkRepository.delete(firstCheckDto);
        checkRepository.delete(secondCheckDto);

        Assert.assertEquals(2, savedCheckDtos.size());
    }

    @Test
    public void findOne()
    {
        UserDto userDto = UserDto.builder()
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
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
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
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
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url("http://www.example2.com")
                .probe("probe")
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
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
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
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
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
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
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
                .url("http://www.example2.com")
                .probe("probe")
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
                .url("http://www.example3.com")
                .probe("probe")
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .refresh(Instant
                        .now()
                        .plus(Duration
                                .ofMinutes(1)))
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto savedFirstCheckDto = checkRepository.save(firstCheckDto);
        CheckDto savedSecondCheckDto = checkRepository.save(secondCheckDto);
        CheckDto savedThirdCheckDto = checkRepository.save(thirdCheckDto);

        Collection<CheckDto> electableChecks = checkRepository
                .findElectableChecks(
                        "probe",
                        false,
                        Instant.now());

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedFirstCheckDto);
        checkRepository.delete(savedSecondCheckDto);
        checkRepository.delete(savedThirdCheckDto);

        Assert.assertEquals(2, electableChecks.size());
    }

    @Test
    public void markChecksElected()
    {
        UserDto userDto = UserDto.builder()
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url("http://www.example2.com")
                .probe("probe")
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        Collection<CheckDto> electedCheckDtos = checkRepository.markChecksElected(
                Arrays.asList(
                        firstCheckDto,
                        secondCheckDto));

        userRepository.delete(savedUserDto);
        checkRepository.delete(firstCheckDto);
        checkRepository.delete(secondCheckDto);

        Assert.assertEquals(2, electedCheckDtos.size());
        electedCheckDtos
                .stream()
                .forEach(check -> Assert.assertEquals(
                        State.ELECTED,
                        check.getState()));
    }

    @Test
    public void batchUpdate()
    {
        UserDto userDto = UserDto.builder()
                .name("name")
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
                .url("http://www.example.com")
                .probe("probe")
                .status(Status.UNKNOWN)
                .state(State.WAITING)
                .interval(1)
                .confirming(true)
                .version(1)
                .build();

        CheckDto secondCheckDto = CheckDto.builder()
                .userId(savedUserDto.getId())
                .name("name2")
                .url("http://www.example2.com")
                .probe("probe")
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

        int updatedCount = Arrays.stream(
                checkRepository.batchUpdate(
                        Arrays.asList(
                                firstUpdatedCheckDto,
                                secondUpdatedCheckDto)))
                .sum();

        userRepository.delete(savedUserDto);
        checkRepository.delete(savedFirstCheckDto);
        checkRepository.delete(savedSecondCheckDto);

        Assert.assertEquals(2, updatedCount);
    }
}
package com.codeaim.urlcheck.task;

import com.codeaim.urlcheck.domain.CheckDto;
import com.codeaim.urlcheck.domain.ResultDto;
import com.codeaim.urlcheck.domain.State;
import com.codeaim.urlcheck.domain.Status;
import com.codeaim.urlcheck.repository.CheckRepository;
import com.codeaim.urlcheck.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CheckTask
{
    private RestTemplate restTemplate;
    private CheckRepository checkRepository;
    private ResultRepository resultRepository;
    private String probe;
    private boolean isClustered;
    private long candidatePoolSize;

    @Autowired
    public CheckTask(
            RestTemplate restTemplate,
            CheckRepository checkRepository,
            ResultRepository resultRepository,
            @Value("${com.codeaim.urlcheck.probe:Standalone}")
            String probe,
            @Value("${com.codeaim.urlcheck.isClustered:false}")
            boolean isClustered,
            @Value("${com.codeaim.urlcheck.candidatePoolSize:5}")
            long candidatePoolSize
    )
    {
        this.restTemplate = restTemplate;
        this.checkRepository = checkRepository;
        this.resultRepository = resultRepository;
        this.probe = probe;
        this.isClustered = isClustered;
        this.candidatePoolSize = candidatePoolSize;
    }

    public void run()
    {
        updateChecks(
                checkRepository,
                markChecksElected(
                        checkRepository,
                        findElectableChecks(
                                checkRepository,
                                probe,
                                isClustered,
                                candidatePoolSize))
                        .stream()
                        .map(electedCheck -> {
                            long startResponseTime =
                                    System.currentTimeMillis();
                            return updateCheckStatus(
                                    checkRepository,
                                    probe,
                                    electedCheck,
                                    createCheckResult(
                                            resultRepository,
                                            probe,
                                            electedCheck,
                                            startResponseTime,
                                            requestCheckUrlStatus(
                                                    restTemplate,
                                                    electedCheck)));
                        })
                        .collect(Collectors.toList()));
    }

    Collection<CheckDto> findElectableChecks(
            CheckRepository checkRepository,
            String probe,
            boolean isClustered,
            long candidatePoolSize
    )
    {
        return checkRepository.findElectableChecks(
                probe,
                isClustered,
                Instant.now(),
                candidatePoolSize);
    }

    Collection<CheckDto> markChecksElected(
            CheckRepository checkRepository,
            Collection<CheckDto> electableChecks
    )
    {
        return checkRepository
                .markChecksElected(
                        electableChecks);
    }

    private HttpStatus requestCheckUrlStatus(
            RestTemplate restTemplate,
            CheckDto checkDto
    )
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");

            return restTemplate
                    .exchange(
                            checkDto.getUrl().uri(),
                            HttpMethod.GET,
                            new HttpEntity<>("", headers),
                            String.class)
                    .getStatusCode();
        }
        catch (HttpStatusCodeException exception)
        {
            return exception
                    .getStatusCode();
        }
        catch (Exception exception)
        {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private ResultDto createCheckResult(
            ResultRepository resultRepository,
            String probe,
            CheckDto checkDto,
            long startResponseTime,
            HttpStatus checkUrlStatus
    )
    {
        return resultRepository.save(ResultDto
                .builder()
                .checkId(checkDto.getId())
                .previousResultId(checkDto.getLatestResultId())
                .probe(probe)
                .responseTime(OptionalLong.of(System.currentTimeMillis() - startResponseTime))
                .statusCode(checkUrlStatus.value())
                .status((checkUrlStatus.is2xxSuccessful()) ? Status.UP : Status.DOWN)
                .changed(!Objects.equals((checkUrlStatus.is2xxSuccessful()) ? Status.UP : Status.DOWN, checkDto.getStatus()))
                .confirmation(checkDto.isConfirming())
                .build());
    }

    private CheckDto updateCheckStatus(
            CheckRepository checkRepository,
            String probe,
            CheckDto checkDto,
            ResultDto resultDto
    )
    {
        if (resultDto.isChanged() && resultDto.isConfirmation())
            return checkRepository.save(
                    statusChangeConfirmed(
                            checkDto,
                            resultDto,
                            probe));
        if (!resultDto.isChanged() && resultDto.isConfirmation())
            return checkRepository.save(
                    statusChangeConfirmationInconclusive(
                            checkDto,
                            resultDto,
                            probe));
        if (resultDto.isChanged())
            return checkRepository.save(
                    statusChangeConfirmationRequired(
                            checkDto,
                            resultDto,
                            probe));

        return checkRepository.save(
                statusChangeNone(
                        checkDto,
                        resultDto,
                        probe));
    }

    private CheckDto statusChangeNone(
            CheckDto checkDto,
            ResultDto resultDto,
            String probe
    )
    {
        return CheckDto
                .buildFrom(checkDto)
                .latestResultId(OptionalLong.of(resultDto.getId()))
                .refresh(Instant.now().plus(
                        checkDto.getInterval(),
                        ChronoUnit.MINUTES))
                .state(State.WAITING)
                .locked(null)
                .probe(Optional.of(probe))
                .build();
    }

    private CheckDto statusChangeConfirmationRequired(
            CheckDto checkDto,
            ResultDto resultDto,
            String probe
    )
    {
        return CheckDto
                .buildFrom(checkDto)
                .latestResultId(OptionalLong.of(resultDto.getId()))
                .confirming(true)
                .state(State.WAITING)
                .locked(null)
                .probe(Optional.of(probe))
                .build();
    }

    private CheckDto statusChangeConfirmationInconclusive(
            CheckDto checkDto,
            ResultDto resultDto,
            String probe
    )
    {
        return CheckDto
                .buildFrom(checkDto)
                .latestResultId(OptionalLong.of(resultDto.getId()))
                .confirming(false)
                .state(State.WAITING)
                .locked(null)
                .probe(Optional.of(probe))
                .build();
    }

    private CheckDto statusChangeConfirmed(
            CheckDto checkDto,
            ResultDto resultDto,
            String probe
    )
    {
        return CheckDto
                .buildFrom(checkDto)
                .latestResultId(OptionalLong.of(resultDto.getId()))
                .status(resultDto.getStatus())
                .confirming(false)
                .refresh(Instant.now().plus(
                        checkDto.getInterval(),
                        ChronoUnit.MINUTES))
                .state(State.WAITING)
                .locked(null)
                .probe(Optional.of(probe))
                .build();
    }

    private int updateChecks(
            CheckRepository checkRepository,
            List<CheckDto> updatedChecks
    )
    {
        return checkRepository
                .batchUpdate(updatedChecks);
    }
}













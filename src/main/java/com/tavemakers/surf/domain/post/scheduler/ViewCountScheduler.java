package com.tavemakers.surf.domain.post.scheduler;

import com.tavemakers.surf.domain.post.dto.PostViewUpdateDto;
import com.tavemakers.surf.domain.post.mapper.PostMapper;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.domain.post.service.PostUpdateService;
import com.tavemakers.surf.global.common.aop.annotations.ExecutionTimeLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final StringRedisTemplate redisTemplate;
    private final PostMapper postMapper;
    private final PostUpdateService postUpdateService;

    private static final String VIEW_COUNT_PATTERN = "post:*:view:count";
    private static final int SCAN_SIZE = 1000;

    @Scheduled(cron = "0 0 * * * *")
    @ExecutionTimeLog(jobName = "조회수 동기화 작업")
    public void synchronizeViewCount() {
        ScanOptions scanOption = ScanOptions.scanOptions()
                .match(VIEW_COUNT_PATTERN)
                .count(SCAN_SIZE)
                .build();

        List<String> keyBuffer = new ArrayList<>(SCAN_SIZE);
        try (Cursor<String> cursor = redisTemplate.scan(scanOption)) {
            while (cursor.hasNext()) {
                keyBuffer.add(cursor.next());

                if (keyBuffer.size() >= SCAN_SIZE) {
                    processBulkUpdate(keyBuffer);
                    keyBuffer.clear();
                }
            }

            if (!keyBuffer.isEmpty()) {
                processBulkUpdate(keyBuffer);
            }
        } catch (Exception e) {
            log.error("조회수 동기화 작업에 실패했습니다.", e);
        }
    }

    private void processBulkUpdate(List<String> viewCountKeys) {
        List<String> viewCountValues = readViewCountValues(viewCountKeys);
        List<PostViewUpdateDto> updateDtoList = convertToUpdateDtoList(viewCountKeys, viewCountValues);
        executeViewCountUpdate(updateDtoList);
    }

    private List<String> readViewCountValues(List<String> keys) {
        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        return values != null ? values : List.of();
    }

    private List<PostViewUpdateDto> convertToUpdateDtoList(List<String> keys, List<String> values) {
        return IntStream.range(0, keys.size())
                .filter(i -> values.get(i) != null)
                .mapToObj(i -> postMapper.toUpdateDto(keys.get(i), values.get(i)))
                .filter(Objects::nonNull)
                .toList();
    }

    private void executeViewCountUpdate(List<PostViewUpdateDto> updateDtoList) {
        if (!updateDtoList.isEmpty()) {
            postUpdateService.updateViewCount(updateDtoList);
        }
    }

}

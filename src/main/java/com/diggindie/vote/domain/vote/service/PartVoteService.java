package com.diggindie.vote.domain.vote.service;

import com.diggindie.vote.domain.vote.dto.PartVoteRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PartVoteService {

    private final RedissonClient redissonClient;
    private final PartVoteExecutor partVoteExecutor;

    private static final String VOTE_LOCK_PREFIX = "part-vote:lock:";

    public void vote(String loginId, PartVoteRequestDto request) {
        String lockKey = VOTE_LOCK_PREFIX + loginId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(5, 3, TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("요청이 많습니다. 잠시 후 다시 시도해주세요.");
            }

            partVoteExecutor.execute(loginId, request);
            log.info("파트장 투표 완료 - loginId: {}, candidateId: {}", loginId, request.candidateId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("투표 처리 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
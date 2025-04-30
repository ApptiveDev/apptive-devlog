package apptive.devlog.support.testconfig.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("local")
public class RedisInitializer implements ApplicationRunner {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        Set<String> keys = redisTemplate.keys("*");

        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Redis 초기화 완료 - {}개의 키 삭제됨", keys.size());
        } else {
            log.info("Redis 초기화 생략 - 삭제할 키 없음");
        }
    }
}

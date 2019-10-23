package com.wext.common.bean;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@SuppressWarnings("unchecked")
public class RedisTool<V> {
    // 参考：https://www.jianshu.com/p/071bae3834b0

    private RedisTemplate redisTemplate;

    @Autowired
    public RedisTool(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean hasKey(@NonNull String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    public Long getExpire(@NonNull String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public boolean expire(@NonNull String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // ============================String(字符串)=============================

    public V get(@NonNull String key) {
        return (V) redisTemplate.opsForValue().get(key);
    }

    public boolean set(@NonNull String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean setIfPresent(@NonNull String key, V value) {
        try {
            redisTemplate.opsForValue().setIfPresent(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean setIfPresent(@NonNull String key, V value, Long time) {
        try {
            Duration ttl = Duration.of(time, ChronoUnit.SECONDS); // 10秒过期
            redisTemplate.opsForValue().setIfPresent(key, value, ttl);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     */
    public boolean set(@NonNull String key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                return set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // ===============================List(列表)=================================
    public List<V> listGet(@NonNull String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Long listSize(@NonNull String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0L;
        }
    }

    public Long listRightPush(@NonNull String key, V value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1L;
        }
    }

    public Long listLeftPush(@NonNull String key, V value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1L;
        }
    }

    public Long listRightPushAll(@NonNull String key, Collection<V> value) {
        try {
            return redisTemplate.opsForList().rightPushAll(key, value);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1L;
        }
    }

    public Long listLeftPushAll(@NonNull String key, Collection<V> value) {
        try {
            return redisTemplate.opsForList().leftPushAll(key, value);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1L;

        }
    }

    public boolean listTrim(@NonNull String key, int from, int end) {
        try {
            redisTemplate.opsForList().trim(key, from, end);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public Long listLeftPushIfPresent(@NonNull String key, V value) {
        try {
            return redisTemplate.opsForList().leftPushIfPresent(key, value);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1L;
        }
    }

    //-----------------------锁----------------------

    public Boolean lock(@NonNull String key) {
        try {
            Duration ttl = Duration.of(10, ChronoUnit.SECONDS); // 10秒过期
            return redisTemplate.opsForValue().setIfAbsent(key, "lock value", ttl);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;    // 如果正常返回会导致业务流程无限等待
        }
    }

    //----------------------ZSet（有序集合）---------------

    public Boolean zsetAdd(@NonNull String key, V value, @NonNull Double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public Long zsetAdd(@NonNull String key, Set<ZSetOperations.TypedTuple<V>> sets) {
        try {
            return redisTemplate.opsForZSet().add(key, sets);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1L;
        }
    }

    public Set<V> zsetGetByRange(@NonNull String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public Long zsetRemove(@NonNull String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    public Long zsetRemove(@NonNull String key, V value) {
        return redisTemplate.opsForZSet().remove(key, value);
    }

    public Long zsetSize(@NonNull String key) {
        return redisTemplate.opsForZSet().size(key);
    }
}

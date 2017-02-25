package com.andyaylward.enviropi.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.muchq.lunarcat.util.PublicPreconditions;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Singleton
public class SensorDataManager {
  private static final int ONE_HOUR_MILLIS = 1000 * 60 * 60;
  private static final long ONE_DAY_MILLIS = ONE_HOUR_MILLIS * 24;
  private static final long DURATION_TO_KEEP = ONE_DAY_MILLIS * 3;
  private static final int START = 0;

  private final JedisPool pool;
  private final ObjectMapper mapper;
  private final Clock clock;

  @Inject
  public SensorDataManager(JedisPool pool, ObjectMapper mapper, Clock clock) {
    this.pool = pool;
    this.mapper = mapper;
    this.clock = clock;
  }

  public void insertEvent(SensorRecord sensorRecord) {
    String key = "" + sensorRecord.getDeviceId();
    try (Jedis jedis = pool.getResource()) {
      jedis.zadd(key, sensorRecord.getTime(), write(sensorRecord));
    }
  }

  public List<SensorRecord> getEvents(long deviceId, long from, long to) {
    validateRange(from, to);
    String key = "" + deviceId;
    try (Jedis jedis = pool.getResource()) {
      return read(jedis.zrangeByScore(key, from, to));
    }
  }

  public void expire(long deviceId) {
    String key = "" + deviceId;
    long upTo = clock.millis() - DURATION_TO_KEEP;
    try (Jedis jedis = pool.getResource()) {
      jedis.zremrangeByScore(key, START, upTo);
    }
  }

  private String write(SensorRecord r) {
    try {
      return mapper.writeValueAsString(r);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<SensorRecord> read(Set<String> records) {
    List<SensorRecord> result = new ArrayList<>();

    for (String record : records) {
      try {
        result.add(mapper.readValue(record, SensorRecord.class));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return result;
  }

  private void validateRange(long from, long to) {
    PublicPreconditions.checkArgument(to >= from, "from must be before to");
    PublicPreconditions.checkArgument(to - from <= ONE_DAY_MILLIS, "maximum range is one day");
  }
}

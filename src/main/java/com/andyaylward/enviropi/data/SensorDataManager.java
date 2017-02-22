package com.andyaylward.enviropi.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Singleton
public class SensorDataManager {
  private static final int ONE_HOUR_MILLIS = 1000 * 60 * 60;
  private static final long ONE_DAY_MILLIS = ONE_HOUR_MILLIS * 24;
  private static final int ONE_WEEK_SECONDS = 60 * 60 * 24 * 7;

  private final JedisPool pool;
  private final ObjectMapper mapper;

  @Inject
  public SensorDataManager(JedisPool pool, ObjectMapper mapper) {
    this.pool = pool;
    this.mapper = mapper;
  }

  public void insertEvent(SensorRecord sensorRecord) {
    try (Jedis jedis = pool.getResource()) {
      String bucketKey = "" + previousBucketFromMillis(sensorRecord.getTime());
      boolean addExpiration = !jedis.exists(bucketKey);

      jedis.zadd(bucketKey, sensorRecord.getTime(), write(sensorRecord));

      if (addExpiration) {
        jedis.expire(bucketKey, ONE_WEEK_SECONDS);
      }
    }
  }

  public List<SensorRecord> getEvents(long from, long to) {
    validateRange(from, to);
    List<SensorRecord> results = new ArrayList<>();
    long bucket = previousBucketFromMillis(from);
    long lastBucket = nextBucketFromMillis(to);

    while (bucket <= lastBucket) {
      try (Jedis jedis = pool.getResource()) {
        results.addAll(read(jedis.zrange("" + bucket, 0, -1)));
      }
      bucket += ONE_HOUR_MILLIS;
    }

    return results;
  }

  private long previousBucketFromMillis(long millis) {
    return millis - (millis % ONE_HOUR_MILLIS);
  }

  private long nextBucketFromMillis(long millis) {
    return previousBucketFromMillis(millis) + ONE_HOUR_MILLIS;
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
    Preconditions.checkArgument(to >= from, "from must be before to");
    Preconditions.checkArgument(to - from <= ONE_DAY_MILLIS, "maximum range is one day");
  }
}

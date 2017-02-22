package com.andyaylward.enviropi.config;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.liveconfig.LiveConfig;
import com.hubspot.liveconfig.LiveConfigModule;
import com.muchq.guice.ReinstallableGuiceModule;
import com.muchq.json.ObjectMapperModule;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class EnviroModule extends ReinstallableGuiceModule {
  @Override
  protected void configure() {
    install(new ObjectMapperModule());
    install(new LiveConfigModule(LiveConfig.builder()
                                     .usingEnvironmentVariables()
                                     .usingSystemProperties()
                                     .build()));
  }

  @Provides
  @Singleton
  public JedisPool getJedis(@Named("enviropi.redis.host") String redisHost) {
    return new JedisPool(new JedisPoolConfig(), redisHost);
  }
}

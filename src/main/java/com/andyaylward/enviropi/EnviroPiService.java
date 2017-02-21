package com.andyaylward.enviropi;

import com.andyaylward.enviropi.config.EnviroModule;
import com.muchq.lunarcat.Service;
import com.muchq.lunarcat.config.Configuration;

public class EnviroPiService {
  public static void main(String[] args) {
    Configuration configuration = Configuration.newBuilder()
        .withBasePackage(EnviroPiService.class.getPackage())
        .withModules(new EnviroModule())
        .build();
    new Service(configuration).run();
  }
}

package com.andyaylward.enviropi.features;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFeature implements Feature {

  @Override
  public boolean configure(FeatureContext context) {
    CorsFilter corsFilter = new CorsFilter();
    corsFilter.getAllowedOrigins().add("*");
    corsFilter.setAllowedMethods("GET, POST, PUT, DELETE, OPTIONS, HEAD");
    corsFilter.setAllowCredentials(true);
    corsFilter.setAllowedHeaders("enviro-key, origin, content-type, accept, authorization");
    context.register(corsFilter);
    return true;
  }
}

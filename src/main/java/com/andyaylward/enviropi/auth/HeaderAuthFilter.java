package com.andyaylward.enviropi.auth;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.MessageDigest;

@Provider
public class HeaderAuthFilter implements ContainerRequestFilter {
  private static final String API_KEY_HEADER_NAME = "enviro-key";
  private static final Response FOUR_OH_ONE = Response
      .status(401)
      .entity(new UnauthorizedResponse())
      .build();

  @Context
  private ResourceInfo resourceInfo;
  private final String apiKey;

  @Inject
  public HeaderAuthFilter(@Named("apikey") String key) {
    this.apiKey = key;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    if (!resourceInfo.getResourceMethod().isAnnotationPresent(ApiKey.class)) {
      return;
    }

    String key = requestContext.getHeaderString(API_KEY_HEADER_NAME);
    if (key == null || !MessageDigest.isEqual(apiKey.getBytes(), key.getBytes())) {
      requestContext.abortWith(FOUR_OH_ONE);
    }
  }

  private static class UnauthorizedResponse {
    private final String message = "unauthorized";

    public String getMessage() {
      return message;
    }
  }
}

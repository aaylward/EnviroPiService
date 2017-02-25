package com.andyaylward.enviropi.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("hello")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

  @GET
  public Response sayHi() {
    return Response.ok(new Hi()).build();
  }

  static class Hi {
    private final String msg = "hi";

    @JsonProperty("msg")
    public String getMsg() {
      return msg;
    }
  }
}

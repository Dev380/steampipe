package com.github.dev380.steampipe.response;

import java.util.stream.Collectors;
import org.bukkit.ChatColor;

/**
 * A response to a command call, returned by inheritors of {@link
 * com.github.dev380.steampipe.command.Command}
 */
public class Response {
  /** One of three possible command return statuses */
  public static enum Status {
    SUCCESS,
    ERROR,
    INFO
  }

  private Status status;
  private ResponseMessage message;

  /**
   * Make a new Response
   *
   * @param status the status of the command
   * @param message the message to send to the player
   * @see Status
   */
  public Response(Status status, ResponseMessage message) {
    this.status = status;
    this.message = message;
  }

  /** Utility method to turn the Response into a String */
  public String asString() {
    return message.getComponents().stream()
        .map(component -> ChatColor.RESET + component.asString(status))
        .collect(Collectors.joining());
  }
}

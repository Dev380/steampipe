package com.github.dev380.steampipe.command;

import com.github.dev380.steampipe.response.Response;
import com.github.dev380.steampipe.response.ResponseMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import org.bukkit.entity.Player;

/**
 * A class to manage commands. To use, make a new instance and call @link{#register(Command)} on all
 * of your commands. Then, call {@link handleCommand(Player, String)} in your PlayerChatEvent
 * listener.
 */
public class CommandManager {
  private ArrayList<Command> commands = new ArrayList<Command>();
  private String commandPrefix = "";
  private String outputPrefix = "";
  private Response NO_PERMISSION =
      new Response(
          Response.Status.ERROR,
          new ResponseMessage("You don't have permission to use this command."));
  private Response INVALID_ARGS =
      new Response(Response.Status.ERROR, new ResponseMessage("Invalid amount of arguments."));
  private Response NOT_FOUND =
      new Response(Response.Status.ERROR, new ResponseMessage("Command not found."));

  /**
   * Make a new CommandManager
   *
   * @param commandPrefix the prefix to use for commands For example, if you want to use "steampipe
   *     help", set this to "steampipe"
   * @param outputPrefix the prefix to use for output For example, if you want to see "[Steampipe]
   *     Welcome to steampipe!", set this to "[Steampipe] " (with the space!)
   */
  public CommandManager(String commandPrefix, String outputPrefix) {
    commands = new ArrayList<Command>();
    this.commandPrefix = commandPrefix.replaceAll("\\s", "");
    this.outputPrefix = outputPrefix;
  }

  /** Register a new command with this CommandManager */
  public CommandManager register(Command command) {
    commands.add(command);
    return this;
  }

  /**
   * Registers steampipe's help command Convenience method because {@link CommandHelp} requires a
   * reference to the CommandManager so it can't chain
   *
   * @param pluginName the name of the plugin to be shown in the help menu
   */
  public CommandManager registerHelp(String pluginName) {
    commands.add(new CommandHelp(pluginName, this));
    return this;
  }

  /** Returns an ArrayList of registered commands */
  public ArrayList<Command> getCommandList() {
    return commands;
  }

  public String getCommandPrefix() {
    return commandPrefix;
  }

  public String getOutputPrefix() {
    return outputPrefix;
  }

  /**
   * Handle a command given a message Should be ran during a PlayerChatEvent
   *
   * @param player the player who sent the message
   * @param message the message the player sent
   * @return if a command was detected in the message and handled
   */
  public Boolean handleCommand(Player player, String message) {
    if (!commandPrefix.equals(message.split(" ")[0])) {
      return false;
    }

    Boolean handledCommand = false;

    String[] commandAndArgs = getCommandAndArgs(message);
    String commandName = commandAndArgs[0];
    String[] args = Arrays.copyOfRange(commandAndArgs, 1, commandAndArgs.length);

    Response response = null;
    for (Command command : commands) {
      if (command.getName().equals(commandName)) {
        handledCommand = true;
        if (!command.hasPermission(player)) {
          response = NO_PERMISSION;
          break;
        }
        if (command.getArgs().getAmount() != args.length) {
          response = INVALID_ARGS;
        }
        response = command.run(new Context(player, args));
        break;
      }
    }

    if (response == null) {
      response = NOT_FOUND;
    }
    player.sendMessage(outputPrefix + response.asString());

    return handledCommand;
  }

  private String[] getCommandAndArgs(String message) {
    String[] args = message.split(" ");
    String commandName;
    if (commandPrefix.equals("")) {
      commandName = args[0];
      args = Arrays.copyOfRange(args, 1, args.length);
    } else {
      if (!args[0].equals(commandPrefix)) {
        return null;
      }
      commandName = args[1];
      args = Arrays.copyOfRange(args, 2, args.length);
    }
    return Stream.concat(Stream.of(commandName), Arrays.stream(args)).toArray(String[]::new);
  }
}

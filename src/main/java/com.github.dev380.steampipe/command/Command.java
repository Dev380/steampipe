package com.github.dev380.steampipe.command;

import com.github.dev380.steampipe.response.Response;
import org.bukkit.entity.Player;

/** The base class of all pipewire commands */
public abstract class Command {
  private String name;
  private String description;
  private Args args;
  private Category category;

  /**
   * Make a new Command
   *
   * @param name the name of the command
   * @param description the description of the command
   * @param args the arguments of the command
   * @param category the category of the command
   */
  public Command(String name, String description, Args args, Category category) {
    this.name = name;
    this.description = description;
    this.args = args;
    this.category = category;
  }

  /** Override this to set if a player has permission to run a command, defaults to always true */
  public Boolean hasPermission(Player player) {
    return true;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Args getArgs() {
    return args;
  }

  public Category getCategory() {
    return category;
  }

  /**
   * Run this command, called by {@link CommandManager#handleCommand(Player, String)} when a command
   * is supposed to be run
   */
  public abstract Response run(Context ctx);
}

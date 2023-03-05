package com.github.dev380.steampipe.command;

import org.bukkit.entity.Player;

/** Context that a command gets when it is called */
public class Context {
  private Player player;
  private String[] args;

  protected Context(Player player, String[] args) {
    this.player = player;
    this.args = args;
  }

  /** Get the player that sent a command */
  public Player getPlayer() {
    return player;
  }

  /** Get the arguments the player sent when calling this command */
  public String[] getArgs() {
    return args;
  }
}

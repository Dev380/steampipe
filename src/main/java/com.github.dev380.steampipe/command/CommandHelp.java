package com.github.dev380.steampipe.command;

import com.github.dev380.steampipe.Colors;
import java.util.Arrays;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/** A help command for steampipe commands */
public class CommandHelp extends Command {
  private static final int COMMANDS_PER_PAGE = 9;
  private static final int MAX_ARGS_PER_COMMAND =
      2; // Max args to show, not max args that a command can have.
  private String pluginName;

  public CommandHelp(String pluginName) {
    super(
        "help",
        "help menu",
        new Args().add("page", "a help page number or command to view help for"),
        Category.DEFAULT);
    this.pluginName = pluginName;
  }

  public void run(Player player, String[] args) {
    HelpStringGetter helpStringHandler;
    Boolean argInCommandList =
        Arrays.stream(CommandManager.getCommandNames()).anyMatch(args[0]::equals);

    Integer amountOfHelpPages = CommandManager.getCommands().length / COMMANDS_PER_PAGE + 1;
    if (isNumeric(args[0])
        && 0 <= Integer.parseInt(args[0])
        && Integer.parseInt(args[0]) <= amountOfHelpPages) {
      helpStringHandler = new HelpNumberHandler(MAX_ARGS_PER_COMMAND, COMMANDS_PER_PAGE);
    } else if (argInCommandList) {
      helpStringHandler = new HelpForCommandHandler(MAX_ARGS_PER_COMMAND);
    } else {
      helpStringHandler = new InvalidArgHandler();
    }
    player.sendMessage(helpStringHandler.getHelpString(args[0]));
  }

  private Boolean isNumeric(String string) {
    try {
      Integer.parseInt(string);
      return true;
    } catch (NumberFormatException notNumeric) {
      return false;
    }
  }
}

interface HelpStringGetter {
  /**
   * Attempts to get help info
   *
   * @param helpArg argument given to help function
   * @return the help response, or null if could not process
   */
  public String getHelpString(String helpArg);
}

class HelpNumberHandler implements HelpStringGetter {
  private int maxArgsPerCommand;
  private int commandsPerPage;

  public HelpNumberHandler(int maxArgsPerCommand, int commandsPerPage) {
    this.maxArgsPerCommand = maxArgsPerCommand;
    this.commandsPerPage = commandsPerPage;
  }

  @Override
  public String getHelpString(String helpArg) {
    try {
      String helpOutput = Prefix.INFO;
      int pageNum = Integer.parseInt(helpArg);

      helpOutput += helpPageHeader(pageNum);

      // Get list of commands on the given page
      Command[] commandsToDisplay = getPagedCommands(pageNum);

      for (Command command : commandsToDisplay) {
        helpOutput += new HelpLineGetter(maxArgsPerCommand).getHelpLine(command);
      }
      return helpOutput;
    } catch (NumberFormatException shouldNeverHappen) {
      return null;
    }
  }

  private String helpPageHeader(int pageNum) {
    return (pageNum == 1)
        ? "Welcome to " + pluginName + "!"
        : pluginName + " help menu: Page " + ChatColor.BOLD + Prefix.HIGHLIGHT_COLOR + pageNum;
  }
  // The commands in a given page number
  private Command[] getPagedCommands(int pageNum) {
    return Arrays.stream(
            Arrays.copyOfRange(
                CommandManager.getCommands(),
                (pageNum - 1) * commandsPerPage,
                pageNum * commandsPerPage))
        .filter(command -> command != null)
        .toArray(Command[]::new);
  }
}

class HelpForCommandHandler implements HelpStringGetter {
  private int maxArgsPerCommand;

  public HelpForCommandHandler(int maxArgsPerCommand) {
    this.maxArgsPerCommand = maxArgsPerCommand;
  }

  @Override
  public String getHelpString(String helpArg) {
    String helpOutput = Prefix.INFO;
    helpOutput += pluginName + " help menu";

    for (Command command : CommandManager.getCommands()) {
      if (helpArg.equals(command.getName())) {
        helpOutput += new HelpLineGetter(maxArgsPerCommand).getHelpLine(command);
        helpOutput += new HelpLineGetter(maxArgsPerCommand).getInDepthHelp(command);
      }
    }
    return helpOutput;
  }
}

class InvalidArgHandler implements HelpStringGetter {
  @Override
  public String getHelpString(String helpArg) {
    return Prefix.ERROR + "Help page not found: " + ChatColor.BOLD + helpArg;
  }
}

class HelpLineGetter {
  private int maxArgsPerCommand;

  /**
   * Make a new HelpLineGetter
   *
   * @param maxArgsPerCommand the maximum amount of arguments to show for a command before
   *     truncating
   */
  public HelpLineGetter(int maxArgsPerCommand) {
    this.maxArgsPerCommand = maxArgsPerCommand;
  }

  /** Gets a line of help in the format `command [args]` */
  public String getHelpLine(Command command) {
    return "\n"
        + Colors.highlight
        + ChatColor.BOLD
        + command.getName()
        + ChatColor.RESET
        + Color.highlight
        + " "
        + command.getArgs().getNames()
        + Colors.info
        + " - "
        + command.getDescription();
  }

  /** In depth help */
  public String getInDepthHelp(Command command) {
    String helpOutput = "";
    for (Args.Entry entry : command.getArgs().getArgs()) {
      helpOutput +=
          "\n"
              + Prefix.INFO_COLOR
              + "-"
              + Prefix.HIGHLIGHT_COLOR
              + ChatColor.BOLD
              + entry.getName()
              + ChatColor.RESET
              + Prefix.INFO_COLOR
              + " - "
              + entry.getDescription();
    }
    return helpOutput;
  }
}

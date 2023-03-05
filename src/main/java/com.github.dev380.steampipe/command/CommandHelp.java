package com.github.dev380.steampipe.command;

import com.github.dev380.steampipe.Colors;
import com.github.dev380.steampipe.response.Response;
import com.github.dev380.steampipe.response.ResponseMessage;
import java.util.ArrayList;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/** A help command for steampipe commands */
public class CommandHelp extends Command {
  private static final int COMMANDS_PER_PAGE = 9;
  private static final int MAX_ARGS_PER_COMMAND =
      2; // Max args to show, not max args that a command can have.
  private String pluginName;
  private CommandManager commandManager;

  public CommandHelp(String pluginName, CommandManager commandManager) {
    super(
        "help",
        "help menu",
        new Args().add("page", "a help page number or command to view help for"),
        Category.DEFAULT);
    this.pluginName = pluginName;
    this.commandManager = commandManager;
  }

  @Override
  public Response run(Context ctx) {
    Player player = ctx.getPlayer();
    String[] args = ctx.getArgs();
    HelpStringGetter helpStringHandler;
    Response.Status status = Response.Status.SUCCESS;
    Boolean argInCommandList = commandManager.getCommandList().stream().anyMatch(args[0]::equals);

    Integer amountOfHelpPages = commandManager.getCommandList().size() / COMMANDS_PER_PAGE + 1;
    if (isNumeric(args[0])
        && 0 <= Integer.parseInt(args[0])
        && Integer.parseInt(args[0]) <= amountOfHelpPages) {
      helpStringHandler =
          new HelpNumberHandler(
              pluginName, commandManager.getCommandList(), MAX_ARGS_PER_COMMAND, COMMANDS_PER_PAGE);
    } else if (argInCommandList) {
      helpStringHandler =
          new HelpForCommandHandler(
              pluginName, commandManager.getCommandList(), MAX_ARGS_PER_COMMAND);
    } else {
      status = Response.Status.ERROR;
      helpStringHandler = new InvalidArgHandler();
    }
    return new Response(status, helpStringHandler.getHelpString(args[0]));
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
  public ResponseMessage getHelpString(String helpArg);
}

class HelpNumberHandler implements HelpStringGetter {
  private String pluginName;
  private ArrayList<Command> commandList;
  private int maxArgsPerCommand;
  private int commandsPerPage;

  public HelpNumberHandler(
      String pluginName,
      ArrayList<Command> commandList,
      int maxArgsPerCommand,
      int commandsPerPage) {
    this.pluginName = pluginName;
    this.maxArgsPerCommand = maxArgsPerCommand;
    this.commandsPerPage = commandsPerPage;
  }

  @Override
  public ResponseMessage getHelpString(String helpArg) {
    try {
      ResponseMessage helpOutput = new ResponseMessage();
      int pageNum = Integer.parseInt(helpArg);

      helpOutput.add(helpPageHeader(pageNum));

      // Get list of commands on the given page
      Command[] commandsToDisplay = getPagedCommands(pageNum);

      for (Command command : commandsToDisplay) {
        helpOutput.add(new HelpLineGetter(maxArgsPerCommand).getHelpLine(command));
      }
      return helpOutput;
    } catch (NumberFormatException shouldNeverHappen) {
      return null;
    }
  }

  private ResponseMessage helpPageHeader(int pageNum) {
    return (pageNum == 1)
        ? new ResponseMessage("Welcome to " + pluginName + "!")
        : new ResponseMessage(pluginName + " help menu: Page ")
            .addEmphasis(String.valueOf(pageNum));
  }
  // The commands in a given page number
  private Command[] getPagedCommands(int pageNum) {
    return commandList.subList((pageNum - 1) * commandsPerPage, pageNum * commandsPerPage).stream()
        .filter(command -> command != null)
        .toArray(Command[]::new);
  }
}

class HelpForCommandHandler implements HelpStringGetter {
  private String pluginName;
  private ArrayList<Command> commandList;
  private int maxArgsPerCommand;

  public HelpForCommandHandler(
      String pluginName, ArrayList<Command> commandList, int maxArgsPerCommand) {
    this.pluginName = pluginName;
    this.maxArgsPerCommand = maxArgsPerCommand;
  }

  @Override
  public ResponseMessage getHelpString(String helpArg) {
    ResponseMessage helpOutput = new ResponseMessage();
    helpOutput.addString(pluginName + " help menu");

    for (Command command : commandList) {
      if (helpArg.equals(command.getName())) {
        helpOutput.add(new HelpLineGetter(maxArgsPerCommand).getHelpLine(command));
        helpOutput.add(new HelpLineGetter(maxArgsPerCommand).getInDepthHelp(command));
      }
    }
    return helpOutput;
  }
}

class InvalidArgHandler implements HelpStringGetter {
  @Override
  public ResponseMessage getHelpString(String helpArg) {
    return new ResponseMessage("Help page not found: ").addEmphasis(helpArg);
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
  public ResponseMessage getHelpLine(Command command) {
    return new ResponseMessage("\n")
        .addString("" + Colors.highlight + ChatColor.BOLD + command.getName())
        .addEmphasis(" " + command.getArgs().getNames().stream().collect(Collectors.joining(" ")))
        .addString(" - " + command.getDescription());
  }

  /** In depth help */
  public ResponseMessage getInDepthHelp(Command command) {
    ResponseMessage helpOutput = new ResponseMessage();
    for (Args.Entry entry : command.getArgs().getArgs()) {
      helpOutput
          .addString("\n-" + Colors.highlight + ChatColor.BOLD + entry.getName())
          .addString(" - " + entry.getDescription());
    }
    return helpOutput;
  }
}

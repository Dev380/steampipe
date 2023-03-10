package com.github.dev380.steampipe.response;

import com.github.dev380.steampipe.Colors;
import java.util.ArrayList;
import org.bukkit.ChatColor;

public class ResponseMessage {
  private ArrayList<Component> components;

  public ResponseMessage() {
    components = new ArrayList<Component>();
  }

  public ResponseMessage(String text) {
    this();
    this.addString(text);
  }

  /** Add another ResponseMessage to this one */
  public ResponseMessage add(ResponseMessage responseMessage) {
    for (Component component : responseMessage.components) {
      components.add(component);
    }
    return this;
  }

  /** Add a singular component to this ResponseMessage */
  public ResponseMessage addComponent(Component component) {
    components.add(component);
    return this;
  }

  /**
   * addComponent but easier to type
   *
   * @see addComponent(Component)
   */
  public ResponseMessage add(Component component) {
    return this.addComponent(component);
  }

  public ArrayList<Component> getComponents() {
    return components;
  }

  /** Add a string to this component */
  public ResponseMessage addString(String string) {
    components.add(new StringComponent(string));
    return this;
  }

  /** Add an emphasized string to this component */
  public ResponseMessage addEmphasis(String string) {
    components.add(new EmphasisComponent(string));
    return this;
  }

  public interface Component {
    public String toOkString();

    public String toErrorString();

    public String toInfoString();

    public default String asString(Response.Status status) {
      switch (status) {
        case SUCCESS:
          return toOkString();
        case ERROR:
          return toErrorString();
        case INFO:
          return toInfoString();
        default:
          return toOkString();
      }
    }
  }

  public class StringComponent implements Component {
    private String string;

    public StringComponent(String string) {
      this.string = string;
    }

    @Override
    public String toOkString() {
      return Colors.success + string;
    }

    @Override
    public String toErrorString() {
      return Colors.error + string;
    }

    @Override
    public String toInfoString() {
      return Colors.info + string;
    }
  }

  public class EmphasisComponent implements Component {
    private String string;

    public EmphasisComponent(String string) {
      this.string = string;
    }

    @Override
    public String toOkString() {
      return Colors.highlight + "" + ChatColor.BOLD + string;
    }

    @Override
    public String toErrorString() {
      return Colors.error + "" + ChatColor.BOLD + string;
    }

    @Override
    public String toInfoString() {
      return Colors.highlight + string;
    }
  }
}

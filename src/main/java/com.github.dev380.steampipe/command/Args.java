package com.github.dev380.steampipe.command;

import java.util.ArrayList;
import java.util.stream.Collectors;

/** Arguments to a Command */
public class Args {
  /** Convenience field for when you have no arguments */
  public static final Args NONE = new Args();

  private ArrayList<Entry> args;

  /** Intialize a new list of arguments with nothing */
  public Args() {
    this.args = new ArrayList<Entry>();
  }

  /**
   * Add a new argument Returns itself for chaining.
   *
   * @param name the name of the argument to add
   * @param description the description of the argument
   */
  public Args add(String name, String description) {
    args.add(new Entry(name, description));
    return this;
  }

  /**
   * Get a list of the arguments
   *
   * @return ArrayList of the arguments
   * @see Entry
   */
  public ArrayList<Entry> getArgs() {
    return args;
  }

  /**
   * Get a list of just the names of the arguments
   *
   * @return ArrayList of the names of the arguments
   * @see getArgs
   */
  public ArrayList<String> getNames() {
    return args.stream().map(Entry::getName).collect(Collectors.toCollection(ArrayList::new));
  }

  /** Get amount of arguments stored in this object */
  public int getAmount() {
    return args.size();
  }

  /** A single argument, consisting of a name and description */
  public static class Entry {
    private String name;
    private String description;

    public Entry(String name, String description) {
      this.name = name;
      this.description = description;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }
  }
}

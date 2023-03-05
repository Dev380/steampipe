package com.github.dev380.steampipe.command;

import java.awt.Color;

/**
 * A category of commands This is purely for aesthetic and organization purposes, such as a help
 * command.
 */
public class Category {
  /** Convenience field for if you don't want to use categories */
  public static final Category DEFAULT = new Category("Default");

  private String name;
  private String description;
  private char symbol;
  private Color color;

  public Category(String name) {
    this.name = name;
  }

  /**
   * Make a new category
   *
   * @param name the name of the category
   * @param description a description of the category
   * @param symbol a character to represent the category
   * @param color a color to represent the category
   */
  public Category(String name, String description, char symbol, Color color) {
    this.name = name;
    this.description = description;
    this.symbol = symbol;
    this.color = color;
  }
}

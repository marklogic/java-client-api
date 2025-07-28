/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A ValuesDefinition represents a values query used to retreive values from the server.
 */
public interface ValuesDefinition {
  /** The possible directions.
   *
   */
  public enum Direction {
    ASCENDING, DESCENDING;
  }

  /** The kinds of frequencies.
   *
   */
  public enum Frequency {
    FRAGMENT, ITEM;
  }

  /**
   * Returns the name of the values constraint.
   * @return The name of the values constraint.
   */
  String getName();

  /**
   * Sets the name of the values constraint.
   * @param name The values constraint name.
   */
  void setName(String name);

  /**
   * Returns the query definition associated with this values query.
   * @return The query definition.
   */
  ValueQueryDefinition getQueryDefinition();

  /**
   * Set the query definition associated with this values query.
   * @param qdef The query definition.
   */
  void setQueryDefinition(ValueQueryDefinition qdef);

  /**
   * Returns the name of the options node used for this values query.
   * @return The name of the options node.
   */
  String getOptionsName();

  /**
   * Set the name of the options node to use for this values query.
   * @param optname The name of the options node.
   */
  void setOptionsName(String optname);

  /**
   * Returns the name of the aggregate function applied to this query.
   * @return The name of the function.
   */
  String[] getAggregate();

  /**
   * Sets the name of the aggregate function to be applied as part of this values query.
   * @param aggregate The name of the function.
   */
  void setAggregate(String... aggregate);

  /**
   * Returns the aggregate path.
   * @return The path.
   */
  String getAggregatePath();

  /**
   * Sets the aggregate path.
   * @param aggregate The aggregate path.
   */
  void setAggregatePath(String aggregate);

  /**
   * Returns the view for this values query.
   * @return The view.
   */
  String getView();

  /**
   * Sets the view for this values query.
   * @param view The view.
   */
  void setView(String view);

  /**
   * Returns the direction of the results in this values query.
   * @return The direction.
   */
  Direction getDirection();

  /**
   * Sets the direction of the results to use in this values query.
   * @param dir The direction.
   */
  void setDirection(Direction dir);

  /**
   * Returns the frequency of the results.
   * @return The frequency.
   */
  Frequency getFrequency();

  /**
   * Sets the frequency to be used in this values query.
   * @param freq The frequency.
   */
  void setFrequency(Frequency freq);
}


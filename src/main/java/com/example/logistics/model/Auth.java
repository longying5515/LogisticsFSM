package com.example.logistics.model;

import lombok.Data;

/**
 * Represents an authorization transition from one state to another.
 */
@Data
public class Auth {

  private String fromState;
  private String toState;

  /**
   * Default constructor.
   */
  public Auth() {
  }

  /**
   * Constructs an Auth instance with specified fromState and toState.
   *
   * @param fromState the state from which the transition starts
   * @param toState the state to which the transition goes
   */
  public Auth(String fromState, String toState) {
    this.fromState = fromState;
    this.toState = toState;
  }
}

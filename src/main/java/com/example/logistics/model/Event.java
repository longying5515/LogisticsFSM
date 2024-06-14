package com.example.logistics.model;

import lombok.Data;

/**
 * Represents an event that triggers a state transition.
 */
@Data
public class Event {

  private String name;
  private String fromState;
  private String toState;
  private String role;
}

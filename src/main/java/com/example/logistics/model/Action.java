package com.example.logistics.model;

import lombok.Data;

/**
 * Represents an action with an ID, action type, and role.
 */
@Data
public class Action {
  
  private int id;
  private String action;
  private String role;
}

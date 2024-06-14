package com.example.logistics.model;

import java.util.List;
import lombok.Data;

/**
 * Application model.
 */
@Data
public class Application {
  private String name;
  private String desc;
  private List<State> states;
  private String beginState;
  private String endState;
  private List<Role> roles;

  // Getters and setters
}

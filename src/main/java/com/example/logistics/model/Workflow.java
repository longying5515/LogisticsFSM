package com.example.logistics.model;

import java.util.List;
import lombok.Data;

/**
 * Workflow model.
 */
@Data
public class Workflow {
  private int appId;
  private int id;
  private String name;
  private String desc;
  private List<State> states;
  private List<Event> events;
}

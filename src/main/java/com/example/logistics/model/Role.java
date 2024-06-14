package com.example.logistics.model;

import java.util.List;
import lombok.Data;

/**
 * Role model.
 */
@Data
public class Role {
  private String role;
  private List<Auth> auth;
}

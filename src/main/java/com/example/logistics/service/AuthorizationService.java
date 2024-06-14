package com.example.logistics.service;

import com.example.logistics.model.Action;
import com.example.logistics.model.Application;
import com.example.logistics.model.Auth;
import com.example.logistics.model.Event;
import com.example.logistics.model.Role;
import com.example.logistics.model.Workflow;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service for handling authorization.
 */
@Service
public class AuthorizationService {

  /**
   * Authorizes an action based on the application, workflow, action, and role.
   *
   * @param app the application
   * @param workflow the workflow
   * @param action the action to authorize
   * @param role the role to authorize
   * @return true if the action is authorized, false otherwise
   */
  public boolean authorize(Application app, Workflow workflow, String action, String role) {

    if (action.equals(role)) {
      return true;
    }

    List<Auth> roleAuths = null;

    // Find the authorization list for the role
    for (Role appRole : app.getRoles()) {
      if (appRole.getRole().equals(role)) {
        roleAuths = appRole.getAuth();
        break; // Exit loop after finding the role
      }
    }

    // Return false if the role is not found
    if (roleAuths == null) {
      return false;
    }

    // Find events that match the action and role
    List<Event> matchingEvents = new ArrayList<>();
    for (Event event : workflow.getEvents()) {
      if (event.getName().equals(action) && event.getRole().equals(role)) {
        matchingEvents.add(event);
      }
    }

    // Return false if no matching events are found
    if (matchingEvents.isEmpty()) {
      return false;
    }

    // Check if any event matches the role's authorizations
    for (Event event : matchingEvents) {
      for (Auth auth : roleAuths) {
        if (event.getFromState().equals(auth.getFromState())
            && event.getToState().equals(auth.getToState())) {
          return true;
        }
      }
    }
    
    // Return false if no matching authorizations are found
    return false;
  }
}

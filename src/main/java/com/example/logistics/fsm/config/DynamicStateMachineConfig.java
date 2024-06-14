package com.example.logistics.fsm.config;

import com.example.logistics.model.Application;
import com.example.logistics.model.Event;
import com.example.logistics.model.Workflow;
import com.example.logistics.reposity.ApplicationDatabase;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;



/**
 * Dynamically configures state machines.
 */
@Configuration
public class DynamicStateMachineConfig {

  /**
   * Builds a state machine based on the provided workflow.
   *
   * @param workflow the workflow to build the state machine from
   * @return the configured state machine
   * @throws Exception if an error occurs during state machine configuration
   */
  public StateMachine<String, String> buildStateMachine(Workflow workflow) throws Exception {
    StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();
    Application app = ApplicationDatabase.applications.get(
            String.valueOf(workflow.getAppId()));
    Set<String> states = new HashSet<>();
    List<Event> events = workflow.getEvents();

    for (Event event : events) {
      states.add(event.getFromState());
      states.add(event.getToState());
    }

    String beginState = app.getBeginState();
    String endState = app.getEndState();

    StateMachineStateConfigurer<String, String> stateConfigurer = 
        builder.configureStates();
    stateConfigurer
        .withStates()
        .initial(beginState)
        .states(states)
        .end(endState);

    StateMachineTransitionConfigurer<String, String> transitionConfigurer = 
        builder.configureTransitions();
    for (Event event : events) {
      String fromState = event.getFromState();
      String toState = event.getToState();
      String eventName = event.getName();
      transitionConfigurer
          .withExternal()
          .source(fromState)
          .target(toState)
          .event(eventName);
    }

    return builder.build();
  }
}

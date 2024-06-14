package com.example.logistics.reposity;

import com.example.logistics.model.Workflow;
import com.example.logistics.result.Content;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.statemachine.StateMachine;

/**
 * WorkflowDatabase handles the storage and management of workflows and state machines.
 */
public class WorkflowDatabase {
  private static final Map<String, StateMachine<?, ?>> stateMachines = new ConcurrentHashMap<>();
  private static final List<Content> contents = new CopyOnWriteArrayList<>();
  private static final Map<Integer, Integer> workflowIdMap = new ConcurrentHashMap<>();
  private static final Map<String, Workflow> workflows = new ConcurrentHashMap<>();
  private static final Map<Integer, Workflow> workflowsMap = new ConcurrentHashMap<>();

  /**
   * Adds or updates a state machine.
   *
   * @param workflowId   the ID of the workflow
   * @param stateMachine the state machine to be added or updated
   */
  public static void addOrUpdateStateMachine(String workflowId, StateMachine<?, ?> stateMachine) {
    stateMachines.put(workflowId, stateMachine);
  }

  /**
   * Gets a state machine by workflow ID.
   *
   * @param workflowId the ID of the workflow
   * @return the state machine associated with the given workflow ID
   */
  public static StateMachine<?, ?> getStateMachine(String workflowId) {
    return stateMachines.get(workflowId);
  }

  /**
   * Deletes a state machine by workflow ID.
   *
   * @param workflowId the ID of the workflow
   */
  public static void deleteStateMachine(String workflowId) {
    stateMachines.remove(workflowId);
  }

  /**
   * Adds content.
   *
   * @param content the content to be added
   */
  public static void addContent(Content content) {
    contents.add(content);
  }

  /**
   * Gets all contents.
   *
   * @return the list of contents
   */
  public static List<Content> getContents() {
    return contents;
  }

  /**
   * Deletes content by ID.
   *
   * @param id the ID of the content to be deleted
   */
  public static void deleteContent(int id) {
    contents.removeIf(content -> content.getId() == id);
  }

  /**
   * Updates content.
   *
   * @param content the content to be updated
   */
  public static void updateContent(Content content) {
    synchronized (contents) {
      contents.removeIf(c -> c.getId() == content.getId());
      contents.add(content);
    }
  }

  /**
   * Adds a mapping between workflow ID and app ID.
   *
   * @param workflowId the ID of the workflow
   * @param appId      the ID of the app
   */
  public static void addWorkflowId(int workflowId, int appId) {
    workflowIdMap.put(workflowId, appId);
  }

  /**
   * Gets the app ID by workflow ID.
   *
   * @param workflowId the ID of the workflow
   * @return the app ID associated with the given workflow ID
   */
  public static Integer getAppId(int workflowId) {
    return workflowIdMap.get(workflowId);
  }

  /**
   * Deletes a mapping between workflow ID and app ID.
   *
   * @param workflowId the ID of the workflow
   */
  public static void deleteWorkflowId(int workflowId) {
    workflowIdMap.remove(workflowId);
  }

  /**
   * Adds or updates a workflow.
   *
   * @param workflow the workflow to be added or updated
   */
  public static void addOrUpdateWorkflow(Workflow workflow) {
    workflows.put(workflow.getName(), workflow);
  }

  /**
   * Gets a workflow by name.
   *
   * @param name the name of the workflow
   * @return the workflow associated with the given name
   */
  public static Workflow getWorkflow(String name) {
    return workflows.get(name);
  }

  /**
   * Deletes a workflow by name.
   *
   * @param name the name of the workflow
   */
  public static void deleteWorkflow(String name) {
    workflows.remove(name);
  }

  /**
   * Checks if a workflow is in progress.
   *
   * @param id the ID of the workflow
   * @return true if the workflow is in progress, false otherwise
   */
  public static boolean isWorkflowInProgress(int id) {
    StateMachine<?, ?> stateMachine = getStateMachine(String.valueOf(id));
    return stateMachine != null 
        && !stateMachine.getState().getId().equals(stateMachine.getInitialState().getId());
  }

  /**
   * Adds or updates a workflow by ID.
   *
   * @param workflow the workflow to be added or updated
   */
  public static void addOrUpdateWorkflowById(Workflow workflow) {
    workflowsMap.put(workflow.getId(), workflow);
  }

  /**
   * Gets a workflow by ID.
   *
   * @param id the ID of the workflow
   * @return the workflow associated with the given ID
   */
  public static Workflow getWorkflowById(int id) {
    return workflowsMap.get(id);
  }

  /**
   * Deletes a workflow by ID.
   *
   * @param id the ID of the workflow
   */
  public static void deleteWorkflowById(int id) {
    workflowsMap.remove(id);
  }
}

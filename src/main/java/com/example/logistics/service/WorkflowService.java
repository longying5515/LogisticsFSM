package com.example.logistics.service;

import com.example.logistics.fsm.config.DynamicStateMachineConfig;
import com.example.logistics.model.Application;
import com.example.logistics.model.Auth;
import com.example.logistics.model.Event;
import com.example.logistics.model.Role;
import com.example.logistics.model.State;
import com.example.logistics.model.Workflow;
import com.example.logistics.reposity.ApplicationDatabase;
import com.example.logistics.reposity.WorkflowDatabase;
import com.example.logistics.result.ApiResponse;
import com.example.logistics.result.Content;
import com.example.logistics.result.PagedApiResponse;
import com.example.logistics.result.PagedData;
import com.example.logistics.result.ReturnCode;
import com.example.logistics.result.SingleData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;


/**
 * Service for managing workflows.
 */
@Service
public class WorkflowService {

  @Autowired
  private DynamicStateMachineConfig dynamicStateMachineConfig;

  @Autowired
  private AuthorizationService authorizationService;

  // Atomic integer for recording workflow IDs
  private static final AtomicInteger workflowId = new AtomicInteger(1);

  // Atomic integer for application IDs
  private static final AtomicInteger applicationId = new AtomicInteger(1);

  /**
   * Creates a new application.
   *
   * @param app the application to create
   * @return the API response
   * @throws Exception if an error occurs during creation
   */
  public ApiResponse createApp(Application app) throws Exception {
    ApiResponse apiResponse = new ApiResponse();
    ArrayList<String> stateCodes = new ArrayList<>();
    for (State state : app.getStates()) {
      stateCodes.add(state.getCode());
    }
    if (app.getStates().size() < 3) {
      apiResponse.setState(ReturnCode.STATE_COUNT_LESS_THAN_THREE);
      apiResponse.setData(new SingleData(app.getName().hashCode()));
      return apiResponse;
    }
    if (!stateCodes.contains(app.getBeginState()) || !stateCodes.contains(app.getEndState())) {
      apiResponse.setState(ReturnCode.START_OR_END_STATE_NOT_IN_LIST);
      apiResponse.setData(new SingleData(app.getName().hashCode()));
      return apiResponse;
    }
    // Check if state transitions in role authorizations are valid
    for (Role role : app.getRoles()) {
      Map<String, String> statePairs = new HashMap<>();
      for (Auth auth : role.getAuth()) {
        String fromState = auth.getFromState();
        String toState = auth.getToState();

        // Check for self-loops
        if (fromState.equals(toState)) {
          apiResponse.setState(ReturnCode.ILLEGAL_STATE_TRANSITION);
          apiResponse.setData(new SingleData(app.getName().hashCode()));
          return apiResponse;
        }

        // Check for reverse transitions
        if (statePairs.containsKey(toState) && statePairs.get(toState).equals(fromState)) {
          apiResponse.setState(ReturnCode.ILLEGAL_STATE_TRANSITION);
          apiResponse.setData(new SingleData(app.getName().hashCode()));
          return apiResponse;
        }

        // Check for transitions back to the initial state
        if (toState.equals(app.getBeginState())) {
          apiResponse.setState(ReturnCode.ILLEGAL_STATE_TRANSITION);
          apiResponse.setData(new SingleData(app.getName().hashCode()));
          return apiResponse;
        }

        statePairs.put(fromState, toState);
      }
    }
    int id=workflowId.getAndIncrement();
    if (ApplicationDatabase.applications.get(
        String.valueOf(app.getName().hashCode() & 0x7FFFFFFF)) != null) {
      apiResponse.setState(ReturnCode.DUPLICATE_WORKFLOW_NAME);
      apiResponse.setData(new SingleData(app.getName().hashCode()));
    } else {
      ApplicationDatabase.applications.put(
          String.valueOf(app.getName().hashCode() & 0x7FFFFFFF), app);
      apiResponse.setState(ReturnCode.SUCCESS);
      apiResponse.setData(new SingleData(app.getName().hashCode() & 0x7FFFFFFF));
    }
    return apiResponse;
  }

  /**
   * Creates a new workflow.
   *
   * @param workflow the workflow to create
   * @return the API response
   * @throws Exception if an error occurs during creation
   */
  public ApiResponse createWorkflow(Workflow workflow) throws Exception {
    Set<String> stateCodes = new HashSet<>();
    ApiResponse apiResponse = new ApiResponse();

    for (State state : workflow.getStates()) {
      stateCodes.add(state.getCode());
    }

    // Check if workflow name is empty
    if (workflow.getName() == null || workflow.getName().trim().isEmpty()) {
      apiResponse.setState(ReturnCode.INVALID_WORKFLOW_NAME);
      apiResponse.setData(new SingleData(null));
      return apiResponse;
    }

    // Check if workflow name is duplicate
    if (WorkflowDatabase.getWorkflow(workflow.getName()) != null) {
      apiResponse.setState(ReturnCode.DUPLICATE_WORKFLOW_NAME);
      apiResponse.setData(new SingleData(null));
      return apiResponse;
    }

    // Check if fromState and toState exist in state list
    for (Event event : workflow.getEvents()) {
      if (!stateCodes.contains(event.getFromState()) 
          || !stateCodes.contains(event.getToState())) {
        apiResponse.setState(ReturnCode.ILLEGAL_STATE_TRANSITION);
        apiResponse.setData(new SingleData(null));
        return apiResponse;
      }
    }

    Application application = ApplicationDatabase.applications.get(
        String.valueOf(workflow.getAppId()));
    List<Auth> auths = new ArrayList<>();

    for (Role role : application.getRoles()) {
      for (Auth auth : role.getAuth()) {
        if (stateCodes.contains(auth.getFromState()) && stateCodes.contains(auth.getToState())) {
          auths.add(auth);
        }
      }
    }

    Set<Auth> authSet = new HashSet<>(auths);

    // Check if each event exists in app
    for (Event event : workflow.getEvents()) {
      String fromState = event.getFromState();
      String toState = event.getToState();
      if (!authSet.contains(new Auth(fromState, toState))) {
        apiResponse.setState(ReturnCode.ILLEGAL_STATE_TRANSITION);
        apiResponse.setData(new SingleData(null));
        return apiResponse;
      }
    }

    if (hasCycle(workflow)) {
      apiResponse.setState(ReturnCode.ILLEGAL_STATE_TRANSITION);
      apiResponse.setData(new SingleData(null));
      return apiResponse;
    }
    StateMachine<String, String> stateMachine = dynamicStateMachineConfig
        .buildStateMachine(workflow);
    stateMachine.start();
    int id = workflowId.getAndIncrement();
    workflow.setId(id);
    WorkflowDatabase.addOrUpdateStateMachine(Integer.toString(id), stateMachine);
    WorkflowDatabase.addWorkflowId(id, workflow.getAppId());
    WorkflowDatabase.addContent(
        new Content(id, workflow.getName(), workflow.getDesc(), "张三", LocalDateTime.now()));
    WorkflowDatabase.addOrUpdateWorkflow(workflow);
    WorkflowDatabase.addOrUpdateWorkflowById(workflow);
    apiResponse.setState(ReturnCode.SUCCESS);
    apiResponse.setData(new SingleData(id));
    return apiResponse;
  }

  private boolean hasCycle(Workflow workflow) {
    // Build graph
    Map<String, List<String>> graph = new HashMap<>();
    for (Event event : workflow.getEvents()) {
      graph.computeIfAbsent(event.getFromState(), k -> new ArrayList<>()).add(event.getToState());
    }

    // Check for cycles using DFS
    Set<String> visited = new HashSet<>();
    Set<String> stack = new HashSet<>();
    for (String state : graph.keySet()) {
      if (hasCycleDfs(state, graph, visited, stack)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasCycleDfs(
      String state, Map<String, List<String>> graph, Set<String> visited, Set<String> stack) {
    if (stack.contains(state)) {
      return true; // Found cycle
    }
    if (visited.contains(state)) {
      return false;
    }
    visited.add(state);
    stack.add(state);
    List<String> neighbors = graph.get(state);
    if (neighbors != null) {
      for (String neighbor : neighbors) {
        if (hasCycleDfs(neighbor, graph, visited, stack)) {
          return true;
        }
      }
    }
    stack.remove(state);
    return false;
  }

  /**
   * Queries workflows with pagination.
   *
   * @param page the page number
   * @param pageSize the page size
   * @return the paginated API response
   */
  public PagedApiResponse queryWorkflow(int page, int pageSize) {
    if (pageSize == 0) {
      page = 1;
      pageSize = 20;
    }
    if (page <= 0) {
      page = 1;
    }
    List<Content> contents = WorkflowDatabase.getContents();

    // Sort by creation time in descending order
    contents.sort(Comparator.comparing(Content::getCreatedTime).reversed());

    // Return successful response with null data if content is empty
    if (contents.isEmpty()) {
      PagedApiResponse pagedApiResponse = new PagedApiResponse();
      pagedApiResponse.setState(ReturnCode.SUCCESS);
      pagedApiResponse.setData(new PagedData(page, pageSize, 0, 0, new ArrayList<>()));
      return pagedApiResponse;
    }

    int fromIndex = (page - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, contents.size());

    if (fromIndex >= contents.size()) {
      page = (contents.size() + pageSize - 1) / pageSize;
      fromIndex = (page - 1) * pageSize;
      toIndex = contents.size();
    }

    int totalPage = (contents.size() + pageSize - 1) / pageSize;

    PagedApiResponse pagedApiResponse = new PagedApiResponse();
    pagedApiResponse.setState(ReturnCode.SUCCESS);
    PagedData pagedData = new PagedData(
        page, pageSize, totalPage, contents.size(), contents.subList(fromIndex, toIndex));
    pagedApiResponse.setData(pagedData);
    return pagedApiResponse;
  }

  /**
   * Updates a workflow.
   *
   * @param workflow the workflow to update
   * @return the API response
   * @throws Exception if an error occurs during update
   */
  public ApiResponse updateWorkflow(Workflow workflow) throws Exception {
    ApiResponse apiResponse = new ApiResponse();
    Workflow existingWorkflow = WorkflowDatabase.getWorkflowById(workflow.getId());

    // Check if workflow exists
    if (existingWorkflow == null) {
      apiResponse.setState(ReturnCode.FLOW_ID_NOT_EXIST);
      apiResponse.setData(null);
      return apiResponse;
    }

    // Check if input workflow ID matches existing workflow ID
    if (workflow.getId() != existingWorkflow.getId()) {
      apiResponse.setState(ReturnCode.INVALID_INPUT_PARAMETER);
      apiResponse.setData(null);
      return apiResponse;
    }

    // Check if workflow is in progress
    if (WorkflowDatabase.isWorkflowInProgress(workflow.getId())) {
      apiResponse.setState(ReturnCode.FLOW_IN_PROGRESS);
      apiResponse.setData(new SingleData(workflow.getId()));
      return apiResponse;
    }

    // Check if updated workflow name is duplicate
    if (!workflow.getName().equals(existingWorkflow.getName()) 
        && WorkflowDatabase.getWorkflow(workflow.getName()) != null) {
      apiResponse.setState(ReturnCode.DUPLICATE_WORKFLOW_NAME);
      apiResponse.setData(new SingleData(workflow.getId()));
      return apiResponse;
    }

    int id = workflow.getId();
    int appId = WorkflowDatabase.getAppId(id);
    workflow.setAppId(appId);
    StateMachine<String, String> stateMachine = dynamicStateMachineConfig
        .buildStateMachine(workflow);
    stateMachine.start();
    WorkflowDatabase.addOrUpdateStateMachine(Integer.toString(workflow.getId()), stateMachine);

    apiResponse.setState(ReturnCode.SUCCESS);
    apiResponse.setData(new SingleData(workflow.getId()));
    return apiResponse;
  }

  /**
   * Deletes a workflow.
   *
   * @param id the ID of the workflow to delete
   * @return the API response
   */
  public ApiResponse deleteWorkflow(int id) {
    // Check if workflow exists
    Integer appId = WorkflowDatabase.getAppId(id);

    if (appId == null) {
      ApiResponse apiResponse = new ApiResponse();
      apiResponse.setState(ReturnCode.FLOW_ID_NOT_EXIST);
      apiResponse.setData(new SingleData(id));
      return apiResponse;
    }

    // Check if workflow is in progress
    if (WorkflowDatabase.isWorkflowInProgress(id)) {
      ApiResponse apiResponse = new ApiResponse();
      apiResponse.setState(ReturnCode.FLOW_IN_PROGRESS);
      apiResponse.setData(new SingleData(id));
      return apiResponse;
    }

    Workflow workflow = WorkflowDatabase.getWorkflowById(id);

    // Delete workflow content
    WorkflowDatabase.deleteContent(id);

    // Delete mapping
    WorkflowDatabase.deleteWorkflowId(id);

    // Delete state machine
    WorkflowDatabase.deleteStateMachine(Integer.toString(id));

    // Delete workflow by name
    WorkflowDatabase.deleteWorkflow(workflow.getName());

    // Delete workflow by ID
    WorkflowDatabase.deleteWorkflowById(id);

    // Build response
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setState(ReturnCode.SUCCESS);
    apiResponse.setData(new SingleData(id));
    return apiResponse;
  }

  /**
   * Performs an action on a workflow.
   *
   * @param id the ID of the workflow
   * @param action the action to perform
   * @param role the role performing the action
   * @return the API response
   */
  public ApiResponse workflowAction(int id, String action, String role) {
    StateMachine<String, String> stateMachine = 
        (StateMachine<String, String>) WorkflowDatabase.getStateMachine(Integer.toString(id));
    ApiResponse apiResponse = new ApiResponse();
    Workflow workflow = WorkflowDatabase.getWorkflowById(id);
    Application app = ApplicationDatabase.applications.get(String.valueOf(workflow.getAppId()));

    if (!authorizationService.authorize(app, workflow, action, role)) {
      apiResponse.setState(ReturnCode.USER_ROLE_PERMISSION_INVALID);
      apiResponse.setData(new SingleData(id));
      return apiResponse;
    }
    if (stateMachine == null) {
      apiResponse.setState(ReturnCode.FLOW_ID_NOT_EXIST);
      apiResponse.setData(new SingleData(id));
      return apiResponse;
    }

    boolean eventAccepted = stateMachine.sendEvent(action);

    if (eventAccepted) {
      stateMachine.sendEvent("AUTO");
      apiResponse.setState(ReturnCode.SUCCESS);
    } else {
      apiResponse.setState(ReturnCode.ILLEGAL_STATE_TRANSITION);
    }

    apiResponse.setData(new SingleData(id));
    return apiResponse;
  }
}

package com.example.logistics.controller;

import com.example.logistics.model.Action;
import com.example.logistics.model.Application;
import com.example.logistics.model.DeleteId;
import com.example.logistics.model.PageQuery;
import com.example.logistics.model.Workflow;
import com.example.logistics.result.ApiResponse;
import com.example.logistics.result.PagedApiResponse;
import com.example.logistics.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for handling workflow-related requests.
 */
@RestController
@RequestMapping("/api")
public class WorkflowController {

  private final WorkflowService workflowService;

  @Autowired
  public WorkflowController(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  /**
   * Creates a new application.
   *
   * @param app the application to create
   * @return the API response
   * @throws Exception if an error occurs during creation
   */
  @PostMapping("/app/create")
  public ApiResponse createApp(@RequestBody Application app) throws Exception {
    return workflowService.createApp(app);
  }

  /**
   * Creates a new workflow.
   *
   * @param workflow the workflow to create
   * @return the API response
   * @throws Exception if an error occurs during creation
   */
  @PostMapping("/workflow/create")
  public ApiResponse createWorkflow(@RequestBody Workflow workflow) throws Exception {
    return workflowService.createWorkflow(workflow);
  }

  /**
   * Queries workflows with pagination.
   *
   * @param pageQuery the page query parameters
   * @return the paged API response
   */
  @PostMapping("/workflow/query")
  public PagedApiResponse queryWorkflow(@RequestBody PageQuery pageQuery) {
    int page = pageQuery.getPage();
    int pageSize = pageQuery.getPageSize();
    return workflowService.queryWorkflow(page, pageSize);
  }

  /**
   * Updates an existing workflow.
   *
   * @param workflow the workflow to update
   * @return the API response
   * @throws Exception if an error occurs during update
   */
  @PostMapping("/workflow/update")
  public ApiResponse updateWorkflow(@RequestBody Workflow workflow) throws Exception {
    return workflowService.updateWorkflow(workflow);
  }

  /**
   * Deletes a workflow.
   *
   * @param id the ID of the workflow to delete
   * @return the API response
   */
  @PostMapping("/workflow/delete")
  public ApiResponse deleteWorkflow(@RequestBody DeleteId id) {
    return workflowService.deleteWorkflow(id.getId());
  }

  /**
   * Performs an action on a workflow.
   *
   * @param action the action to perform
   * @return the API response
   */
  @PostMapping("/workflow/action")
  public ApiResponse workflowAction(@RequestBody Action action) {
    int id = action.getId();
    String role = action.getRole();
    String actionName = action.getAction();
    return workflowService.workflowAction(id, actionName, role);
  }
}

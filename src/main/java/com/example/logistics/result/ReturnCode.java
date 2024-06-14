package com.example.logistics.result;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing return codes and their messages.
 */
public enum ReturnCode {
  SUCCESS(20000, "操作成功"),
  START_OR_END_STATE_INVALID(50010, "开始状态或结束状态不合理"),
  STATE_COUNT_LESS_THAN_THREE(50011, "状态总数不能少于3个"),
  START_OR_END_STATE_NOT_IN_LIST(50012, "开始状态或结束状态不在状态列表中"),
  USER_ROLE_PERMISSION_INVALID(50013, "用户角色权限不合理"),
  STATE_TRANSITION_INVALID(50020, "状态转换不合理"),
  MISSING_REQUIRED_STATE(50021, "缺少必要状态"),
  ILLEGAL_STATE_NAME(50022, "非法的状态名"),
  CIRCULAR_DEPENDENCY_IN_STATE_TRANSITIONS(50023, "存在循环依赖的状态流转关系"),
  NO_CONDITION_FOR_STATE_TRANSITIONS(50024, "存在没有触发条件的状态关系"),
  APP_ID_NOT_EXIST(50031, "appId 不存在"),
  FLOW_ID_NOT_EXIST(50032, "流程 ID 不存在"),
  FLOW_IN_PROGRESS(50033, "流程正在进行中"),
  ILLEGAL_STATE_TRANSITION(50040, "非法的状态流转"),
  INVALID_INPUT_PARAMETER(50050, "输入参数错误"),
  SYSTEM_ERROR(50502, "系统异常，请稍后重试"),
  INVALID_WORKFLOW_NAME(50060, "工作流名称无效"),
  DUPLICATE_WORKFLOW_NAME(50061, "工作流名称重复");

  private final int code;
  private final String message;

  ReturnCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @JsonValue
  public StateCodeJson toJson() {
    return new StateCodeJson(code, message);
  }

  /**
   * Inner class representing the JSON structure for a return code.
   */
  public static class StateCodeJson {
    private final int code;
    private final String msg;

    public StateCodeJson(int code, String msg) {
      this.code = code;
      this.msg = msg;
    }

    public int getCode() {
      return code;
    }

    public String getMsg() {
      return msg;
    }
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}

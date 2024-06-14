package com.example.logistics.result;

import lombok.Data;

/**
 * Encapsulates the API response result.
 */
@Data
public class ApiResponse {

  private ReturnCode state;
  private SingleData data;

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object
   */
  @Override
  public String toString() {
    return "ApiResponse{"
        + "state=" + state
        + ", data=" + data
        + '}';
  }
}

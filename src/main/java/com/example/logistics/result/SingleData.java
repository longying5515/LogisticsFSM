package com.example.logistics.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Represents a single data entity.
 */
@Data
public class SingleData {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer id;

  /**
   * Constructs a new SingleData instance with the specified id.
   *
   * @param id the identifier of the data
   */
  public SingleData(Integer id) {
    this.id = id;
  }
}

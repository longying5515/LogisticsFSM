package com.example.logistics.result;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents a return state with a code and message.
 */
@Data
@RequiredArgsConstructor
public class ReturnState {

  private final int code;
  private final String msg;
}

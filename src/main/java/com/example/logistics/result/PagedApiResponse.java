package com.example.logistics.result;

import lombok.Data;

/**
 * Represents the API response for a paginated query.
 */
@Data
public class PagedApiResponse {

  private ReturnCode state;
  private PagedData data;
}

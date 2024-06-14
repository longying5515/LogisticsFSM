package com.example.logistics.model;

import lombok.Data;

/**
 * Represents a page query with page number and page size.
 */
@Data
public class PageQuery {

  private int page;
  private int pageSize;
}

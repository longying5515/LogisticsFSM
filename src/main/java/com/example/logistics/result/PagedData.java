package com.example.logistics.result;

import java.util.List;
import lombok.Data;

/**
 * Represents paginated data.
 */
@Data
public class PagedData {

  private int page;
  private int size;
  private int totalPage;
  private int total;
  private List<Content> content;

  /**
   * Constructs a PagedData instance with the specified parameters.
   *
   * @param page the current page number
   * @param size the number of items per page
   * @param totalPage the total number of pages
   * @param total the total number of items
   * @param content the list of content items
   */
  public PagedData(int page, int size, int totalPage, int total, List<Content> content) {
    this.page = page;
    this.size = size;
    this.totalPage = totalPage;
    this.total = total;
    this.content = content;
  }
}

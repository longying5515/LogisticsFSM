package com.example.logistics.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;


/**
 * Represents content for paginated storage.
 */
@Data
@RequiredArgsConstructor
public class Content {

  private int id;
  private String name;
  private String desc;
  private String creator;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdTime;
  
  /**
   * Constructs a new Content instance with the specified parameters.
   *
   * @param id the identifier of the content
   * @param name the name of the content
   * @param desc the description of the content
   * @param creator the creator of the content
   * @param createdTime the time the content was created
   */
  public Content(int id, String name, String desc, String creator, LocalDateTime createdTime) {
    this.id = id;
    this.name = name;
    this.desc = desc;
    this.creator = creator;
    this.createdTime = createdTime;
  }
}

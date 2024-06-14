package com.example.logistics.reposity;

import com.example.logistics.model.Application;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the application database.
 */
public class ApplicationDatabase {

  public static final Map<String, Application> applications = new ConcurrentHashMap<>();
}

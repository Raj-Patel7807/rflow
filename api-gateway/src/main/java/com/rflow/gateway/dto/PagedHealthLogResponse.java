package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Paginated Response payload containing Health Logs.
 */
@Data
@Builder
public class PagedHealthLogResponse {

    private List<HealthLogResponse> logs;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}

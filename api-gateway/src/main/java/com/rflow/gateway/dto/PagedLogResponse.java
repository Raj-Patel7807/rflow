package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Paginated Response payload containing request logs.
 */
@Data
@Builder
public class PagedLogResponse {

    private List<RequestLogResponse> logs;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}

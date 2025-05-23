package com.taxiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling driver rating requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    private double rating;
}

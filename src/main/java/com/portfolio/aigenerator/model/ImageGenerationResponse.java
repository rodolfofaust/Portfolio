package com.portfolio.aigenerator.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageGenerationResponse {
    
    private String id;
    private String prompt;
    private String negativePrompt;
    private String size;
    private String style;
    private Integer quality;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private String status;
    private String errorMessage;
} 
package com.portfolio.aigenerator.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ImageGenerationRequest {
    
    @NotBlank(message = "A descrição da imagem é obrigatória")
    @Size(min = 10, max = 1000, message = "A descrição deve ter entre 10 e 1000 caracteres")
    private String prompt;
    
    @Size(max = 1000, message = "A descrição negativa deve ter no máximo 1000 caracteres")
    private String negativePrompt;
    
    private String size = "1024x1024";
    private String style = "vivid";
    private Integer quality = 1;
    private Integer n = 1;
} 
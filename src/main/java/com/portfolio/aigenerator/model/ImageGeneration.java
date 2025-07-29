package com.portfolio.aigenerator.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "image_generations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageGeneration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 1000)
    private String prompt;
    
    @Column(length = 1000)
    private String negativePrompt;
    
    @Column(nullable = false)
    private String size;
    
    @Column(nullable = false)
    private String style;
    
    @Column(nullable = false)
    private Integer quality;
    
    @Column(nullable = false)
    private Integer n;
    
    @ElementCollection
    @CollectionTable(name = "generated_images", joinColumns = @JoinColumn(name = "generation_id"))
    @Column(name = "image_url", length = 1000)
    private List<String> imageUrls;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private String status;
    
    @Column(length = 1000)
    private String errorMessage;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 
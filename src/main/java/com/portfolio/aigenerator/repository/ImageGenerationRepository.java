package com.portfolio.aigenerator.repository;

import com.portfolio.aigenerator.model.ImageGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageGenerationRepository extends JpaRepository<ImageGeneration, Long> {
    
    List<ImageGeneration> findAllByOrderByCreatedAtDesc();
    
    List<ImageGeneration> findByStatusOrderByCreatedAtDesc(String status);
} 
package com.portfolio.aigenerator.controller;

import com.portfolio.aigenerator.model.ImageGenerationRequest;
import com.portfolio.aigenerator.model.ImageGenerationResponse;
import com.portfolio.aigenerator.service.ImageGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Geração de Imagens", description = "API para geração de imagens com IA")
@CrossOrigin(origins = "*")
public class ImageGenerationController {

    private final ImageGenerationService imageGenerationService;

    @PostMapping("/generate")
    @Operation(summary = "Gerar imagem", description = "Gera uma imagem baseada no prompt fornecido")
    public ResponseEntity<ImageGenerationResponse> generateImage(@Valid @RequestBody ImageGenerationRequest request) {
        log.info("Recebida requisição para gerar imagem com prompt: {}", request.getPrompt());
        
        ImageGenerationResponse response = imageGenerationService.generateImage(request);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar gerações", description = "Lista todas as gerações de imagens")
    public ResponseEntity<List<ImageGenerationResponse>> getAllGenerations() {
        log.info("Recebida requisição para listar todas as gerações");
        
        List<ImageGenerationResponse> generations = imageGenerationService.getAllGenerations();
        
        return ResponseEntity.ok(generations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar geração por ID", description = "Busca uma geração específica pelo ID")
    public ResponseEntity<ImageGenerationResponse> getGenerationById(@PathVariable Long id) {
        log.info("Recebida requisição para buscar geração com ID: {}", id);
        
        ImageGenerationResponse generation = imageGenerationService.getGenerationById(id);
        
        return ResponseEntity.ok(generation);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica se a API está funcionando")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("API de Geração de Imagens funcionando!");
    }
} 
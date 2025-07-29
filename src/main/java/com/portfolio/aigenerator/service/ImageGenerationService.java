package com.portfolio.aigenerator.service;

import com.portfolio.aigenerator.model.ImageGeneration;
import com.portfolio.aigenerator.model.ImageGenerationRequest;
import com.portfolio.aigenerator.model.ImageGenerationResponse;
import com.portfolio.aigenerator.repository.ImageGenerationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageGenerationService {

    private final OpenAIService openAIService;
    private final ImageGenerationRepository repository;

    public ImageGenerationResponse generateImage(ImageGenerationRequest request) {
        try {
            log.info("Iniciando geração de imagem com prompt: {}", request.getPrompt());
            
            // Gerar imagens usando OpenAI
            List<String> imageUrls = openAIService.generateImages(
                request.getPrompt(),
                request.getNegativePrompt(),
                request.getSize(),
                request.getStyle(),
                request.getQuality(),
                request.getN()
            );

            // Salvar no banco de dados
            ImageGeneration generation = ImageGeneration.builder()
                    .prompt(request.getPrompt())
                    .negativePrompt(request.getNegativePrompt())
                    .size(request.getSize())
                    .style(request.getStyle())
                    .quality(request.getQuality())
                    .n(request.getN())
                    .imageUrls(imageUrls)
                    .status("COMPLETED")
                    .build();

            ImageGeneration savedGeneration = repository.save(generation);

            return ImageGenerationResponse.builder()
                    .id(savedGeneration.getId().toString())
                    .prompt(savedGeneration.getPrompt())
                    .negativePrompt(savedGeneration.getNegativePrompt())
                    .size(savedGeneration.getSize())
                    .style(savedGeneration.getStyle())
                    .quality(savedGeneration.getQuality())
                    .imageUrls(savedGeneration.getImageUrls())
                    .createdAt(savedGeneration.getCreatedAt())
                    .status(savedGeneration.getStatus())
                    .build();

        } catch (Exception e) {
            log.error("Erro ao gerar imagem: {}", e.getMessage(), e);
            
            // Salvar erro no banco
            ImageGeneration errorGeneration = ImageGeneration.builder()
                    .prompt(request.getPrompt())
                    .negativePrompt(request.getNegativePrompt())
                    .size(request.getSize())
                    .style(request.getStyle())
                    .quality(request.getQuality())
                    .n(request.getN())
                    .status("ERROR")
                    .errorMessage(e.getMessage())
                    .build();

            ImageGeneration savedError = repository.save(errorGeneration);

            return ImageGenerationResponse.builder()
                    .id(savedError.getId().toString())
                    .prompt(savedError.getPrompt())
                    .status("ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public List<ImageGenerationResponse> getAllGenerations() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ImageGenerationResponse getGenerationById(Long id) {
        ImageGeneration generation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Geração não encontrada com ID: " + id));
        
        return mapToResponse(generation);
    }

    private ImageGenerationResponse mapToResponse(ImageGeneration generation) {
        return ImageGenerationResponse.builder()
                .id(generation.getId().toString())
                .prompt(generation.getPrompt())
                .negativePrompt(generation.getNegativePrompt())
                .size(generation.getSize())
                .style(generation.getStyle())
                .quality(generation.getQuality())
                .imageUrls(generation.getImageUrls())
                .createdAt(generation.getCreatedAt())
                .status(generation.getStatus())
                .errorMessage(generation.getErrorMessage())
                .build();
    }
} 
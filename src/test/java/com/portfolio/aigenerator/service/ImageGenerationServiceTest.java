package com.portfolio.aigenerator.service;

import com.portfolio.aigenerator.model.ImageGeneration;
import com.portfolio.aigenerator.model.ImageGenerationRequest;
import com.portfolio.aigenerator.model.ImageGenerationResponse;
import com.portfolio.aigenerator.repository.ImageGenerationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageGenerationServiceTest {

    @Mock
    private OpenAIService openAIService;

    @Mock
    private ImageGenerationRepository repository;

    @InjectMocks
    private ImageGenerationService imageGenerationService;

    private ImageGenerationRequest request;
    private ImageGeneration savedGeneration;

    @BeforeEach
    void setUp() {
        request = new ImageGenerationRequest();
        request.setPrompt("Um gato siamês em um jardim japonês");
        request.setSize("1024x1024");
        request.setStyle("vivid");
        request.setQuality(1);
        request.setN(1);

        savedGeneration = ImageGeneration.builder()
                .id(1L)
                .prompt(request.getPrompt())
                .size(request.getSize())
                .style(request.getStyle())
                .quality(request.getQuality())
                .n(request.getN())
                .imageUrls(Arrays.asList("https://example.com/image1.jpg"))
                .status("COMPLETED")
                .build();
    }

    @Test
    void generateImage_Success() {
        // Given
        when(openAIService.generateImages(
                request.getPrompt(),
                request.getNegativePrompt(),
                request.getSize(),
                request.getStyle(),
                request.getQuality(),
                request.getN()
        )).thenReturn(Arrays.asList("https://example.com/image1.jpg"));

        when(repository.save(any(ImageGeneration.class))).thenReturn(savedGeneration);

        // When
        ImageGenerationResponse response = imageGenerationService.generateImage(request);

        // Then
        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals(request.getPrompt(), response.getPrompt());
        assertEquals("COMPLETED", response.getStatus());
        assertNotNull(response.getImageUrls());
        assertEquals(1, response.getImageUrls().size());
    }

    @Test
    void generateImage_Error() {
        // Given
        when(openAIService.generateImages(
                request.getPrompt(),
                request.getNegativePrompt(),
                request.getSize(),
                request.getStyle(),
                request.getQuality(),
                request.getN()
        )).thenThrow(new RuntimeException("Erro na API da OpenAI"));

        when(repository.save(any(ImageGeneration.class))).thenReturn(savedGeneration);

        // When
        ImageGenerationResponse response = imageGenerationService.generateImage(request);

        // Then
        assertNotNull(response);
        assertEquals("ERROR", response.getStatus());
        assertNotNull(response.getErrorMessage());
    }
} 
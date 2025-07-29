package com.portfolio.aigenerator.service;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenAIService {

    private final OpenAiService openAiService;

    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
    }

    public List<String> generateImages(String prompt, String negativePrompt, String size, String style, Integer quality, Integer n) {
        try {
            CreateImageRequest request = CreateImageRequest.builder()
                    .prompt(prompt)
                    .size(size)
                    .quality(quality == 1 ? "standard" : "hd")
                    .style(style)
                    .n(n)
                    .build();

            List<Image> images = openAiService.createImage(request).getData();
            
            return images.stream()
                    .map(Image::getUrl)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao gerar imagem com OpenAI: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar imagem: " + e.getMessage());
        }
    }
} 
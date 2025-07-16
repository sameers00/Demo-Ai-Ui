package com.demoai.demoai_ui.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.demoai.demoai_ui.entity.Prompt;
import com.demoai.demoai_ui.repository.PromptRepository;

@WebMvcTest(PromptController.class)
public class PromptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromptRepository promptRepo;

    @Test
    public void testShowHomePage() throws Exception {
        Mockito.when(promptRepo.findAll()).thenReturn(Collections.singletonList(new Prompt()));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testAddPrompt() throws Exception {
        mockMvc.perform(post("/addPrompt")
                        .param("promptName", "Test Prompt")
                        .param("userPrompt", "What is in the image?"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
    }

    @Test
    public void testUpdatePrompt() throws Exception {
        Prompt prompt = new Prompt();
        prompt.setId(1L);
        prompt.setPromptName("Old");
        prompt.setUserPrompt("Old Prompt");

        Mockito.when(promptRepo.findById(1L)).thenReturn(Optional.of(prompt));

        mockMvc.perform(post("/updatePrompt")
                        .param("id", "1")
                        .param("userPrompt", "Updated Prompt"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("index"));
    }

    @Test
    public void testPredictImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("imageFile", "image.jpg", "image/jpeg", "dummy-image-data".getBytes());

        mockMvc.perform(multipart("/predictImage")
                        .file(file)
                        .param("userPrompt", "What is shown in the image?"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("predictionResult"));
    }
}

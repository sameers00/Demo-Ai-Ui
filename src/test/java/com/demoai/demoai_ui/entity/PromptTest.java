package com.demoai.demoai_ui.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PromptTest {

    @Test
    public void testPromptEntity() {
        Prompt prompt = new Prompt();
        prompt.setId(1L);
        prompt.setPromptName("Test");
        prompt.setUserPrompt("User Prompt");

        assertEquals(1L, prompt.getId());
        assertEquals("Test", prompt.getPromptName());
        assertEquals("User Prompt", prompt.getUserPrompt());
    }
}

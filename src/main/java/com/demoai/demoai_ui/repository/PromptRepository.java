package com.demoai.demoai_ui.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demoai.demoai_ui.entity.Prompt;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

}

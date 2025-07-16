package com.demoai.demoai_ui.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.demoai.demoai_ui.entity.Prompt;
import com.demoai.demoai_ui.repository.PromptRepository;
import com.demoai.demoai_ui.service.MultipartInputStreamFileResource;

@Controller
public class PromptController {

    @Autowired
    private PromptRepository promptRepo;

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Prompt> prompts = promptRepo.findAll();
        model.addAttribute("prompts", prompts);
        return "index";
    }

    @PostMapping("/predictImage")
    public String predictImage(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("userPrompt") String userPrompt,
            Model model) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new MultipartInputStreamFileResource(imageFile.getInputStream(), imageFile.getOriginalFilename()));
            body.add("question", userPrompt);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            String flaskUrl = "http://localhost:5000/predict";

            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, requestEntity, Map.class);
            String answer = response.getBody().get("answer").toString();

            model.addAttribute("predictionResult", answer);
        } catch (Exception e) {
            model.addAttribute("predictionResult", "Prediction failed: " + e.getMessage());
        }

        List<Prompt> prompts = promptRepo.findAll();
        model.addAttribute("prompts", prompts);
        return "index";
    }

    @PostMapping("/addPrompt")
    public String addPrompt(@RequestParam String promptName, @RequestParam String userPrompt, Model model) {
        Prompt prompt = new Prompt();
        prompt.setPromptName(promptName);
        prompt.setUserPrompt(userPrompt);
        promptRepo.save(prompt);

        List<Prompt> prompts = promptRepo.findAll();
        model.addAttribute("prompts", prompts);
        return "redirect:/";
    }

    @PostMapping("/updatePrompt")
    public String updatePrompt(@RequestParam Long id, @RequestParam String userPrompt, Model model) {
        Prompt prompt = promptRepo.findById(id).orElseThrow();
        prompt.setUserPrompt(userPrompt);
        promptRepo.save(prompt);

        List<Prompt> prompts = promptRepo.findAll();
        model.addAttribute("prompts", prompts);
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(Exception ex) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("message", ex.getMessage());
        return mv;
    }
}

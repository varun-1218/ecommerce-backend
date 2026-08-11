package com.varun.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.varun.ecommerce.dto.FAQDTO;
import com.varun.ecommerce.service.FAQService;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin(origins = "*")
public class FAQController {
    
    @Autowired
    private FAQService faqService;
    
    @PostMapping
    public ResponseEntity<FAQDTO> createFAQ(
            @RequestBody FAQDTO faqDTO,
            @RequestParam Long userId) {
        try {
            FAQDTO createdFAQ = faqService.createFAQ(faqDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFAQ);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FAQDTO> updateFAQ(@PathVariable Long id, @RequestBody FAQDTO faqDTO) {
        try {
            FAQDTO updatedFAQ = faqService.updateFAQ(id, faqDTO);
            return ResponseEntity.ok(updatedFAQ);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FAQDTO> getFAQById(@PathVariable Long id) {
        try {
            FAQDTO faqDTO = faqService.getFAQById(id);
            return ResponseEntity.ok(faqDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<FAQDTO>> getFAQsByProduct(@PathVariable Long productId) {
        try {
            List<FAQDTO> faqs = faqService.getFAQsByProductId(productId);
            return ResponseEntity.ok(faqs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<FAQDTO>> getAllFAQs() {
        try {
            List<FAQDTO> faqs = faqService.getAllFAQs();
            return ResponseEntity.ok(faqs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PutMapping("/{id}/answer")
    public ResponseEntity<FAQDTO> addAnswerToFAQ(
            @PathVariable Long id,
            @RequestParam String answer) {
        try {
            FAQDTO faqDTO = faqService.addAnswerToFAQ(id, answer);
            return ResponseEntity.ok(faqDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) {
        try {
            faqService.deleteFAQ(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
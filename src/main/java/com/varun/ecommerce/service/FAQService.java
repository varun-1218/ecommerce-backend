package com.varun.ecommerce.service;

import java.util.List;

import com.varun.ecommerce.dto.FAQDTO;

public interface FAQService {
    
    FAQDTO createFAQ(FAQDTO faqDTO, Long userId);
    
    FAQDTO updateFAQ(Long faqId, FAQDTO faqDTO);
    
    FAQDTO getFAQById(Long faqId);
    
    List<FAQDTO> getFAQsByProductId(Long productId);
    
    List<FAQDTO> getAllFAQs();
    
    void deleteFAQ(Long faqId);
    
    FAQDTO addAnswerToFAQ(Long faqId, String answer);
}
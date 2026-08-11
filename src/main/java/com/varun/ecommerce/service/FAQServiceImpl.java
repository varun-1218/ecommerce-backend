package com.varun.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varun.ecommerce.dto.FAQDTO;
import com.varun.ecommerce.entity.FAQ;
import com.varun.ecommerce.entity.Product;
import com.varun.ecommerce.entity.User;
import com.varun.ecommerce.repository.FAQRepository;
import com.varun.ecommerce.repository.ProductRepository;
import com.varun.ecommerce.repository.UserRepository;

@Service
public class FAQServiceImpl implements FAQService {
    
    @Autowired
    private FAQRepository faqRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public FAQDTO createFAQ(FAQDTO faqDTO, Long userId) {
        Product product = productRepository.findById(faqDTO.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        FAQ faq = new FAQ();
        faq.setQuestion(faqDTO.getQuestion());
        faq.setAnswer(faqDTO.getAnswer());
        faq.setProduct(product);
        faq.setUser(user);
        
        FAQ savedFAQ = faqRepository.save(faq);
        return convertToDTO(savedFAQ);
    }
    
    @Override
    public FAQDTO updateFAQ(Long faqId, FAQDTO faqDTO) {
        FAQ faq = faqRepository.findById(faqId)
            .orElseThrow(() -> new RuntimeException("FAQ not found"));
        
        faq.setQuestion(faqDTO.getQuestion());
        faq.setAnswer(faqDTO.getAnswer());
        
        FAQ updatedFAQ = faqRepository.save(faq);
        return convertToDTO(updatedFAQ);
    }
    
    @Override
    public FAQDTO getFAQById(Long faqId) {
        FAQ faq = faqRepository.findById(faqId)
            .orElseThrow(() -> new RuntimeException("FAQ not found"));
        return convertToDTO(faq);
    }
    
    @Override
    public List<FAQDTO> getFAQsByProductId(Long productId) {
        List<FAQ> faqs = faqRepository.findByProductId(productId);
        return faqs.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<FAQDTO> getAllFAQs() {
        List<FAQ> faqs = faqRepository.findAll();
        return faqs.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteFAQ(Long faqId) {
        FAQ faq = faqRepository.findById(faqId)
            .orElseThrow(() -> new RuntimeException("FAQ not found"));
        faqRepository.delete(faq);
    }
    
    @Override
    public FAQDTO addAnswerToFAQ(Long faqId, String answer) {
        FAQ faq = faqRepository.findById(faqId)
            .orElseThrow(() -> new RuntimeException("FAQ not found"));
        
        faq.setAnswer(answer);
        FAQ updatedFAQ = faqRepository.save(faq);
        return convertToDTO(updatedFAQ);
    }
    
    private FAQDTO convertToDTO(FAQ faq) {
        FAQDTO faqDTO = new FAQDTO();
        faqDTO.setId(faq.getId());
        faqDTO.setQuestion(faq.getQuestion());
        faqDTO.setAnswer(faq.getAnswer());
        faqDTO.setProductId(faq.getProduct().getId());
        faqDTO.setProductName(faq.getProduct().getName());
        faqDTO.setUserId(faq.getUser().getId());
        faqDTO.setUserName(faq.getUser().getFirstName() + " " + faq.getUser().getLastName());
        faqDTO.setCreatedAt(faq.getCreatedAt());
        return faqDTO;
    }
}
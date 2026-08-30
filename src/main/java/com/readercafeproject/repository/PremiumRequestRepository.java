package com.readercafeproject.repository;

 
import com.readercafeproject.model.PremiumRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PremiumRequestRepository extends JpaRepository<PremiumRequest, Long> {
// Status, User Email, သို့မဟုတ် Payment Method ဖြင့် ရှာဖွေခြင်း (ID အစဉ်အတိုင်း)
    Page<PremiumRequest> findByStatusContainingIgnoreCaseOrUserEmailContainingIgnoreCaseOrPaymentMethodContainingIgnoreCaseOrderByIdDesc(
            String status, String email, String paymentMethod, Pageable pageable);

    // Filter မပါဘဲ Pagination ဖြင့် ဆွဲထုတ်ခြင်း (ID အစဉ်အတိုင်း)
    Page<PremiumRequest> findAllByOrderByIdDesc(Pageable pageable);
}
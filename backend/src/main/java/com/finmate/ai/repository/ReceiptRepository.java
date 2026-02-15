package com.finmate.ai.repository;

import com.finmate.ai.entity.Receipt;
import com.finmate.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByUserOrderByCreatedAtDesc(User user);
}

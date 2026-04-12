package com.cafe.request.repo;

import com.cafe.request.model.ServiceRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
  List<ServiceRequest> findAllByOrderByCreatedAtDesc();
  List<ServiceRequest> findByStatus(String status);
}


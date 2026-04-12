package com.cafe.order.repo;

import com.cafe.order.model.CafeOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {
  List<CafeOrder> findByTableLabelOrderByCreatedAtDesc(String tableLabel);
  List<CafeOrder> findAllByOrderByCreatedAtDesc();
}


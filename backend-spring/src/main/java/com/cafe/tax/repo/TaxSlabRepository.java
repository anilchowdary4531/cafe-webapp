package com.cafe.tax.repo;

import com.cafe.tax.model.TaxSlab;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxSlabRepository extends JpaRepository<TaxSlab, Long> {
  List<TaxSlab> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);
  Optional<TaxSlab> findByNameAndIsActive(String name, Boolean isActive);
}


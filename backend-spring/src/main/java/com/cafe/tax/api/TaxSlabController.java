package com.cafe.tax.api;

import com.cafe.tax.model.TaxSlab;
import com.cafe.tax.repo.TaxSlabRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tax-slabs")
public class TaxSlabController {

  private final TaxSlabRepository taxSlabRepository;

  public TaxSlabController(TaxSlabRepository taxSlabRepository) {
    this.taxSlabRepository = taxSlabRepository;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<TaxSlab> listAll() {
    return taxSlabRepository.findAll();
  }

  @GetMapping("/active")
  public List<TaxSlab> listActive() {
    return taxSlabRepository.findByIsActiveOrderByCreatedAtDesc(true);
  }

  @PostMapping
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> create(@RequestBody CreateTaxSlabRequest req) {
    if (req.name() == null || req.name().isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error", "name is required"));
    }
    if (req.rate() == null || req.rate().signum() <= 0) {
      return ResponseEntity.badRequest().body(Map.of("error", "rate must be greater than 0"));
    }

    TaxSlab slab = new TaxSlab();
    slab.setName(req.name());
    slab.setRate(req.rate());
    slab.setDescription(req.description());
    slab.setIsActive(true);

    return ResponseEntity.ok(taxSlabRepository.save(slab));
  }

  @PatchMapping("/{id}")
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> update(
      @PathVariable Long id,
      @RequestBody UpdateTaxSlabRequest req) {
    TaxSlab slab = taxSlabRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Tax slab not found"));

    if (req.name() != null && !req.name().isBlank()) {
      slab.setName(req.name());
    }
    if (req.rate() != null && req.rate().signum() > 0) {
      slab.setRate(req.rate());
    }
    if (req.description() != null) {
      slab.setDescription(req.description());
    }
    if (req.isActive() != null) {
      slab.setIsActive(req.isActive());
    }
    slab.setUpdatedAt(LocalDateTime.now());

    return ResponseEntity.ok(taxSlabRepository.save(slab));
  }

  @DeleteMapping("/{id}")
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    TaxSlab slab = taxSlabRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Tax slab not found"));
    slab.setIsActive(false);
    slab.setUpdatedAt(LocalDateTime.now());
    taxSlabRepository.save(slab);
    return ResponseEntity.ok(Map.of("message", "Tax slab deactivated"));
  }

  // DTOs
  public record CreateTaxSlabRequest(
      String name,
      java.math.BigDecimal rate,
      String description
  ) {}

  public record UpdateTaxSlabRequest(
      String name,
      java.math.BigDecimal rate,
      String description,
      Boolean isActive
  ) {}
}




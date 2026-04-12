package com.cafe.request.api;

import com.cafe.request.model.ServiceRequest;
import com.cafe.request.repo.ServiceRequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

  private final ServiceRequestRepository requestRepository;
  private final ObjectMapper objectMapper;

  public RequestController(ServiceRequestRepository requestRepository, ObjectMapper objectMapper) {
    this.requestRepository = requestRepository;
    this.objectMapper = objectMapper;
  }

  @GetMapping
  public List<ServiceRequest> list() {
    return requestRepository.findAllByOrderByCreatedAtDesc();
  }

  @PostMapping
  @Transactional
  public ResponseEntity<?> create(@RequestBody ServiceRequestDto dto) {
    if (dto.table() == null || dto.table().isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error", "table is required"));
    }

    ServiceRequest sr = new ServiceRequest();
    sr.setTableLabel(dto.table());
    sr.setType(dto.type() != null ? dto.type() : "Other");
    sr.setNote(dto.note());
    sr.setCustomerPhone(dto.customerPhone());
    sr.setCustomerPhoneMasked(dto.customerPhoneMasked());

    if (dto.phoneVerifiedAt() != null && !dto.phoneVerifiedAt().isBlank()) {
      try {
        sr.setPhoneVerifiedAt(LocalDateTime.parse(dto.phoneVerifiedAt().replace("Z", "")));
      } catch (Exception ignored) {}
    }

    if (dto.items() != null) {
      try {
        sr.setItems(objectMapper.writeValueAsString(dto.items()));
      } catch (JsonProcessingException ignored) {}
    }

    return ResponseEntity.ok(requestRepository.save(sr));
  }

  @PatchMapping("/{id}/status")
  @Transactional
  public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody StatusRequest req) {
    ServiceRequest sr = requestRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    sr.setStatus(req.status());
    sr.setUpdatedAt(LocalDateTime.now());
    return ResponseEntity.ok(requestRepository.save(sr));
  }

  @DeleteMapping("/completed")
  @Transactional
  public ResponseEntity<?> deleteCompleted() {
    List<ServiceRequest> completed = requestRepository.findByStatus("completed");
    requestRepository.deleteAll(completed);
    return ResponseEntity.ok(Map.of("deleted", completed.size()));
  }

  // ── DTOs ─────────────────────────────────────────────────────────────────────
  public record ServiceRequestDto(
      String table,
      String type,
      String note,
      String customerPhone,
      String customerPhoneMasked,
      String phoneVerifiedAt,
      Object items
  ) {}

  public record StatusRequest(String status) {}
}


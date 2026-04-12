package com.cafe.menu.api;

import com.cafe.menu.model.MenuItem;
import com.cafe.menu.repo.MenuItemRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

  private final MenuItemRepository menuItemRepository;

  public MenuController(MenuItemRepository menuItemRepository) {
    this.menuItemRepository = menuItemRepository;
  }

  @GetMapping
  public List<MenuItem> list() {
    return menuItemRepository.findByAvailableTrueOrderByCategoryAscNameAsc();
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMIN')")
  public List<MenuItem> listAll() {
    return menuItemRepository.findAllByOrderByCategoryAscNameAsc();
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> create(@Valid @RequestBody MenuItemRequest request) {
    MenuItem item = new MenuItem();
    item.setName(request.name());
    item.setCategory(request.category());
    item.setPrice(request.price() != null ? request.price() : BigDecimal.ZERO);
    item.setDescription(request.description());
    item.setImageUrl(request.imageUrl());
    item.setRestricted(Boolean.TRUE.equals(request.restricted()));
    if (request.available() != null) item.setAvailable(request.available());
    return ResponseEntity.ok(menuItemRepository.save(item));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MenuItemRequest request) {
    MenuItem item = menuItemRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
    if (request.name() != null) item.setName(request.name());
    if (request.category() != null) item.setCategory(request.category());
    if (request.price() != null) item.setPrice(request.price());
    if (request.description() != null) item.setDescription(request.description());
    if (request.imageUrl() != null) item.setImageUrl(request.imageUrl());
    if (request.restricted() != null) item.setRestricted(request.restricted());
    if (request.available() != null) item.setAvailable(request.available());
    item.setUpdatedAt(LocalDateTime.now());
    return ResponseEntity.ok(menuItemRepository.save(item));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    MenuItem item = menuItemRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
    item.setAvailable(false);
    item.setUpdatedAt(LocalDateTime.now());
    menuItemRepository.save(item);
    return ResponseEntity.ok(Map.of("ok", true));
  }

  // DTO
  public record MenuItemRequest(
      String name,
      String category,
      BigDecimal price,
      String description,
      String imageUrl,
      Boolean restricted,
      Boolean available
  ) {}
}


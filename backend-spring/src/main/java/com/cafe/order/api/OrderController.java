package com.cafe.order.api;

import com.cafe.order.model.CafeOrder;
import com.cafe.order.model.OrderItem;
import com.cafe.order.repo.CafeOrderRepository;
import com.cafe.tax.repo.TaxSlabRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.05");

  private final CafeOrderRepository orderRepository;
  private final TaxSlabRepository taxSlabRepository;

  public OrderController(CafeOrderRepository orderRepository, TaxSlabRepository taxSlabRepository) {
    this.orderRepository = orderRepository;
    this.taxSlabRepository = taxSlabRepository;
  }

  @GetMapping
  public List<CafeOrder> list(@RequestParam(required = false) String table) {
    if (table != null && !table.isBlank()) {
      return orderRepository.findByTableLabelOrderByCreatedAtDesc(table);
    }
    return orderRepository.findAllByOrderByCreatedAtDesc();
  }

  @PostMapping
  @Transactional
  public ResponseEntity<?> create(@RequestBody OrderRequest request) {
    if (request.table() == null || request.table().isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error", "table is required"));
    }
    if (request.items() == null || request.items().isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "items cannot be empty"));
    }

    CafeOrder order = new CafeOrder();
    order.setTableLabel(request.table());
    order.setNote(request.note());
    order.setCustomerPhone(request.customerPhone());
    order.setCustomerPhoneMasked(request.customerPhoneMasked());
    order.setCustomerEmail(request.customerEmail());

    if (request.phoneVerifiedAt() != null) {
      order.setPhoneVerifiedAt(LocalDateTime.parse(request.phoneVerifiedAt().replace("Z", "")));
    }

    List<OrderItem> items = request.items().stream().map(i -> {
      OrderItem oi = new OrderItem();
      oi.setOrder(order);
      oi.setMenuItemId(i.menuItemId());
      oi.setName(i.name());
      oi.setQty(i.qty() > 0 ? i.qty() : 1);
      oi.setPrice(i.price() != null ? i.price() : BigDecimal.ZERO);
      oi.setRestricted(Boolean.TRUE.equals(i.restricted()));
      return oi;
    }).toList();
    order.setItems(items);

    BigDecimal subtotal = items.stream()
        .filter(i -> !i.isRestricted())
        .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQty())))
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);

    // Get tax rate from database (default to 5% if no active tax slab found)
    BigDecimal taxRate = taxSlabRepository.findByNameAndIsActive("Standard Tax (5%)", true)
        .map(slab -> slab.getRate())
        .orElse(DEFAULT_TAX_RATE);

    BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    order.setSubtotal(subtotal);
    order.setTax(tax);
    order.setTotal(subtotal.add(tax));

    return ResponseEntity.ok(orderRepository.save(order));
  }

  @PatchMapping("/{id}/status")
  @Transactional
  public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody StatusRequest req) {
    CafeOrder order = orderRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    order.setStatus(req.status());
    order.setUpdatedAt(LocalDateTime.now());
    return ResponseEntity.ok(orderRepository.save(order));
  }

  @PatchMapping("/{id}/payment")
  @Transactional
  public ResponseEntity<?> updatePayment(@PathVariable Long id, @RequestBody StatusRequest req) {
    CafeOrder order = orderRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    order.setPaymentStatus(req.status());
    if ("paid".equals(req.status())) {
      order.setPaidAt(LocalDateTime.now());
    } else {
      order.setPaidAt(null);
    }
    order.setUpdatedAt(LocalDateTime.now());
    return ResponseEntity.ok(orderRepository.save(order));
  }

  // ── DTOs ─────────────────────────────────────────────────────────────────────
  public record OrderRequest(
      String table,
      String note,
      String customerPhone,
      String customerPhoneMasked,
      String customerEmail,
      String phoneVerifiedAt,
      List<ItemRequest> items
  ) {}

  public record ItemRequest(
      Long menuItemId,
      String name,
      int qty,
      BigDecimal price,
      Boolean restricted
  ) {}

  public record StatusRequest(String status) {}
}


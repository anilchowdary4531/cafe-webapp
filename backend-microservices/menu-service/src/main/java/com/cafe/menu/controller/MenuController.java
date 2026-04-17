package com.cafe.menu.controller;

import com.cafe.menu.entity.MenuItem;
import com.cafe.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public List<Map<String, Object>> getMenu() {
        return menuService.getAllMenuItems().stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("name", item.getName());
            map.put("category", item.getCategory());
            map.put("price", item.getPrice().doubleValue());
            map.put("description", item.getDescription());
            map.put("restricted", item.getRestricted());
            return map;
        }).toList();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createMenuItem(@RequestBody Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String category = (String) payload.get("category");
        BigDecimal price = BigDecimal.valueOf((Double) payload.get("price"));
        String description = (String) payload.getOrDefault("description", null);
        Boolean restricted = (Boolean) payload.getOrDefault("restricted", false);

        MenuItem item = menuService.createMenuItem(name, category, price, description, restricted);
        Map<String, Object> response = new HashMap<>();
        response.put("id", item.getId());
        response.put("name", item.getName());
        response.put("category", item.getCategory());
        response.put("price", item.getPrice().doubleValue());
        response.put("description", item.getDescription());
        response.put("restricted", item.getRestricted());
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        Optional<MenuItem> item = menuService.getMenuItemById(id);
        if (item.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}

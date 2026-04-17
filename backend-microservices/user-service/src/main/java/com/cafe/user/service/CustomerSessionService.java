package com.cafe.user.service;

import com.cafe.user.entity.CustomerSession;
import com.cafe.user.repository.CustomerSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomerSessionService {

    @Autowired
    private CustomerSessionRepository customerSessionRepository;

    public CustomerSession createOrUpdateSession(String table, String phone) {
        Optional<CustomerSession> existing = customerSessionRepository.findByTableLabel(table);
        CustomerSession session = existing.orElse(new CustomerSession());
        session.setTableLabel(table);
        session.setPhone(phone);
        session.setVerifiedAt(LocalDateTime.now());
        return customerSessionRepository.save(session);
    }

    public Optional<CustomerSession> getSession(String table) {
        return customerSessionRepository.findByTableLabel(table);
    }
}

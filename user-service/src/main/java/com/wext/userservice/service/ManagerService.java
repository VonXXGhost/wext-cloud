package com.wext.userservice.service;

import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.userservice.domain.Manager;
import com.wext.userservice.repository.ManagerRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.regex.Pattern;


@Service
@Slf4j
public class ManagerService {
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Manager getManagerByName(@NonNull String name) {
        return this.managerRepository.findByName(name)
                .orElseThrow(() -> new NonExistentException("Name: " + name + " not found."));
    }

    public Manager getManagerById(@NonNull Long id) {
        return this.managerRepository.findById(id)
                .orElseThrow(() -> new NonExistentException("ID: " + id + " not found."));
    }

    @Transactional
    public Manager createManager(@NonNull String name, @NonNull String password, String role) {
        if (Pattern.matches("^\\d*$", name)) {  // 名称不能为纯数字
            throw new InvalidOperationException("Name error. Digital only.");
        }
        if (role == null || role.length() == 0) {
            role = "Manager";
        }

        try {
            Manager manager = managerRepository.saveAndFlush(Manager.builder()
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());

            log.info("New manager has created: " + manager);
            return manager;
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            throw new InvalidOperationException("Can not create.Maybe the manager with this name is exist.");
        }

    }
}

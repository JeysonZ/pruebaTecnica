package com.example.prueba_tecnica.repository;

import com.example.prueba_tecnica.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

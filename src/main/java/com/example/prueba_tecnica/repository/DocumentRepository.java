package com.example.prueba_tecnica.repository;

import com.example.prueba_tecnica.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}

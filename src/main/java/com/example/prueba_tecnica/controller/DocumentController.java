package com.example.prueba_tecnica.controller;

import com.example.prueba_tecnica.model.Document;
import com.example.prueba_tecnica.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    /**
     * Controller method to get the list of documents.
     *
     * @return documents the list of documents.
     */
    @GetMapping
    public ResponseEntity<List<Document>> getDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        log.warn("Documents found: " + documents);
        return ResponseEntity.ok(documents);
    }
}

package com.example.prueba_tecnica.controller;

import com.example.prueba_tecnica.model.Document;
import com.example.prueba_tecnica.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/excel")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }
        try {
            documentService.processExcelFile(file);
            Document savedDocument = documentService.saveDocument(file.getOriginalFilename(), "xlsx", file.getSize());
            log.warn("Save file excel: " + savedDocument);
            return ResponseEntity.ok(savedDocument);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }

    @PostMapping("/pdf")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid PDF file.");
        }
        Document savedDocument = documentService.saveDocument(file.getOriginalFilename(), "pdf", file.getSize());
        log.warn("Save file pdf: " + savedDocument);
        return ResponseEntity.ok(savedDocument);
    }
}

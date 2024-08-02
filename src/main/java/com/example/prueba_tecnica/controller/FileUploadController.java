package com.example.prueba_tecnica.controller;

import com.example.prueba_tecnica.constants.Constants;
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

    /**
     * Method to send and save Excel documents
     *
     * @param file the MultipartFile of documents
     * @return a message states or the document data
     */
    @PostMapping("/excel")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }
        try {
            documentService.processExcelFile(file);
            Document savedDocument = documentService.saveDocument(file.getOriginalFilename(),
                    Constants.EXCEL_DOCUMENT_EXTENSION, file.getSize());
            log.warn("Save file excel: " + savedDocument);
            return ResponseEntity.ok(savedDocument);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }

    /**
     * Method to send and save PDF documents
     *
     * @param file the MultipartFile of documents
     * @return a message states or the document data
     */
    @PostMapping("/pdf")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid PDF file.");
        }
        Document savedDocument = documentService.saveDocument(file.getOriginalFilename(),
                Constants.PDF_DOCUMENT_EXTENSION, file.getSize());
        log.warn("Save file pdf: " + savedDocument);
        return ResponseEntity.ok(savedDocument);
    }
}

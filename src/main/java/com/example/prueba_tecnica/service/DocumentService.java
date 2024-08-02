package com.example.prueba_tecnica.service;

import com.example.prueba_tecnica.constants.Constants;
import com.example.prueba_tecnica.model.Document;
import com.example.prueba_tecnica.model.User;
import com.example.prueba_tecnica.repository.DocumentRepository;
import com.example.prueba_tecnica.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method for saving the document
     *
     * @param fileName the file name.
     * @param fileType the file type.
     * @param fileSize the file size.
     * @return the Document
     */
    public Document saveDocument(final String fileName, final String fileType, final long fileSize) {
        final Document document = new Document();
        document.setFileName(fileName);
        document.setFileType(fileType);
        document.setUploadDate(LocalDateTime.now());
        document.setFileSize(fileSize);
        return documentRepository.save(document);
    }

    /**
     * Method to get all saved documents.
     *
     * @return a list of documents.
     */
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * Method for processing Excel data.
     *
     * @param file the MultipartFile
     * @throws IOException
     */
    public void processExcelFile(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Integer> headerMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                headerMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                User user = new User();
                user.setId(getLongCellValue(row, headerMap.get(Constants.COLUMN_ID)));
                user.setName(getStringCellValue(row, headerMap.get(Constants.COLUMN_NAME)));
                user.setAge(getIntCellValue(row, headerMap.get(Constants.COLUMN_AGE)));
                user.setEmail(getStringCellValue(row, headerMap.get(Constants.COLUMN_EMAIL)));
                users.add(user);
            }
            userRepository.saveAll(users);
        }
    }

    private Long getLongCellValue(Row row, Integer columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private String getStringCellValue(Row row, Integer columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return (cell != null && cell.getCellType() == CellType.STRING) ? cell.getStringCellValue() : null;
    }

    private Integer getIntCellValue(Row row, Integer columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return (cell != null && cell.getCellType() == CellType.NUMERIC) ? (int) cell.getNumericCellValue() : null;
    }
}

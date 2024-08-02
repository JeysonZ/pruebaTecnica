package com.example.prueba_tecnica;

import com.example.prueba_tecnica.constants.Constants;
import com.example.prueba_tecnica.model.Document;
import com.example.prueba_tecnica.model.User;
import com.example.prueba_tecnica.repository.DocumentRepository;
import com.example.prueba_tecnica.repository.UserRepository;
import com.example.prueba_tecnica.service.DocumentService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllDocuments() {
        Document document = new Document();
        document.setId(1L);
        document.setFileName("test.pdf");
        document.setFileType("upload/pdf");
        document.setUploadDate(LocalDateTime.now());
        document.setFileSize(12345);

        when(documentRepository.findAll()).thenReturn(Collections.singletonList(document));

        List<Document> documents = documentService.getAllDocuments();
        assertEquals(1, documents.size());
        assertEquals("test.pdf", documents.get(0).getFileName());
    }

    @Test
    public void testSaveDocument() {
        String fileName = "test.pdf";
        String fileType = "application/pdf";
        long fileSize = 12345;

        Document expectedDocument = new Document();
        expectedDocument.setId(1L);
        expectedDocument.setFileName(fileName);
        expectedDocument.setFileType(fileType);
        expectedDocument.setUploadDate(LocalDateTime.now());
        expectedDocument.setFileSize(fileSize);

        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(1L);
            return doc;
        });

        Document result = documentService.saveDocument(fileName, fileType, fileSize);

        assertEquals(fileName, result.getFileName(), "File name should match");
        assertEquals(fileType, result.getFileType(), "File type should match");
        assertEquals(fileSize, result.getFileSize(), "File size should match");
        assertEquals(1L, result.getId(), "ID should match");

        verify(documentRepository).save(any(Document.class));
    }

    @Test
    public void testProcessExcelFile() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue(Constants.COLUMN_ID);
        headerRow.createCell(1).setCellValue(Constants.COLUMN_NAME);
        headerRow.createCell(2).setCellValue(Constants.COLUMN_AGE);
        headerRow.createCell(3).setCellValue(Constants.COLUMN_EMAIL);

        Row userRow = sheet.createRow(1);
        userRow.createCell(0).setCellValue(1L);
        userRow.createCell(1).setCellValue("Jeyson");
        userRow.createCell(2).setCellValue(30);
        userRow.createCell(3).setCellValue("jeyson@example.com");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        MultipartFile file = new MockMultipartFile("file", "users.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());

        documentService.processExcelFile(file);

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setName("Jeyson");
        expectedUser.setAge(30);
        expectedUser.setEmail("jeyson@example.com");

        verify(userRepository, times(1)).saveAll(Arrays.asList(expectedUser));
    }
}

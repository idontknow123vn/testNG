package com.example.testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
    public List<String[]> readLoginData(String filePath, int sheetNum) throws IOException {
        List<String[]> loginData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(sheetNum); // Sheet 1
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua header
                String id = String.valueOf(row.getCell(0).getNumericCellValue());
                String description = row.getCell(1).getStringCellValue();
                String email = row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue();
                String password = row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue();
                String expectedResult = row.getCell(4).getStringCellValue(); // Đọc Expected result từ cột thứ 5
                loginData.add(new String[]{id, description, email, password, expectedResult});
            }
        }
        return loginData;
    }

    public List<String[]> readSignUpData(String filePath, int sheetNum) throws IOException {
        List<String[]> loginData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(sheetNum); // Sheet 1
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua header
                String id = String.valueOf(row.getCell(0).getNumericCellValue());
                String description = row.getCell(1).getStringCellValue();
                String firstName = row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue();
                String lastName = row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue();
                String country = row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue();
                String phone = row.getCell(5) == null ? "" : row.getCell(5).getStringCellValue();
                String email = row.getCell(6) == null ? "" : row.getCell(6).getStringCellValue();
                String password = row.getCell(7) == null ? "" : row.getCell(7).getStringCellValue();
                String expectedResult = row.getCell(8).getStringCellValue(); // Đọc Expected result từ cột thứ 5
                loginData.add(new String[]{id, description, firstName, lastName, country, phone, email, password, expectedResult});
            }
        }
        return loginData;
    }
}

package com.example.testing;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {
    public void writeResult(String filePath, String id, String description, String expectedResult, String actualResult, int sheetNum) throws IOException {
        Workbook workbook;
        FileOutputStream fos;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            workbook = new XSSFWorkbook(); // Tạo mới nếu file chưa tồn tại
        }

        Sheet sheet = workbook.getSheetAt(sheetNum); // Sheet 1

        // Xóa dữ liệu hiện có trong sheet
        int numberOfRows = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < numberOfRows; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                sheet.removeRow(row);
            }
        }

        int lastRow = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(lastRow);

        row.createCell(0).setCellValue(Double.parseDouble(id));
        row.createCell(1).setCellValue(description);
        row.createCell(2).setCellValue(expectedResult);
        row.createCell(3).setCellValue(actualResult);

        fos = new FileOutputStream(filePath);
        workbook.write(fos);
        fos.close();
    }
}

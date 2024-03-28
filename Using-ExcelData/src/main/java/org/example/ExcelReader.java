package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExcelReader {
    public static List<UserData> readExcelData(String filePath) {
        List<UserData> userDataList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet("testData");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                Iterator<Cell> cellIterator = row.cellIterator();

                UserData userData = new UserData();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.getCellType() == CellType.STRING) {
                        if (cell.getColumnIndex() == 0) {
                            userData.setUsername(cell.getStringCellValue());
                        } else if (cell.getColumnIndex() == 1) {
                            userData.setPassword(cell.getStringCellValue());
                        } else if (cell.getColumnIndex() == 3) {
                            userData.setType(cell.getStringCellValue());
                        }
                    }
                }
                userDataList.add(userData);
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userDataList;
    }
}

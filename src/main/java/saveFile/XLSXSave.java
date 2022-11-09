package saveFile;

import data.MainData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class XLSXSave {
    Workbook workbook;
    MainData mainData;
    File saveFile;
    File file;

    public XLSXSave(File file, MainData mainData, File saveFile) throws IOException, InvalidFormatException {
        String filePath = file.getPath();
        this.file = file;
        this.saveFile = saveFile;
        this.mainData = mainData;
        workbook = new XSSFWorkbook(new FileInputStream(filePath));
    }

    public void setData(){
        for(int i = 0; i < mainData.userInfo.size();i++){
            workbook.getSheetAt(0).createRow(workbook.getSheetAt(0).getPhysicalNumberOfRows());
            for(int k = 0; k < mainData.userInfo.get(i).size(); k++){
                workbook.getSheetAt(0).getRow(workbook.getSheetAt(0).getPhysicalNumberOfRows()-1).createCell(k).setCellValue(mainData.userInfo.get(i).get(k));
            }
        }
    }

    public void close() throws IOException {
        workbook.close();
    }

    public void saveFile() throws IOException {
        workbook.write(new FileOutputStream(new File(saveFile.getPath() + "\\Ответы_пользователей.xlsx")));
        workbook.write(new FileOutputStream(file));
    }
}
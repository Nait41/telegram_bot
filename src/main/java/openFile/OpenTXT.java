package openFile;

import data.MainData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class OpenTXT {
    File txtFile;

    public OpenTXT(File txtFile){
        this.txtFile = txtFile;
    }

    public void getQuestList(MainData mainData) throws FileNotFoundException {
        Scanner scanner = new Scanner(txtFile);
        mainData.questList.add(new ArrayList<>());
        while (scanner.hasNextLine()){
            String temp = scanner.nextLine();
            if(!temp.equals("")){
                mainData.questList.get(mainData.questList.size()-1).add(temp);
            } else {
                mainData.questList.add(new ArrayList<>());
            }
        }
    }
}

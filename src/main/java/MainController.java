
import bots.MainBot;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import openFile.OpenTXT;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import saveFile.XLSXSave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainController {

    MainBot mainBot;
    TelegramBotsApi telegramBotsApi;
    OpenTXT openTXT;
    boolean checkLoad;
    boolean checkUnload;
    File txtFile;
    File saveDirectory;
    static String errorMessageStr = "";
    boolean checkStartActive = false;

    @FXML
    private Button answerTable;

    @FXML
    private Button closeButton;

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private Button startButton;

    @FXML
    private Button loadQuest;

    public void addHinds(){
        Tooltip closeTip = new Tooltip();
        closeTip.setText("Нажмите, для того, чтобы закрыть приложение");
        closeTip.setStyle("-fx-text-fill: #ffaa00;");
        closeButton.setTooltip(closeTip);

        Tooltip answerTableTip = new Tooltip();
        answerTableTip.setText("Нажмите, для того, чтобы сохранить ответы пользователей в таблицу");
        answerTableTip.setStyle("-fx-text-fill: #ffaa00;");
        answerTable.setTooltip(answerTableTip);

        Tooltip startTip = new Tooltip();
        startTip.setText("Нажмите для того, чтобы запустить бота");
        startTip.setStyle("-fx-text-fill: #ffaa00;");
        startButton.setTooltip(startTip);

        Tooltip loadTip = new Tooltip();
        loadTip.setText("Нажмите, для того, чтобы загрузить вопросы");
        loadTip.setStyle("-fx-text-fill: #ffaa00;");
        loadQuest.setTooltip(loadTip);
    }

    @FXML
    void initialize() throws FileNotFoundException, InterruptedException {
        addHinds();

        FileInputStream closeStream = new FileInputStream(Application.rootDirPath + "\\close.png");
        Image closeImage = new Image(closeStream);
        ImageView closeView = new ImageView(closeImage);
        closeButton.graphicProperty().setValue(closeView);

        FileInputStream startStream = new FileInputStream(Application.rootDirPath + "\\start.png");
        Image startImage = new Image(startStream);
        ImageView startView = new ImageView(startImage);
        startButton.graphicProperty().setValue(startView);

        FileInputStream loadQuestStream = new FileInputStream(Application.rootDirPath + "\\loadQuest.png");
        Image loadQuestImage = new Image(loadQuestStream);
        ImageView loadQuestView = new ImageView(loadQuestImage);
        loadQuest.graphicProperty().setValue(loadQuestView);

        FileInputStream answerTableStream = new FileInputStream(Application.rootDirPath + "\\answerTable.png");
        Image answerTableImage = new Image(answerTableStream);
        ImageView answerTableView = new ImageView(answerTableImage);
        answerTable.graphicProperty().setValue(answerTableView);

        int r = 60;
        startButton.setShape(new Circle(r));
        startButton.setMinSize(r*2, r*2);
        startButton.setMaxSize(r*2, r*2);

        startButton.setOnAction(ActionEvent -> {
            if(checkLoad && checkUnload){
                if(checkStartActive == false){
                    mainBot = new MainBot();
                    try {
                        telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                        telegramBotsApi.registerBot(mainBot);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    openTXT = new OpenTXT(txtFile);
                    try {
                        openTXT.getQuestList(mainBot.mainData);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    startButton.setStyle("-fx-background-color: #2ee500");
                    checkStartActive = true;
                } else {
                    try {
                        XLSXSave xlsxSave = new XLSXSave(new File(Application.rootDirPath + "\\usersInfo.XLSX"), mainBot.mainData, saveDirectory);
                        xlsxSave.setData();
                        xlsxSave.saveFile();
                        xlsxSave.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidFormatException e) {
                        e.printStackTrace();
                    }
                    mainBot = null;
                    telegramBotsApi = null;
                    openTXT = null;
                    startButton.setStyle("-fx-background-color: #ffaa00");
                    checkStartActive = false;
                }
            } else {
                errorMessageStr = "Вы не загрузили список вопросов или не выбрали дирректорию сохранения результатов пользователей...";
                ErrorController errorController = new ErrorController();
                try {
                    errorController.start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        answerTable.setOnAction(ActionEvent -> {
            if(!checkStartActive)
            {
                DirectoryChooser dirChooser = new DirectoryChooser();
                saveDirectory = dirChooser.showDialog(new Stage());
                checkUnload = true;
            }
            else
            {
                errorMessageStr = "Телеграм бот запущен. Выключите бота и повторите попытку...";
                ErrorController errorController = new ErrorController();
                try {
                    errorController.start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        loadQuest.setOnAction(ActionEvent -> {
            if(!checkStartActive)
            {
                FileChooser fileChooser = new FileChooser();
                txtFile = fileChooser.showOpenDialog(new Stage());
                checkLoad = true;
            }
            else
            {
                errorMessageStr = "Телеграм бот запущен. Выключите бота и повторите попытку...";
                ErrorController errorController = new ErrorController();
                try {
                    errorController.start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        closeButton.setOnAction(ActionEvent -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
    }
}

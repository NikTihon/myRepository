package pet.project1.realtyapp.example;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileChooserExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор файла");

        // Установка начального каталога (необязательно)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Фильтры файлов (необязательно)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Показать диалог открытия файла
        File selectedFile = fileChooser.showOpenDialog(primaryStage); // Открыть
        // Или: File selectedFile = fileChooser.showSaveDialog(primaryStage); // Сохранить

        if (selectedFile != null) {
            System.out.println("Выбранный файл: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("Выбор файла отменен.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


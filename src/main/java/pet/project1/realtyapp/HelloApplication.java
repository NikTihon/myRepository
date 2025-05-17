package pet.project1.realtyapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class HelloApplication extends Application {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();

    final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    final TableView<TableEntity> table = new TableView<>();
    final ObservableList<TableEntity> tableData = FXCollections.observableArrayList();

    private void initApp() {
        xAxis.setLabel("Время");
        yAxis.setLabel("Цена за м.кв., руб.");

        TableColumn<TableEntity, String> time = new TableColumn<>("Время");
        time.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<TableEntity, String> price = new TableColumn<>("Цена");
        price.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<TableEntity, String> chainAbsoluteGrowth = new TableColumn<>("Абсолютный прирост цепной");
        chainAbsoluteGrowth.setCellValueFactory(new PropertyValueFactory<>("chainAbsoluteGrowth"));

        TableColumn<TableEntity, String> basicAbsoluteGrowth = new TableColumn<>("Абсолютный прирост базисный");
        basicAbsoluteGrowth.setCellValueFactory(new PropertyValueFactory<>("basicAbsoluteGrowth"));

        TableColumn<TableEntity, String> chainGrowthRates = new TableColumn<>("Темпы роста цепные");
        chainGrowthRates.setCellValueFactory(new PropertyValueFactory<>("chainGrowthRates"));

        TableColumn<TableEntity, String> basicGrowthRates = new TableColumn<>("Темпы роста базисные");
        basicGrowthRates.setCellValueFactory(new PropertyValueFactory<>("basicGrowthRates"));

        TableColumn<TableEntity, String> chainGrowthRates2 = new TableColumn<>("Темпы прироста цепные");
        chainGrowthRates2.setCellValueFactory(new PropertyValueFactory<>("chainGrowthRates2"));

        TableColumn<TableEntity, String> basicGrowthRates2 = new TableColumn<>("Темпы прироста базисные");
        basicGrowthRates2.setCellValueFactory(new PropertyValueFactory<>("basicGrowthRates2"));

        TableColumn<TableEntity, String> absoluteValue = new TableColumn<>("Абсолютное значение");
        absoluteValue.setCellValueFactory(new PropertyValueFactory<>("absoluteValue"));

        TableColumn<TableEntity, String> relativeAcceleration = new TableColumn<>("Относительное ускорение");
        relativeAcceleration.setCellValueFactory(new PropertyValueFactory<>("relativeAcceleration"));

        TableColumn<TableEntity, String> advanceRatio = new TableColumn<>("Коэффициент опережения");
        advanceRatio.setCellValueFactory(new PropertyValueFactory<>("advanceRatio"));

        table.getColumns().addAll(time, price, chainAbsoluteGrowth, basicAbsoluteGrowth, chainGrowthRates,
                basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                absoluteValue, relativeAcceleration, advanceRatio);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 800);

        initApp();

        TextField fileField = new TextField();
        Button fileButton = new Button("Выбрать файл");

        Button okButton = new Button("Ок");
        okButton.setOnAction(actionEvent -> {
            try (Scanner sc = new Scanner(new FileReader(fileField.getText()))) {
                XYChart.Series series = new XYChart.Series();
                series.setName("График");
                while (sc.hasNextInt()) {
                    int time = sc.nextInt();
                    double price = sc.nextDouble();

                    if (tableData.isEmpty()) {
                        tableData.add(new TableEntity(time, price));
                    } else {
                        double chainAbsoluteGrowth = price - tableData.getLast().getPrice();
                        double basicAbsoluteGrowth = price - tableData.getFirst().getPrice();
                        double chainGrowthRates = (price/ tableData.getLast().getPrice()) * 100;
                        double basicGrowthRates = (price/ tableData.getFirst().getPrice()) * 100;
                        double chainGrowthRates2 = chainAbsoluteGrowth/tableData.getLast().getPrice();
                        double basicGrowthRates2 = basicAbsoluteGrowth/tableData.getFirst().getPrice();
                        double absoluteValue = 0.01 * tableData.getLast().getPrice();
                        if(tableData.size() >= 2){
                            double relativeAcceleration = (chainGrowthRates - tableData.getLast().getChainGrowthRates()) * 100;
                            double advanceRatio = chainGrowthRates/tableData.getLast().getChainGrowthRates();
                            tableData.add(new TableEntity(time, price,chainAbsoluteGrowth, basicAbsoluteGrowth,
                                    chainGrowthRates, basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                                    absoluteValue, relativeAcceleration, advanceRatio));
                        }else{
                            tableData.add(new TableEntity(time, price,chainAbsoluteGrowth, basicAbsoluteGrowth,
                                    chainGrowthRates, basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                                    absoluteValue));
                        }
                    }
                    series.getData().add(new XYChart.Data<>(time, price));
                }
                table.setItems(tableData);
                chart.getData().add(series);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        HBox fileHBox = new HBox();
        fileHBox.getChildren().addAll(fileField, fileButton, okButton);

        VBox vbox = new VBox(fileHBox, chart, table);

        fileButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            fileField.setText(file.getAbsolutePath());
        });

        root.getChildren().addAll(vbox);
        stage.setTitle("Приложение для анализа и прогнозирования рынка недвижимости");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
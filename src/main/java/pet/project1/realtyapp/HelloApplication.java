package pet.project1.realtyapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pet.project1.realtyapp.entity.MainCharacteristicsTableEntity;
import pet.project1.realtyapp.entity.MovingAverageTableEntity;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.stream.IntStream;

public class HelloApplication extends Application {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();

    final NumberAxis xAxis2 = new NumberAxis();
    final NumberAxis yAxis2 = new NumberAxis();

    final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    final LineChart<Number, Number> chart2 = new LineChart<>(xAxis2, yAxis2);
    final TableView<MainCharacteristicsTableEntity> table = new TableView<>();
    final TableView<MovingAverageTableEntity> table2 = new TableView<>();
    final ObservableList<MainCharacteristicsTableEntity> tableData = FXCollections.observableArrayList();
    final ObservableList<MovingAverageTableEntity> tableData2 = FXCollections.observableArrayList();

    private double timeSum() {
        return tableData2.stream().
                skip(1).
                limit(tableData2.size() - 2).
                mapToDouble(MovingAverageTableEntity::getTime).sum();
    }

    private double timeSqrSum() {
        return tableData2.stream().
                skip(1).
                limit(tableData2.size() - 2).
                mapToDouble(i -> Math.pow(i.getTime(), 2)).
                sum();
    }


    private double priceSum() {
        return tableData2.stream()
                .mapToDouble(MovingAverageTableEntity::getMovingAverage)
                .sum();
    }

    private double timeAndPriceSum() {
        return tableData2.stream()
                .skip(1)
                .limit(tableData2.size() - 2)
                .mapToDouble(i -> i.getPrice() * i.getTime())
                .sum();
    }

    private double getK(double sumX, double sumY, double sumXY, double sumSqrX) {
        return ((tableData2.size() - 2) * sumXY - sumX * sumY) / ((tableData2.size() - 2) * sumSqrX - sumX * sumX);
    }

    private double getB(double k, double sumX, double sumY) {
        return (sumY - k * sumX) / (tableData2.size() - 2);
    }

    private double linearFunction(double x, double k, double b) {
        return k * x + b;
    }


    private void initApp() {
        xAxis.setLabel("Время");
        yAxis.setLabel("Цена за м.кв., руб.");

        xAxis2.setLabel("Время");
        yAxis2.setLabel("Цена за м.кв., руб.");

        chart.setMinHeight(500);
        chart2.setMinHeight(500);
        table.setMinHeight(500);
        table2.setMinHeight(500);

        TableColumn<MainCharacteristicsTableEntity, String> time = new TableColumn<>("Время");
        time.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<MainCharacteristicsTableEntity, String> price = new TableColumn<>("Цена");
        price.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<MainCharacteristicsTableEntity, String> chainAbsoluteGrowth = new TableColumn<>("Абсолютный прирост цепной");
        chainAbsoluteGrowth.setCellValueFactory(new PropertyValueFactory<>("chainAbsoluteGrowth"));

        TableColumn<MainCharacteristicsTableEntity, String> basicAbsoluteGrowth = new TableColumn<>("Абсолютный прирост базисный");
        basicAbsoluteGrowth.setCellValueFactory(new PropertyValueFactory<>("basicAbsoluteGrowth"));

        TableColumn<MainCharacteristicsTableEntity, String> chainGrowthRates = new TableColumn<>("Темпы роста цепные");
        chainGrowthRates.setCellValueFactory(new PropertyValueFactory<>("chainGrowthRates"));

        TableColumn<MainCharacteristicsTableEntity, String> basicGrowthRates = new TableColumn<>("Темпы роста базисные");
        basicGrowthRates.setCellValueFactory(new PropertyValueFactory<>("basicGrowthRates"));

        TableColumn<MainCharacteristicsTableEntity, String> chainGrowthRates2 = new TableColumn<>("Темпы прироста цепные");
        chainGrowthRates2.setCellValueFactory(new PropertyValueFactory<>("chainGrowthRates2"));

        TableColumn<MainCharacteristicsTableEntity, String> basicGrowthRates2 = new TableColumn<>("Темпы прироста базисные");
        basicGrowthRates2.setCellValueFactory(new PropertyValueFactory<>("basicGrowthRates2"));

        TableColumn<MainCharacteristicsTableEntity, String> absoluteValue = new TableColumn<>("Абсолютное значение");
        absoluteValue.setCellValueFactory(new PropertyValueFactory<>("absoluteValue"));

        TableColumn<MainCharacteristicsTableEntity, String> relativeAcceleration = new TableColumn<>("Относительное ускорение");
        relativeAcceleration.setCellValueFactory(new PropertyValueFactory<>("relativeAcceleration"));

        TableColumn<MainCharacteristicsTableEntity, String> advanceRatio = new TableColumn<>("Коэффициент опережения");
        advanceRatio.setCellValueFactory(new PropertyValueFactory<>("advanceRatio"));

        table.getColumns().addAll(time, price, chainAbsoluteGrowth, basicAbsoluteGrowth, chainGrowthRates,
                basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                absoluteValue, relativeAcceleration, advanceRatio);

        TableColumn<MovingAverageTableEntity, String> time1 = new TableColumn<>("Время");
        time1.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<MovingAverageTableEntity, String> price1 = new TableColumn<>("Цена");
        price1.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<MovingAverageTableEntity, String> movingAverage = new TableColumn<>("Скользящая средняя из трёх уровней");
        movingAverage.setCellValueFactory(new PropertyValueFactory<>("movingAverage"));

        table2.getColumns().addAll(time1, price1, movingAverage);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 1000);

        initApp();

        TextField fileField = new TextField();
        Button fileButton = new Button("Выбрать файл");

        Button okButton = new Button("Ок");
        okButton.setOnAction(actionEvent -> {
            try (Scanner sc = new Scanner(new FileReader(fileField.getText()))) {
                XYChart.Series series = new XYChart.Series();
                XYChart.Series series2 = new XYChart.Series();
                series2.setName("Скользящая средняя из трех уровней");
                series.setName("График");
                while (sc.hasNextInt()) {
                    int time = sc.nextInt();
                    double price = sc.nextDouble();

                    int tableData2Size = tableData2.size();
                    tableData2.add(new MovingAverageTableEntity(time, price));

                    if (tableData2.size() >= 3) {
                        double movingAverage = (tableData2.get(tableData2.size() - 3).getPrice() +
                                tableData2.get(tableData2.size() - 2).getPrice() +
                                tableData2.getLast().getPrice()) / 3;
                        tableData2.get(tableData2Size - 1).setMovingAverage(movingAverage);
                        series2.getData().add(new XYChart.Data<>(tableData2.get(tableData2Size - 1).getTime(), movingAverage));
                    }

                    if (tableData.isEmpty()) {
                        tableData.add(new MainCharacteristicsTableEntity(time, price));
                    } else {
                        double chainAbsoluteGrowth = price - tableData.getLast().getPrice();
                        double basicAbsoluteGrowth = price - tableData.getFirst().getPrice();
                        double chainGrowthRates = (price / tableData.getLast().getPrice()) * 100;
                        double basicGrowthRates = (price / tableData.getFirst().getPrice()) * 100;
                        double chainGrowthRates2 = chainAbsoluteGrowth / tableData.getLast().getPrice();
                        double basicGrowthRates2 = basicAbsoluteGrowth / tableData.getFirst().getPrice();
                        double absoluteValue = 0.01 * tableData.getLast().getPrice();

                        if (tableData.size() >= 2) {
                            double relativeAcceleration = (chainGrowthRates - tableData.getLast().getChainGrowthRates());
                            double advanceRatio = chainGrowthRates / tableData.getLast().getChainGrowthRates();
                            tableData.add(new MainCharacteristicsTableEntity(time, price, chainAbsoluteGrowth, basicAbsoluteGrowth,
                                    chainGrowthRates, basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                                    absoluteValue, relativeAcceleration, advanceRatio));
                        } else {
                            tableData.add(new MainCharacteristicsTableEntity(time, price, chainAbsoluteGrowth, basicAbsoluteGrowth,
                                    chainGrowthRates, basicGrowthRates, chainGrowthRates2, basicGrowthRates2, absoluteValue));
                        }
                    }
                    series.getData().add(new XYChart.Data<>(time, price));
                }

                double sumX = timeSum();
                double sumY = priceSum();
                double sumXY = timeAndPriceSum();
                double sumSqrX = timeSqrSum();

                System.out.println(sumX + " " + sumY + " " + sumXY + " " + sumSqrX);


                double k = getK(sumX, sumY, sumXY, sumSqrX);
                double b = getB(k, sumX, sumY);

                System.out.println(k + " " + b);

                XYChart.Series series3 = new XYChart.Series();

                for (int i = 0; i < tableData2.size(); i++) {
                    series3.getData().add(new XYChart.Data<>(tableData2.get(i).getTime(), linearFunction(tableData2.get(i).getTime(), k, b)));
                }

//                chart2.getData().addAll(series2,series3);

                table2.setItems(tableData2);
                table.setItems(tableData);
                chart.getData().addAll(series, series2, series3);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        HBox fileHBox = new HBox();
        fileHBox.getChildren().addAll(fileField, fileButton, okButton);

        VBox vbox = new VBox(fileHBox, table, table2, chart, chart2);

        fileButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            fileField.setText(file.getAbsolutePath());
        });

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFocusTraversable(true);
        scrollPane.requestFocus();
        scrollPane.setMaxHeight(900);
        scrollPane.setMaxWidth(900);


        root.getChildren().addAll(scrollPane);
        stage.setTitle("Приложение для анализа и прогнозирования рынка недвижимости");
        stage.setMaxWidth(900);

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
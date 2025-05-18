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
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
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
                .mapToDouble(i -> i.getMovingAverage() * i.getTime())
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

    private double lnPrice() {
        return tableData2.stream()
                .skip(1)
                .limit(tableData2.size() - 2)
                .mapToDouble(i -> Math.log(i.getMovingAverage()))
                .sum();
    }

    private double timeLnPrice() {
        return tableData2.stream()
                .skip(1)
                .limit(tableData2.size() - 2)
                .mapToDouble(i -> i.getTime() * Math.log(i.getMovingAverage()))
                .sum();
    }

    private double exponentialFunction(double x, double a0, double a2) {
        return a0 * Math.pow(a2, x);
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
                series3.setName("Линейная функция");

                for (int i = 0; i < tableData2.size(); i++) {
                    series3.getData().add(new XYChart.Data<>(tableData2.get(i).getTime(), linearFunction(tableData2.get(i).getTime(), k, b)));
                }

                double[] variables = GaussMethod(new double[][]{
                        {tableData2.size() - 2, sumX, lnPrice()},
                        {sumX, sumSqrX, timeLnPrice()}
                });

                double a0 = Math.exp(variables[0]);
                double a1 = Math.exp(variables[1]);
                System.out.println(a0 + " " + a1);
                XYChart.Series series4 = new XYChart.Series();
                series4.setName("Показательная функция");

                for (int i = 0; i < tableData2.size(); i++) {
                    series4.getData().add(new XYChart.Data<>(tableData2.get(i).getTime(), exponentialFunction(tableData2.get(i).getTime(), a0, a1)));
                }

//                chart2.getData().addAll(series2,series3);

                table2.setItems(tableData2);
                table.setItems(tableData);
                chart.getData().addAll(series, series4);

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


    public static double determinant(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        } else if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        } else {
            double det = 0;
            for (int i = 0; i < n; i++) {
                double[][] submatrix = getSubmatrix(matrix, 0, i);
                det += Math.pow(-1, i) * matrix[0][i] * determinant(submatrix);
            }
            return det;
        }
    }


    private static double[][] getSubmatrix(double[][] matrix, int excludingRow, int excludingCol) {
        int n = matrix.length;
        double[][] submatrix = new double[n - 1][n - 1];
        int subRow = 0;
        for (int i = 0; i < n; i++) {
            if (i == excludingRow) continue;
            int subCol = 0;
            for (int j = 0; j < n; j++) {
                if (j == excludingCol) continue;
                submatrix[subRow][subCol] = matrix[i][j];
                subCol++;
            }
            subRow++;
        }
        return submatrix;
    }


    private double[] SolveGauss(double[][] matrix) {
        double[] answer = new double[matrix[0].length - 1];
        AtomicBoolean noSolutionFlag = new AtomicBoolean(false);
        Arrays.fill(answer, 1);
        for (int i = matrix.length - 1; i >= 0; i--) {
            int finalI = i;
            double expression = matrix[i][matrix[0].length - 1] - (IntStream.range(i + 1, matrix[0].length - 1)
                    .mapToDouble(j -> answer[j] * matrix[finalI][j]).sum());
            if (expression == 0 && matrix[i][i] == 0) {
                answer[i] = 1;
            } else if (expression != 0 && matrix[i][i] == 0) {
                noSolutionFlag.set(true);
                break;
            } else {
                answer[i] = expression / matrix[i][i];
            }
        }
        if (noSolutionFlag.get()) {
            System.out.println("No solution found");
        } else {
            System.out.println("Ответ:");
            printVector(answer);
            return answer;
        }
        return new double[]{};
    }

    public double[] GaussMethod(double[][] matrix) {
        //double[][] newMatrix = Arrays.stream(matrix).toArray(double[][]::new);
        if (determinant(matrix) == 0) {
            System.out.println("Определитель равен 0 => уравнение не " +
                    "имеет решений или имеет бесконечно много решний");
        }
        double m;
        int r = 0;
        for (int k = 0; k < matrix[0].length; k++) {
            for (int j = r + 1; j < matrix.length; j++) {
                m = (-1) * matrix[j][k] / matrix[r][k];
                System.out.println("Коэффициент = " + m);
                for (int i = k; i < matrix.length + 1; ++i) {
                    matrix[j][i] = matrix[r][i] * m + matrix[j][i];
                }
                printMatrix(matrix);
                System.out.println();
            }
            r++;
        }
        return SolveGauss(matrix);
    }

    public static void printMatrix(double[][] matrix) {
        Arrays.stream(matrix).forEach(i -> System.out.println(Arrays.toString(i)));
    }

    public static void printVector(double[] v) {
        for (int i = 0; i < v.length; i++) {
            System.out.print("x" + (i + 1) + " = " + v[i] + "\n");
        }
    }


    public static void main(String[] args) {
        launch();
    }
}
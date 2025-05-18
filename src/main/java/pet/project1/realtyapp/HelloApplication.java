package pet.project1.realtyapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;


public class HelloApplication extends Application {
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();

    private final NumberAxis xAxis2 = new NumberAxis();
    private final NumberAxis yAxis2 = new NumberAxis();

    private final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    private final LineChart<Number, Number> chart2 = new LineChart<>(xAxis2, yAxis2);

    private final TableView<MainCharacteristicsTableEntity> mainCharacteristicTable = new TableView<>();
    private final TableView<MovingAverageTableEntity> movingAverageTable = new TableView<>();

    private final ObservableList<MainCharacteristicsTableEntity> mainTableData = FXCollections.observableArrayList();
    private final ObservableList<MovingAverageTableEntity> movingTableData = FXCollections.observableArrayList();

    private final TextField fileField = new TextField();


    private double timePowSum(int pow) {
        return movingTableData.stream().
                skip(1).
                limit(movingTableData.size() - 2).
                mapToDouble(i -> Math.pow(i.getTime(), pow)).
                sum();
    }

    private double timeSqrPriceSum() {
        return movingTableData.stream()
                .skip(1)
                .limit(movingTableData.size() - 2)
                .mapToDouble(i -> i.getMovingAverage() * Math.pow(i.getTime(), 2))
                .sum();
    }

    private double priceSum() {
        return movingTableData.stream()
                .mapToDouble(MovingAverageTableEntity::getMovingAverage)
                .sum();
    }

    private double timeAndPriceSum() {
        return movingTableData.stream()
                .skip(1)
                .limit(movingTableData.size() - 2)
                .mapToDouble(i -> i.getMovingAverage() * i.getTime())
                .sum();
    }

    private double getK(double sumX, double sumY, double sumXY, double sumSqrX) {
        return ((movingTableData.size() - 2) * sumXY - sumX * sumY) / ((movingTableData.size() - 2) * sumSqrX - sumX * sumX);
    }

    private double getB(double k, double sumX, double sumY) {
        return (sumY - k * sumX) / (movingTableData.size() - 2);
    }

    private double linearFunction(double x, double k, double b) {
        return k * x + b;
    }

    private double lnPriceSum() {
        return movingTableData.stream()
                .skip(1)
                .limit(movingTableData.size() - 2)
                .mapToDouble(i -> Math.log(i.getMovingAverage()))
                .sum();
    }

    private double timeLnPriceSum() {
        return movingTableData.stream()
                .skip(1)
                .limit(movingTableData.size() - 2)
                .mapToDouble(i -> i.getTime() * Math.log(i.getMovingAverage()))
                .sum();
    }

    private double exponentialFunction(double x, double a0, double a2) {
        return a0 * Math.pow(a2, x);
    }

    private double parabolaFunction(double x, double a0, double a1, double a2) {
        return a0 + x * a1 + x * x * a2;
    }

    private void initApp() {
        xAxis.setLabel("Время");
        yAxis.setLabel("Цена за м.кв., руб.");

        xAxis2.setLabel("Время");
        yAxis2.setLabel("Цена за м.кв., руб.");

        chart.setMinHeight(500);
        chart2.setMinHeight(500);
        mainCharacteristicTable.setMinHeight(500);
        movingAverageTable.setMinHeight(500);

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

        mainCharacteristicTable.getColumns().addAll(List.of(
                time, price, chainAbsoluteGrowth, basicAbsoluteGrowth, chainGrowthRates,
                basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                absoluteValue, relativeAcceleration, advanceRatio));

        TableColumn<MovingAverageTableEntity, String> time1 = new TableColumn<>("Время");
        time1.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<MovingAverageTableEntity, String> price1 = new TableColumn<>("Цена");
        price1.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<MovingAverageTableEntity, String> movingAverage = new TableColumn<>("Скользящая средняя из трёх уровней");
        movingAverage.setCellValueFactory(new PropertyValueFactory<>("movingAverage"));

        movingAverageTable.getColumns().addAll(List.of(time1, price1, movingAverage));
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 1000);

        initApp();

        Button fileButton = new Button("Выбрать файл");
        fileButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            fileField.setText(file.getAbsolutePath());
        });

        Button okButton = new Button("Ок");
        okButton.setOnAction(okButtonActionEvent());

        HBox fileHBox = new HBox();
        fileHBox.getChildren().addAll(fileField, fileButton, okButton);

        VBox vbox = new VBox(fileHBox, mainCharacteristicTable, movingAverageTable, chart, chart2);

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

    public EventHandler<ActionEvent> okButtonActionEvent() {
        return actionEvent -> {
            try (Scanner sc = new Scanner(new FileReader(fileField.getText()))) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
                series2.setName("Скользящая средняя из трех уровней");
                series.setName("График");
                while (sc.hasNextInt()) {
                    int time = sc.nextInt();
                    double price = sc.nextDouble();

                    int tableData2Size = movingTableData.size();
                    movingTableData.add(new MovingAverageTableEntity(time, price));

                    if (movingTableData.size() >= 3) {
                        double movingAverage = (movingTableData.get(movingTableData.size() - 3).getPrice() +
                                movingTableData.get(movingTableData.size() - 2).getPrice() +
                                movingTableData.getLast().getPrice()) / 3;
                        movingTableData.get(tableData2Size - 1).setMovingAverage(movingAverage);
                        series2.getData().add(new XYChart.Data<>(movingTableData.get(tableData2Size - 1).getTime(), movingAverage));
                    }

                    if (mainTableData.isEmpty()) {
                        mainTableData.add(new MainCharacteristicsTableEntity(time, price));
                    } else {
                        double chainAbsoluteGrowth = price - mainTableData.getLast().getPrice();
                        double basicAbsoluteGrowth = price - mainTableData.getFirst().getPrice();
                        double chainGrowthRates = (price / mainTableData.getLast().getPrice()) * 100;
                        double basicGrowthRates = (price / mainTableData.getFirst().getPrice()) * 100;
                        double chainGrowthRates2 = chainAbsoluteGrowth / mainTableData.getLast().getPrice();
                        double basicGrowthRates2 = basicAbsoluteGrowth / mainTableData.getFirst().getPrice();
                        double absoluteValue = 0.01 * mainTableData.getLast().getPrice();

                        if (mainTableData.size() >= 2) {
                            double relativeAcceleration = (chainGrowthRates - mainTableData.getLast().getChainGrowthRates());
                            double advanceRatio = chainGrowthRates / mainTableData.getLast().getChainGrowthRates();
                            mainTableData.add(new MainCharacteristicsTableEntity(time, price, chainAbsoluteGrowth, basicAbsoluteGrowth,
                                    chainGrowthRates, basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                                    absoluteValue, relativeAcceleration, advanceRatio));
                        } else {
                            mainTableData.add(new MainCharacteristicsTableEntity(time, price, chainAbsoluteGrowth, basicAbsoluteGrowth,
                                    chainGrowthRates, basicGrowthRates, chainGrowthRates2, basicGrowthRates2, absoluteValue));
                        }
                    }
                    series.getData().add(new XYChart.Data<>(time, price));
                }

                double sumX = timePowSum(1);
                double sumY = priceSum();
                double sumXY = timeAndPriceSum();
                double sumSqrX = timePowSum(2);

                System.out.println(timePowSum(1) + " " + sumY + " " + sumXY + " " + timePowSum(2));

                double k = getK(sumX, sumY, sumXY, sumSqrX);
                double b = getB(k, sumX, sumY);

                System.out.println(k + " " + b);

                XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
                series3.setName("Линейная функция");

                for (MovingAverageTableEntity movingTableDatum : movingTableData) {
                    series3.getData().add(new XYChart.Data<>(movingTableDatum.getTime(),
                            linearFunction(movingTableDatum.getTime(), k, b)));
                }

                double[] variables = GaussMethod(new double[][]{
                        {movingTableData.size() - 2, sumX, lnPriceSum()},
                        {sumX, sumSqrX, timeLnPriceSum()}
                });

                double a0 = Math.exp(variables[0]);
                double a1 = Math.exp(variables[1]);
                System.out.println(a0 + " " + a1);
                XYChart.Series<Number, Number> series4 = new XYChart.Series<>();
                series4.setName("Показательная функция");

                for (MovingAverageTableEntity movingTableDatum : movingTableData) {
                    series4.getData().add(new XYChart.Data<>(movingTableDatum.getTime(),
                            exponentialFunction(movingTableDatum.getTime(), a0, a1)));
                }

                double[] variables2 = GaussMethod(new double[][]{
                        {movingTableData.size() - 2, sumX, sumSqrX, sumY},
                        {sumX, sumSqrX, timePowSum(3), sumXY},
                        {sumSqrX, timePowSum(3), timePowSum(4), timeSqrPriceSum()}
                });

                XYChart.Series<Number, Number> series5 = new XYChart.Series<>();
                series5.setName("Порабола");

                for (MovingAverageTableEntity movingTableDatum : movingTableData) {
                    series5.getData().add(new XYChart.Data<>(movingTableDatum.getTime(),
                            parabolaFunction(movingTableDatum.getTime(), variables2[0], variables2[1], variables2[2])));
                }

//                chart2.getData().addAll(series2);

                movingAverageTable.setItems(movingTableData);
                mainCharacteristicTable.setItems(mainTableData);
                chart.getData().addAll(List.of(series, series2, series3, series4, series5));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }


    public static void main(String[] args) {
        launch();
    }
}
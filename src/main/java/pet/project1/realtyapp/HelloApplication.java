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
import pet.project1.realtyapp.entity.TableEntity;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

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

    private final ObservableList<XYChart.Data<Number, Number>> graphPoints = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<Number, Number>> moveAveragePoints = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<Number, Number>> linearFunctionPoints = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<Number, Number>> exponentialFunctionPoints = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<Number, Number>> parabolaFunctionPoints = FXCollections.observableArrayList();

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

    private BiFunction<Double, double[], Double> linearFunction() {
        return (x, coefficients) -> coefficients[0] * x + coefficients[1];
    }

    private BiFunction<Double, double[], Double> exponentialFunction() {
        return (x, coefficients) -> coefficients[0] * Math.pow(coefficients[1], x);
    }

    private BiFunction<Double, double[], Double> parabolaFunction() {
        return (x, coefficients) -> coefficients[0] + x * coefficients[1] + x * x * coefficients[2];
    }

    private void initApp() {
        xAxis.setLabel("Время");
        yAxis.setLabel("Цена за м.кв., руб.");

        xAxis2.setLabel("Время");
        yAxis2.setLabel("Цена за м.кв., руб.");

        chart.setMinHeight(500);
        chart2.setMinHeight(500);
        chart.setMinWidth(500);
        chart2.setMinWidth(500);

        movingAverageTable.prefWidthProperty().bind(mainCharacteristicTable.widthProperty());

        TableColumn<MainCharacteristicsTableEntity, String> time = new TableColumn<>("Время");
        time.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<MainCharacteristicsTableEntity, String> price = new TableColumn<>("Цена");
        price.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<MainCharacteristicsTableEntity, String> chainAbsoluteGrowth =
                new TableColumn<>("Абсолютный\nприрост\nцепной");
        chainAbsoluteGrowth.setCellValueFactory(new PropertyValueFactory<>("chainAbsoluteGrowth"));

        TableColumn<MainCharacteristicsTableEntity, String> basicAbsoluteGrowth =
                new TableColumn<>("Абсолютный\nприрост\nбазисный");
        basicAbsoluteGrowth.setCellValueFactory(new PropertyValueFactory<>("basicAbsoluteGrowth"));

        TableColumn<MainCharacteristicsTableEntity, String> chainGrowthRates =
                new TableColumn<>("Темпы\nроста\nцепные");
        chainGrowthRates.setCellValueFactory(new PropertyValueFactory<>("chainGrowthRates"));

        TableColumn<MainCharacteristicsTableEntity, String> basicGrowthRates =
                new TableColumn<>("Темпы\nроста\nбазисные");
        basicGrowthRates.setCellValueFactory(new PropertyValueFactory<>("basicGrowthRates"));

        TableColumn<MainCharacteristicsTableEntity, String> chainGrowthRates2 =
                new TableColumn<>("Темпы\nприроста\nцепные");
        chainGrowthRates2.setCellValueFactory(new PropertyValueFactory<>("chainGrowthRates2"));

        TableColumn<MainCharacteristicsTableEntity, String> basicGrowthRates2 =
                new TableColumn<>("Темпы\nприроста\nбазисные");
        basicGrowthRates2.setCellValueFactory(new PropertyValueFactory<>("basicGrowthRates2"));

        TableColumn<MainCharacteristicsTableEntity, String> absoluteValue =
                new TableColumn<>("Абсолютное\nзначение");
        absoluteValue.setCellValueFactory(new PropertyValueFactory<>("absoluteValue"));

        TableColumn<MainCharacteristicsTableEntity, String> relativeAcceleration =
                new TableColumn<>("Относительное\nускорение");
        relativeAcceleration.setCellValueFactory(new PropertyValueFactory<>("relativeAcceleration"));

        TableColumn<MainCharacteristicsTableEntity, String> advanceRatio =
                new TableColumn<>("Коэффициент\nопережения");
        advanceRatio.setCellValueFactory(new PropertyValueFactory<>("advanceRatio"));

        this.setStandartPropertyAll(time, price, chainAbsoluteGrowth, basicAbsoluteGrowth, chainGrowthRates,
                basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                absoluteValue, relativeAcceleration, advanceRatio);

        mainCharacteristicTable.getColumns().addAll(List.of(
                time, price, chainAbsoluteGrowth, basicAbsoluteGrowth, chainGrowthRates,
                basicGrowthRates, chainGrowthRates2, basicGrowthRates2,
                absoluteValue, relativeAcceleration, advanceRatio));


        TableColumn<MovingAverageTableEntity, String> time1 = new TableColumn<>("Время");
        time1.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<MovingAverageTableEntity, String> price1 = new TableColumn<>("Цена");
        price1.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<MovingAverageTableEntity, String> movingAverage = new TableColumn<>("Скользящая\nсредняя\nизтрёх\nуровней");
        movingAverage.setCellValueFactory(new PropertyValueFactory<>("movingAverage"));

        this.setStandartPropertyAll(time1, price1, movingAverage);

        movingAverageTable.getColumns().addAll(List.of(time1, price1, movingAverage));
    }

    @SafeVarargs
    private void setStandartPropertyAll(TableColumn<? extends TableEntity, String>... columns) {
        for (TableColumn<? extends TableEntity, String> column : columns) {
            column.setMinWidth(50.0);
            column.setMaxWidth(1000.0);
        }
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1920, 1080);

        initApp();

        Button fileButton = new Button("Выбрать файл");
        fileButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            String file = fileChooser.showOpenDialog(stage).getAbsolutePath();
            fileField.setText(file);
        });

        Button okButton = new Button("Ок");
        okButton.setOnAction(okButtonActionEvent());

        HBox fileHBox = new HBox();
        fileHBox.getChildren().addAll(fileField, fileButton, okButton);

        VBox vbox = new VBox(fileHBox);

        vbox.getChildren().add(addBlock(
                new Label[]{new Label("Основные характеристики")},
                mainCharacteristicTable,
                chart,
                new Label[]{new Label("Тут тоже")})
        );

        vbox.getChildren().addAll(addBlock(
                new Label[]{new Label("Скользящая средняя")},
                movingAverageTable,
                chart2,
                new Label[]{})
        );

//        vbox.setMinHeight(1920);
//        vbox.setMinWidth(1080);

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.prefWidthProperty().bind(stage.widthProperty());
        scrollPane.prefHeightProperty().bind(stage.heightProperty());
//        scrollPane.setFitToWidth(true);
//        scrollPane.setFitToHeight(true);
//        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
//        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
//        scrollPane.setFocusTraversable(true);
//        scrollPane.requestFocus();
//        scrollPane.setMinHeight(3000);
//        scrollPane.setMinWidth(3000);


        root.getChildren().addAll(scrollPane);
        stage.setTitle("Приложение для анализа и прогнозирования рынка недвижимости");


        stage.setScene(scene);
        stage.show();
    }

    public VBox addBlock(Label[] label1, TableView<? extends TableEntity> table, LineChart<Number, Number> lineChart, Label[] label2) {
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        hBox.getChildren().addAll(table, lineChart);
        vBox.getChildren().addAll(label1);
        vBox.getChildren().addAll(hBox);
        vBox.getChildren().addAll(label2);
        return vBox;
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
                        moveAveragePoints.add(new XYChart.Data<>(movingTableData.get(tableData2Size - 1).getTime(), movingAverage));
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
                    graphPoints.add(new XYChart.Data<>(time, price));
                }

                XYChart.Series<Number, Number> series = new XYChart.Series<>(graphPoints);
                XYChart.Series<Number, Number> series2 = new XYChart.Series<>(moveAveragePoints);
                series2.setName("Скользящая средняя из трех уровней");
                series.setName("График");

                double sumX = timePowSum(1);
                double sumY = priceSum();
                double sumXY = timeAndPriceSum();
                double sumSqrX = timePowSum(2);

                System.out.println(timePowSum(1) + " " + sumY + " " + sumXY + " " + timePowSum(2));

                double k = getK(sumX, sumY, sumXY, sumSqrX);
                double b = getB(k, sumX, sumY);

                System.out.println(k + " " + b);

                plot(linearFunctionPoints, linearFunction(), new double[]{k, b});
                XYChart.Series<Number, Number> series3 = new XYChart.Series<>(linearFunctionPoints);
                series3.setName("Линейная функция");

                double[] variables = GaussMethod(new double[][]{
                        {movingTableData.size() - 2, sumX, lnPriceSum()},
                        {sumX, sumSqrX, timeLnPriceSum()}
                });

                plot(exponentialFunctionPoints, exponentialFunction(),
                        Arrays.stream(variables)
                                .map(Math::exp)
                                .peek(System.out::println)
                                .toArray()
                );
                XYChart.Series<Number, Number> series4 = new XYChart.Series<>(exponentialFunctionPoints);
                series4.setName("Показательная функция");

                double[] variables2 = GaussMethod(new double[][]{
                        {movingTableData.size() - 2, sumX, sumSqrX, sumY},
                        {sumX, sumSqrX, timePowSum(3), sumXY},
                        {sumSqrX, timePowSum(3), timePowSum(4), timeSqrPriceSum()}
                });

                plot(parabolaFunctionPoints, parabolaFunction(), variables2);
                XYChart.Series<Number, Number> series5 = new XYChart.Series<>(parabolaFunctionPoints);
                series5.setName("Парабола");


                movingAverageTable.setItems(movingTableData);
                mainCharacteristicTable.setItems(mainTableData);
                chart.getData().add(series);

                XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
                chart2.getData().addAll(List.of(series1, series2));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }

    private void plot(ObservableList<XYChart.Data<Number, Number>> points,
                      BiFunction<Double, double[], Double> function,
                      double[] variables) {
        for (MovingAverageTableEntity movingTableDatum : movingTableData) {
            points.add(new XYChart.Data<>(movingTableDatum.getTime(),
                    function.apply(movingTableDatum.getTime(), variables)));
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
package pet.project1.realtyapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import pet.project1.realtyapp.entity.FunctionTableEntity;
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

    private final LineChart<Number, Number> chart =
            new LineChart<>(setAxisProperties(new NumberAxis()), setAxisProperties(new NumberAxis()));
    private final LineChart<Number, Number> chart2 =
            new LineChart<>(setAxisProperties(new NumberAxis()), setAxisProperties(new NumberAxis()));
    private final LineChart<Number, Number> chart3 =
            new LineChart<>(setAxisProperties(new NumberAxis()), setAxisProperties(new NumberAxis()));
    private final LineChart<Number, Number> chart4 =
            new LineChart<>(setAxisProperties(new NumberAxis()), setAxisProperties(new NumberAxis()));
    private final LineChart<Number, Number> chart5 =
            new LineChart<>(setAxisProperties(new NumberAxis()), setAxisProperties(new NumberAxis()));

    private final TableView<MainCharacteristicsTableEntity> mainCharacteristicTable = new TableView<>();
    private final TableView<MovingAverageTableEntity> movingAverageTable = new TableView<>();
    private final TableView<FunctionTableEntity> linearTable = new TableView<>();
    private final TableView<FunctionTableEntity> exponentialTable = new TableView<>();
    private final TableView<FunctionTableEntity> parabolaTable = new TableView<>();

    private final ObservableList<MainCharacteristicsTableEntity> mainTableData =
            FXCollections.observableArrayList();
    private final ObservableList<MovingAverageTableEntity> movingTableData =
            FXCollections.observableArrayList();
    private final ObservableList<FunctionTableEntity> linearTableData =
            FXCollections.observableArrayList();
    private final ObservableList<FunctionTableEntity> exponentialTableData =
            FXCollections.observableArrayList();
    private final ObservableList<FunctionTableEntity> parabolaTableData =
            FXCollections.observableArrayList();

    private final ObservableList<XYChart.Data<Number, Number>> graphPoints =
            FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<Number, Number>> moveAveragePoints =
            FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<Number, Number>> linearFunctionPoints
            = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<Number, Number>> exponentialFunctionPoints =
            FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<Number, Number>> parabolaFunctionPoints =
            FXCollections.observableArrayList();

    private double[] linearVar;
    private double[] exponentialVar;
    private double[] parabolaVar;

    private VBox vbox;

    private final TextField fileField = new TextField();


    private NumberAxis setAxisProperties(NumberAxis axis) {
        axis.setLabel("Время");
        axis.setLabel("Цена за м.кв., руб.");
        return axis;
    }

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
        chart.setMinSize(500, 500);
        chart2.setMinSize(500, 500);
        chart3.setMinSize(500, 500);
        chart4.setMinSize(500, 500);
        chart5.setMinSize(500, 500);

        movingAverageTable.prefWidthProperty().bind(mainCharacteristicTable.widthProperty());
        linearTable.prefWidthProperty().bind(mainCharacteristicTable.widthProperty());
        exponentialTable.prefWidthProperty().bind(mainCharacteristicTable.widthProperty());
        parabolaTable.prefWidthProperty().bind(mainCharacteristicTable.widthProperty());


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


        TableColumn<FunctionTableEntity, String> time2 = new TableColumn<>("Время");
        time2.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<FunctionTableEntity, String> price2 = new TableColumn<>("Цена");
        price2.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<FunctionTableEntity, String> lvl = new TableColumn<>("Вычисленный\nуровень");
        lvl.setCellValueFactory(new PropertyValueFactory<>("lvl"));

        TableColumn<FunctionTableEntity, String> error = new TableColumn<>("Отклонение");
        error.setCellValueFactory(new PropertyValueFactory<>("error"));

        TableColumn<FunctionTableEntity, String> time3 = new TableColumn<>("Время");
        time3.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<FunctionTableEntity, String> price3 = new TableColumn<>("Цена");
        price3.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<FunctionTableEntity, String> lvl2 = new TableColumn<>("Вычисленный\nуровень");
        lvl2.setCellValueFactory(new PropertyValueFactory<>("lvl"));

        TableColumn<FunctionTableEntity, String> error2 = new TableColumn<>("Отклонение");
        error2.setCellValueFactory(new PropertyValueFactory<>("error"));

        TableColumn<FunctionTableEntity, String> time4 = new TableColumn<>("Время");
        time4.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<FunctionTableEntity, String> price4 = new TableColumn<>("Цена");
        price4.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<FunctionTableEntity, String> lvl3 = new TableColumn<>("Вычисленный\nуровень");
        lvl3.setCellValueFactory(new PropertyValueFactory<>("lvl"));

        TableColumn<FunctionTableEntity, String> error3 = new TableColumn<>("Отклонение");
        error3.setCellValueFactory(new PropertyValueFactory<>("error"));

        linearTable.getColumns().addAll(List.of(
                time2, price2, lvl, error
        ));

        exponentialTable.getColumns().addAll(List.of(
                time3, price3, lvl2, error2
        ));

        parabolaTable.getColumns().addAll(List.of(
                time4, price4, lvl3, error3
        ));

        this.setStandartPropertyAll(time1, price1, movingAverage);

        movingAverageTable.getColumns().addAll(List.of(time1, price1, movingAverage));
    }


    @SafeVarargs
    private void setStandartPropertyAll(TableColumn<? extends TableEntity, String>... columns) {
        for (TableColumn<? extends TableEntity, String> column : columns) {
            column.setMinWidth(50.0);
            column.setMaxWidth(150.0);
        }
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1920, 1000);

        this.initApp();

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

        vbox = new VBox(fileHBox);
        vbox.setSpacing(50);
        vbox.setStyle("-fx-background-color: green;");
        vbox.setMaxWidth(Double.MAX_VALUE);
        vbox.setMaxHeight(Double.MAX_VALUE);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinWidth(1920);


        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.prefWidthProperty().bind(stage.widthProperty());
        scrollPane.prefHeightProperty().bind(stage.heightProperty());
        scrollPane.setMaxWidth(Double.MAX_VALUE);


        root.getChildren().addAll(scrollPane);

        stage.setTitle("Приложение для анализа и прогнозирования рынка недвижимости");
        stage.setScene(scene);
        stage.show();
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

                double k = getK(sumX, sumY, sumXY, sumSqrX);
                double b = getB(k, sumX, sumY);

                linearVar = new double[]{k, b};

                plot(linearFunctionPoints, linearFunction(), new double[]{k, b});
                XYChart.Series<Number, Number> series3 = new XYChart.Series<>(linearFunctionPoints);
                series3.setName("Линейная функция");

                exponentialVar = GaussMethod(new double[][]{
                        {movingTableData.size() - 2, sumX, lnPriceSum()},
                        {sumX, sumSqrX, timeLnPriceSum()}
                });

                plot(exponentialFunctionPoints, exponentialFunction(),
                        Arrays.stream(exponentialVar)
                                .map(Math::exp)
                                .peek(System.out::println)
                                .toArray()
                );
                XYChart.Series<Number, Number> series4 = new XYChart.Series<>(exponentialFunctionPoints);
                series4.setName("Показательная функция");

                parabolaVar = GaussMethod(new double[][]{
                        {movingTableData.size() - 2, sumX, sumSqrX, sumY},
                        {sumX, sumSqrX, timePowSum(3), sumXY},
                        {sumSqrX, timePowSum(3), timePowSum(4), timeSqrPriceSum()}
                });

                plot(parabolaFunctionPoints, parabolaFunction(), parabolaVar);
                XYChart.Series<Number, Number> series5 = new XYChart.Series<>(parabolaFunctionPoints);
                series5.setName("Парабола");

                for (int i = 0; i < mainTableData.size(); i++) {
                    double time = mainTableData.get(i).getTime();
                    double price = mainTableData.get(i).getPrice();

                    linearTableData.add(new FunctionTableEntity(
                            time,
                            price,
                            (double) linearFunctionPoints.get(i).getYValue(),
                            Math.pow((price - (double) linearFunctionPoints.get(i).getYValue()), 2))
                    );

                    exponentialTableData.add(new FunctionTableEntity(
                            time,
                            price,
                            (double) exponentialFunctionPoints.get(i).getYValue(),
                            Math.pow((price - (double) exponentialFunctionPoints.get(i).getYValue()), 2))
                    );

                    parabolaTableData.add(new FunctionTableEntity(
                            time,
                            price,
                            (double) parabolaFunctionPoints.get(i).getYValue(),
                            Math.pow((price - (double) parabolaFunctionPoints.get(i).getYValue()), 2))
                    );

                }

                movingAverageTable.setItems(movingTableData);
                mainCharacteristicTable.setItems(mainTableData);
                linearTable.setItems(linearTableData);
                exponentialTable.setItems(exponentialTableData);
                parabolaTable.setItems(parabolaTableData);

                chart.getData().add(series);
                chart2.getData().addAll(List.of(
                        new XYChart.Series<>(graphPoints),
                        series2
                ));

                chart3.getData().addAll(List.of(
                        new XYChart.Series<>(graphPoints),
                        new XYChart.Series<>(moveAveragePoints),
                        series3
                ));

                chart4.getData().addAll(List.of(
                        new XYChart.Series<>(graphPoints),
                        new XYChart.Series<>(moveAveragePoints),
                        series4
                ));

                chart5.getData().addAll(List.of(
                        new XYChart.Series<>(graphPoints),
                        new XYChart.Series<>(moveAveragePoints),
                        series5
                ));


                vbox.getChildren().add(addBlock(
                        new Label[]{
                                setLabelProperties(new Label("Основные характеристики"), 20, Pos.CENTER)
                        },
                        mainCharacteristicTable,
                        chart,
                        new Label[]{}
                ));

                double averageRowLevel = 0;
                double averageAbsoluteGrowth =
                        (mainTableData.getLast().getPrice() - mainTableData.getFirst().getPrice())
                                / (mainTableData.size() - 1);
                double averageGrowthRate = Math.pow(
                        mainTableData.getLast().getPrice() / mainTableData.getFirst().getPrice(),
                        1.0 / mainTableData.size()
                );

                for (MainCharacteristicsTableEntity data : mainTableData) {
                    averageRowLevel += data.getPrice();
                }
                averageRowLevel /= mainTableData.size();

                double finalAverageRowLevel = averageRowLevel;
                double variance = Math.sqrt(
                        mainTableData.stream()
                                .mapToDouble(i -> Math.pow(i.getPrice() - finalAverageRowLevel, 2))
                                .sum()
                                / (mainTableData.size() - 1)
                );

                vbox.getChildren().addAll(
                        setLabelProperties(
                                new Label("Средние показатели"),
                                20,
                                Pos.CENTER
                        ),
                        setLabelProperties(
                                new Label(
                                        averageRowLevel + " - средний уровень ряда\n" +
                                                averageAbsoluteGrowth + " - средний абсолютный прирост\n" +
                                                averageGrowthRate + " - средний темп роста\n" +
                                                variance + " - среднее квадратическое отклонение"),
                                15,
                                Pos.CENTER
                        )
                );

                int index1 = (mainTableData.size() / 2);
                int index2 = mainTableData.size();

                //System.out.println(index1 + " " + index2 + " indexes");

                double middleRowLevel1 = getMiddleRowLevel(0, index1);
                double middleRowLevel2 = getMiddleRowLevel(index1, index2);

                double varianceLevel1 = getVariance(0, index1, middleRowLevel1);
                double varianceLevel2 = getVariance(index1, index2, middleRowLevel2);

                double F = Double.max(varianceLevel1, varianceLevel2) / Double.min(varianceLevel1, varianceLevel2);

                double tableF = new FDistribution(
                        index1 - 1,
                        index2 - index1 - 1
                ).inverseCumulativeProbability(1.0 - 0.05);

                double t;

                double tableT;

                String str1 = "";
                String str2 = "";

                if (F < tableF) {
                    double deviation = Math.sqrt(
                            ((varianceLevel1 * (index1 - 1)) + (varianceLevel2 * (index2 - index1 - 1)))
                                    / (index1 + (index2 - index1) - 2)
                    );

                    str1 += "вычисленный F-критерий Фишера меньше табличного, следовательно, \n" +
                            "дисперсии уровней в двух половинах ряда отличаются незначимо\n" +
                            deviation + " - среднее квадратическое отклонение\n";

                    t = Math.abs(middleRowLevel1 - middleRowLevel2)
                            / (deviation * Math.sqrt((1.0 / index1) + (1.0 / (index2 - index1))));

                    tableT = new TDistribution(index2 - 2)
                            .inverseCumulativeProbability(1.0 - 0.05 / 2);
                } else {


                    double f = ((varianceLevel1 / index1) + (varianceLevel2 / (index2 - index1)))
                            / Math.sqrt((Math.pow(varianceLevel1 / index1, 2) / (index1 + 1))
                            + (Math.pow(varianceLevel2 / (index2 - index1), 2) / (index2 - index1 + 1))
                    );
                    str1 += "вычисленный F-критерий Фишера больше табличного, следовательно, " +
                            "дисперсии уровней в двух половинах ряда отличаются значимо\n";

                    t = Math.abs(middleRowLevel1 - middleRowLevel2) /
                            Math.sqrt((1.0 / index1) + (1.0 / (index2 - index1)));

                    tableT = new TDistribution(f - 2).inverseCumulativeProbability(1.0 - 0.05 / 2);
                }

                if (t < tableT) {
                    double error =
                            new TDistribution(mainTableData.size() - 1).inverseCumulativeProbability(1.0 - 0.05 / 2)
                                    * variance
                                    * Math.sqrt(1 + (1 / (mainTableData.size() - 1.0)));

                    str2 += "вычисленный t-критерий Стьюдента меньше табличного, \n" +
                            "т.е с большей вероятностью ряд не имеет тенденции и \n" +
                            "его можно считать стационарным\n" +
                            error + " - ошибка прогноза\n" +
                            (averageRowLevel - error) + " < Y-пр < " + (averageRowLevel + error);
                } else {
                    str2 += """
                            вычисленный t-критерий Стьюдента больше табличного,\s
                            т.е ряд нельзя считать стационарным
                            """;
                }

                vbox.getChildren().addAll(
                        setLabelProperties(
                                new Label("Проверка ряда на стационарность"),
                                20,
                                Pos.CENTER),
                        setLabelProperties(
                                new Label(middleRowLevel1 + ", " + middleRowLevel2 +
                                        " - средние уровни половин ряда\n" +
                                        varianceLevel1 + ", " + varianceLevel2 + " - дисперсии половин ряда\n" +
                                        F + " - F-критерий Фишера\n" +
                                        tableF + " - F-критерий Фишера табличный\n" +
                                        str1 +
                                        t + " - t-критерий Стьюдента\n" +
                                        tableT + "- t-критерий Стьюдента табличный\n" +
                                        str2),
                                15,
                                Pos.CENTER)
                );

                vbox.getChildren().add(addBlock(
                        new Label[]{
                                setLabelProperties(new Label("Скользящая средняя"), 20, Pos.CENTER)
                        },
                        movingAverageTable,
                        chart2,
                        new Label[]{}
                ));

                vbox.getChildren().add(addBlock(
                        new Label[]{
                                setLabelProperties(new Label("Прямая"), 20, Pos.CENTER),
                                setLabelProperties(
                                        new Label("Уравнение прямой: y = " + linearVar[0] + "*t + " + linearVar[1]),
                                        15,
                                        Pos.CENTER
                                )
                        },
                        linearTable,
                        chart3,
                        new Label[]{
                                setLabelProperties(
                                        new Label(linearTableData.stream()
                                                .mapToDouble(FunctionTableEntity::getError)
                                                .sum() + " - сумма квадратов отклонений"),
                                        15,
                                        Pos.CENTER
                                )
                        }
                ));

                vbox.getChildren().add(addBlock(
                        new Label[]{
                                setLabelProperties(
                                        new Label("Показательная функция"),
                                        20,
                                        Pos.CENTER),
                                setLabelProperties(
                                        new Label("Уравнение показательной функции: y = " +
                                                exponentialVar[0] + " * " + exponentialVar[1] + "^t"),
                                        15,
                                        Pos.CENTER
                                )
                        },
                        exponentialTable,
                        chart4,
                        new Label[]{
                                setLabelProperties(
                                        new Label(exponentialTableData.stream()
                                                .mapToDouble(FunctionTableEntity::getError)
                                                .sum() + " - сумма квадратов отклонений"),
                                        15,
                                        Pos.CENTER
                                )
                        }
                ));

                vbox.getChildren().add(addBlock(
                        new Label[]{
                                setLabelProperties(
                                        new Label("Парабола"),
                                        20,
                                        Pos.CENTER),
                                setLabelProperties(
                                        new Label("Уравнение параболы: y = "
                                                + parabolaVar[0] + " + " + parabolaVar[1] +
                                                "*t + " + parabolaVar[2] + "*t^2"),
                                        15,
                                        Pos.CENTER
                                )
                        },
                        parabolaTable,
                        chart5,
                        new Label[]{
                                setLabelProperties(
                                        new Label(parabolaTableData.stream()
                                                .mapToDouble(FunctionTableEntity::getError)
                                                .sum() + " - сумма квадратов отклонений"),
                                        15,
                                        Pos.CENTER
                                )
                        }
                ));

                Group block = new Group();
                block.minHeight(500);
                vbox.getChildren().add(block);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }

    private double getVariance(int start, int end, double middleRowLevel) {
        double sum = IntStream.range(start, end)
                .mapToDouble(i -> Math.pow(mainTableData.get(i).getPrice() - middleRowLevel, 2))
                .sum();
        System.out.println(sum + " сумма дисперсия\n" + " " + (double) ((end - start) - 1));
        return sum / (double) ((end - start) - 1);
    }

    private double getMiddleRowLevel(int start, int end) {
        return IntStream.range(start, end)
                .mapToDouble(i -> mainTableData.get(i).getPrice())
                .average()
                .orElse(0);
    }

    private Label setLabelProperties(Label label, int fontSize, Pos position) {
        label.setStyle("-fx-font-size: " + fontSize + "; -fx-background-color: yellow;");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(position);
        return label;
    }

    public VBox addBlock(Label[] label1, TableView<? extends TableEntity> table,
                         LineChart<Number, Number> lineChart,
                         Label[] label2) {
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        hBox.getChildren().addAll(table, lineChart);
        vBox.getChildren().addAll(label1);
        vBox.getChildren().addAll(hBox);
        vBox.getChildren().addAll(label2);
        hBox.setAlignment(Pos.CENTER);
        return vBox;
    }


//    public static double determinant(double[][] matrix) {
//        int n = matrix.length;
//        if (n == 1) {
//            return matrix[0][0];
//        } else if (n == 2) {
//            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
//        } else {
//            double det = 0;
//            for (int i = 0; i < n; i++) {
//                double[][] submatrix = getSubmatrix(matrix, 0, i);
//                det += Math.pow(-1, i) * matrix[0][i] * determinant(submatrix);
//            }
//            return det;
//        }
//    }
//
//
//    private static double[][] getSubmatrix(double[][] matrix, int excludingRow, int excludingCol) {
//        int n = matrix.length;
//        double[][] submatrix = new double[n - 1][n - 1];
//        int subRow = 0;
//        for (int i = 0; i < n; i++) {
//            if (i == excludingRow) continue;
//            int subCol = 0;
//            for (int j = 0; j < n; j++) {
//                if (j == excludingCol) continue;
//                submatrix[subRow][subCol] = matrix[i][j];
//                subCol++;
//            }
//            subRow++;
//        }
//        return submatrix;
//    }


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
//        double[][] newMatrix = Arrays.stream(matrix).toArray(double[][]::new);
//        if (determinant(matrix) == 0) {
//            System.out.println("Определитель равен 0 => уравнение не " +
//                    "имеет решений или имеет бесконечно много решний");
//        }
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
package pet.project1.realtyapp.example;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class TableViewExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Создание TableView
        TableView<Person> table = new TableView<>();

        // 2. Создание столбцов
        TableColumn<Person, String> firstNameCol = new TableColumn<>("Имя");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Person, String> lastNameCol = new TableColumn<>("Фамилия");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // 3. Добавление столбцов в таблицу
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

        // 4. Создание данных
        ObservableList<Person> data =
                FXCollections.observableArrayList(
                        new Person("Иван", "Иванов", "ivan.ivanov@example.com"),
                        new Person("Петр", "Петров", "petr.petrov@example.com"),
                        new Person("Мария", "Сидорова", "maria.sidorova@example.com")
                );

        // 5. Установка данных в таблицу
        table.setItems(data);

        // 6. Создание Scene и отображение Stage
        Scene scene = new Scene(table, 600, 400);
        primaryStage.setTitle("TableView Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 7. Класс данных (Модель)
    public static class Person {
        private final String firstName;
        private final String lastName;
        private final String email;

        public Person(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


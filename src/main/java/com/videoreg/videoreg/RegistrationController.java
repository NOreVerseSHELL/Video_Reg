package com.videoreg.videoreg;

import com.videoreg.videoreg.model.User;
import com.videoreg.videoreg.model.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegistrationController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField ageField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    protected void onLoginClick() {
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String password = passwordField.getText();

        if (first.isEmpty() || last.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Введите имя, фамилию и пароль");
            return;
        }

        User user = UserRepository.findByCredentials(first, last, password);
        if (user != null) {
            openVideoScene(user);
        } else {
            errorLabel.setText("Пароль - должен быть не менее 8 символов, содержать заглавные и прописные буквы, цифры, спецсимволы.");
        }
    }

    @FXML
    protected void onRegisterClick() {
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String password = passwordField.getText();

        if (first.isEmpty() || last.isEmpty() || ageStr.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 120) {
                errorLabel.setText("Возраст 1..120");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Возраст должен быть числом");
            return;
        }

        if (!Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}")
                .matcher(password).matches()) {
            errorLabel.setText("Пароль слишком слабый");
            return;
        }

        User user = new User(first, last, age, password);
        boolean created = UserRepository.createUser(user);
        if (created) {
            errorLabel.setText("Регистрация успешна! Теперь войдите.");
        } else {
            errorLabel.setText("Пользователь уже существует");
        }
    }

    private void openVideoScene(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/videoreg/videoreg/video.fxml"));
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            VideoController controller = loader.getController();
            controller.setUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

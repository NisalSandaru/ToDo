package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {
    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public static String logInUserID;
    public static String loginUserName;

    public void lblCreateNewAccountOnMouseClick(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("/project1/project1/CreateNewAccountForm.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register");
        primaryStage.centerOnScreen();

    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        if (userName.trim().isEmpty()){
            txtUserName.requestFocus();
        } else if (password.trim().isEmpty()) {
            txtPassword.requestFocus();
        }else {

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE name = ? AND password = ?");
                preparedStatement.setObject(1, userName);
                preparedStatement.setObject(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {

                    logInUserID = resultSet.getString(1);
                    loginUserName = resultSet.getString(2);

                    Parent parent = FXMLLoader.load(this.getClass().getResource("/project1/project1/ToDoForm.fxml"));
                    Scene scene = new Scene(parent);
                    Stage primaryStage = (Stage) root.getScene().getWindow();
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("To Do");
                    primaryStage.centerOnScreen();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Username or Password Incorrect");
                    alert.showAndWait();

                    txtUserName.clear();
                    txtPassword.clear();
                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

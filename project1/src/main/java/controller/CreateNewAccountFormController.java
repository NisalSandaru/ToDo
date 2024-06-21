package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import static java.awt.Color.red;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label pwNotMatched1;
    public Label pwNotMatched2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblID;
    public AnchorPane root;

    public void initialize() {
        setVisibility(false);
        setDisableCommon(true);
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {

        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.equals(confirmPassword)){

            setBorderColor("transparent");
            setVisibility(false);

            register();

        }else {

            setBorderColor("red");
            setVisibility(true);

            txtNewPassword.requestFocus();

        }
    }

    public void setBorderColor(String color){

        txtNewPassword.setStyle("-fx-border-color: " + color);
        txtConfirmPassword.setStyle("-fx-border-color: " + color);
    }

    public void setVisibility(boolean isVisible){
        pwNotMatched1.setVisible(isVisible);
        pwNotMatched2.setVisible(isVisible);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setDisableCommon(false);

        txtUserName.requestFocus();
        autoGenerateID();
    }

    public void setDisableCommon(boolean isDisable){
        txtNewPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
        txtUserName.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        btnRegister.setDisable(isDisable);
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT `uid` FROM user order by uid desc LIMIT 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String userID = resultSet.getString(1);

                userID = userID.substring(1, userID.length());
                int intId = Integer.parseInt(userID);

                intId++;

                if (intId < 10){
                    lblID.setText("U00" + intId);
                } else if (intId < 100) {
                    lblID.setText("U0" + intId);
                }else {
                    lblID.setText("U" + intId);
                }

            }else {
                lblID.setText("U001");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(){

        String id = lblID.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (userName.trim().isEmpty()){
            txtUserName.requestFocus();
        }else if (email.trim().isEmpty()){
            txtEmail.requestFocus();
        }else if (password.trim().isEmpty()){
            txtNewPassword.requestFocus();
        }else if (confirmPassword.trim().isEmpty()){
            txtConfirmPassword.requestFocus();
        }else {

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `user` values (?,?,?,?)");
                preparedStatement.setObject(1, id);
                preparedStatement.setObject(2, userName);
                preparedStatement.setObject(3, email);
                preparedStatement.setObject(4, password);

                preparedStatement.executeUpdate();

                Parent parent = FXMLLoader.load(this.getClass().getResource("/project1/project1/LoginForm.fxml"));
                Scene scene = new Scene(parent);

                Stage stage = (Stage) root.getScene().getWindow();

                stage.setScene(scene);
                stage.setTitle("Login");
                stage.centerOnScreen();

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package controller;

import db.DBConnection;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import javax.swing.event.ChangeEvent;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {
    public Label lblTitle;
    public Label lblUserID;
    public AnchorPane root;
    public Pane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM> lstToDo;
    public TextField txtSelectedToDo;
    public Button btnDelete;
    public Button btnUpdate;

    public void initialize() {
        lblTitle.setText("Hi.. " + LoginFormController.loginUserName + " Welcome to ToDo List");
        lblUserID.setText(LoginFormController.logInUserID);

        subRoot.setVisible(false);

        loadList();

        setDisableCommon(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observableValue, ToDoTM oldValue, ToDoTM newValue) {

                if (lstToDo.getSelectionModel().getSelectedItem() == null) {
                    return;
                }

                setDisableCommon(false);

                subRoot.setVisible(false);

                txtSelectedToDo.setText(lstToDo.getSelectionModel().getSelectedItem().getDescription());
            }
        });
    }

    public void setDisableCommon(boolean isDisable){
        txtSelectedToDo.setDisable(isDisable);
        btnDelete.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);

        txtSelectedToDo.clear();
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want to Log Out..?" , ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("/project1/project1/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.centerOnScreen();
        }
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {

        lstToDo.getSelectionModel().clearSelection();

        setDisableCommon(true);

        subRoot.setVisible(true);

        txtDescription.requestFocus();

    }

    public void btnAddtoListOnAction(ActionEvent actionEvent) {
        String id = autoGenerateID();
        String description = txtDescription.getText();
        String userId = lblUserID.getText();

        if (description.trim().isEmpty()){
            txtDescription.requestFocus();
        }else {

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `todo` values (?,?,?)");
                preparedStatement.setObject(1, id);
                preparedStatement.setObject(2, description);
                preparedStatement.setObject(3, userId);

                preparedStatement.executeUpdate();

                txtDescription.clear();
                subRoot.setVisible(false);

                loadList();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        String id = "";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT `id` FROM todo order by id desc LIMIT 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String todoID = resultSet.getString(1);

                todoID = todoID.substring(1, todoID.length());
                int intId = Integer.parseInt(todoID);

                intId++;

                if (intId < 10){
                    id = "T00" + intId;
                } else if (intId < 100) {
                    id = "T0" + intId;
                }else {
                    id = "T" + intId;
                }

            }else {
                id = "T001";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public void loadList(){
        ObservableList<ToDoTM> todos = lstToDo.getItems();
        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `todo` WHERE `user_id` = ?");
            preparedStatement.setObject(1, lblUserID.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String userId = resultSet.getString(3);

                todos.add(new ToDoTM(id, description, userId));

            }

            lstToDo.refresh();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want to Delete this ToDo..?" , ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){

            String id = lstToDo.getSelectionModel().getSelectedItem().getId();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `todo` WHERE `id` = ?");
                preparedStatement.setObject(1, id);
                preparedStatement.executeUpdate();
                loadList();
                setDisableCommon(true);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedToDo.getText();
        String id = lstToDo.getSelectionModel().getSelectedItem().getId();

        if (description.trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please Enter Description...", ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
        }else {

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `todo` set description = ? where id = ?");
                preparedStatement.setObject(1, description);
                preparedStatement.setObject(2, id);
                preparedStatement.executeUpdate();
                loadList();
                setDisableCommon(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

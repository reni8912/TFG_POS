/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Code;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;

public class ServiceController implements Initializable {

    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    private int getUserIdByEmail(String email) throws SQLException {
        String query = "SELECT id FROM user_s WHERE email = ?";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, email);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        return 0;
    }

    @FXML
    private TextField C_name;

    @FXML
    private TextField C_price;

    @FXML
    private Button back;

    @FXML
    private TextField money;

    @FXML
    private ComboBox<String> choser;

    @FXML
    private TextField name;

    @FXML
    private Button create;

    @FXML
    private Button delete;

    @FXML
    private Button save;

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "Admin";
    private static final String pass = "1234";
    private static final String urll = "jdbc:mysql://localhost:3306/pos";
    private java.sql.Connection con;

    @FXML
    void save(ActionEvent event) throws IOException {
        String serviceName = choser.getValue();
        String newName = name.getText();
        float newPrice = Float.parseFloat(money.getText());

        try {
            String query = "UPDATE service SET name = ?, price = ? WHERE name = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, newName);
            statement.setFloat(2, newPrice);
            statement.setString(3, serviceName);
            statement.executeUpdate();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ServiceFXML.fxml"));
            Parent root = loader.load();
            ServiceController serviceController = loader.getController();
            serviceController.setEmail(email);
            serviceController.loadData();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            stage.setFullScreen(false);
            stage.setResizable(false);
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @FXML
    void delete(ActionEvent event) throws IOException {
        String serviceName = choser.getValue();

        try {
            String query = "DELETE FROM service WHERE name = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, serviceName);
            statement.executeUpdate();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ServiceFXML.fxml"));
            Parent root = loader.load();
            ServiceController serviceController = loader.getController();
            serviceController.setEmail(email);
            serviceController.loadData();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            stage.setFullScreen(false);
            stage.setResizable(false);
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.show();

            choser.getItems().remove(serviceName);

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @FXML
    void create(ActionEvent event) throws IOException {
        String name = C_name.getText();
        float price = Float.parseFloat(C_price.getText());

        try {

            int userId = getUserIdByEmail(email);

            String query = "INSERT INTO service (name, price, user_s_id) VALUES (?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, name);
            statement.setFloat(2, price);
            statement.setInt(3, userId);
            statement.executeUpdate();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ServiceFXML.fxml"));
            Parent root = loader.load();
            ServiceController serviceController = loader.getController();
            serviceController.setEmail(email);
            serviceController.loadData();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            stage.setFullScreen(false);
            stage.setResizable(false);
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @FXML
    void back(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("HubFXML.fxml"));
        Parent root = loader.load();
        HubController hubController = loader.getController();
        hubController.setEmail(email);
        hubController.loadData();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.show();

    }

    public void loadData() {
        List<String> userNames = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            System.out.println(email);
            ResultSet rs = stmt.executeQuery("SELECT service.name FROM service JOIN user_s ON user_s.id = service.user_s_id WHERE user_s.email = '" + email + "'");
            while (rs.next()) {
                userNames.add(rs.getString("service.name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        choser.setItems(FXCollections.observableArrayList(userNames));
        choser.setValue(userNames.get(0));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(urll, user, pass);
            if (con != null) {
                System.out.println("Conexión exitosa");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al intentar conectarse a la base de datos");
        }

        choser.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    String serviceName = newValue.toString();
                    PreparedStatement ps = con.prepareStatement("SELECT price, name FROM service WHERE name = ?");
                    ps.setString(1, serviceName);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        float price = rs.getFloat("price");
                        String name2 = rs.getString("name");
                        money.setText(Float.toString(price));
                        name.setText(name2);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

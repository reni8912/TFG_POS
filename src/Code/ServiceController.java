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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import java.util.Optional;


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
    
  boolean error = false;

@FXML
void save(ActionEvent event) {
    String serviceName = choser.getValue();
    String newName = name.getText();
    float newPrice;

    try {
        newPrice = Float.parseFloat(money.getText());
    } catch (NumberFormatException e) {
        error = true;
        showAlert(AlertType.ERROR, "Error de formato", "El precio ingresado no es válido.");
        return;
    }

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

        showAlert(AlertType.INFORMATION, "Guardado exitoso", "El servicio se ha guardado correctamente.");

    } catch (SQLException e) {
        error = true;
        showAlert(AlertType.ERROR, "Error al guardar", "Se ha producido un error al guardar el servicio.");
    } catch (IOException ex) {
        error = true;
        showAlert(AlertType.ERROR, "Error al guardar", "Se ha producido un error al guardar el servicio.");
    } finally {
        if (error) {
            showAlert(AlertType.ERROR, "Error al guardar", "Se ha producido un error al guardar el servicio.");
        }
    }
}




private void showAlert(AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

  @FXML
void delete(ActionEvent event) throws IOException {
    String serviceName = choser.getValue();

    // Mostrar una alerta de confirmación
    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmationAlert.setTitle("Confirmación de eliminación");
    confirmationAlert.setHeaderText(null);
    confirmationAlert.setContentText("¿Estás seguro de borrar esta tarifa?");

    Optional<ButtonType> result = confirmationAlert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
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

            showAlert(Alert.AlertType.INFORMATION, "Borrado exitoso", "El servicio se ha borrado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error al borrar", "Se ha producido un error al borrar el servicio.");
        }
    }
}
   @FXML
void create(ActionEvent event) {
    String name = C_name.getText();
    float price;

    try {
        price = Float.parseFloat(C_price.getText());
    } catch (NumberFormatException e) {
        showAlert(AlertType.ERROR, "Error de formato", "El precio ingresado no es válido.");
        return; // Salir del método para evitar la ejecución del código restante
    }

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

        showAlert(AlertType.INFORMATION, "Creado exitosamente", "El servicio se ha creado correctamente.");

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(AlertType.ERROR, "Error al crear", "Se ha producido un error al crear el servicio.");
    } catch (IOException ex) {
        ex.printStackTrace();
        showAlert(AlertType.ERROR, "Error al crear", "Se ha producido un error al crear el servicio.");
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
        if (!userNames.isEmpty()) {
            choser.setValue(userNames.get(0));
        }
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
                    } else {
                        // No se encontraron valores en la base de datos
                        money.setText("");
                        name.setText("");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

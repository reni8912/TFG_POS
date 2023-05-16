/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package Code;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.PreparedStatement;


public class LoginController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private PasswordField contraseñaL;

    @FXML
    private TextField correoL;

    @FXML
    private Button login;
    
     private String correo;
    
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "Admin";
    private static final String pass = "1234";
    private static final String urll = "jdbc:mysql://localhost:3306/pos";
    private java.sql.Connection con;
    
@FXML
private void handleButtonAction(ActionEvent event) throws IOException {
     correo = correoL.getText();
    String contraseña = contraseñaL.getText();

    try {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM user_s WHERE email = ?");
        ps.setString(1, correo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String contraseñaBD = rs.getString("password");
            if (contraseña.equals(contraseñaBD)) {
                // La contraseña es correcta, por lo que se carga la interfaz de usuario del Hub
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HubFXML.fxml"));
                Parent root = loader.load();
                HubController hubController = loader.getController();
                hubController.setEmail(correo);  
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
            } else {
                // La contraseña es incorrecta, se muestra un mensaje de error
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error de inicio de sesión");
                alert.setHeaderText(null);
                alert.setContentText("La contraseña es incorrecta. Inténtelo de nuevo.");
                alert.showAndWait();
            }
        } else {
            // No se encontró el correo electrónico en la base de datos, se muestra un mensaje de error
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de inicio de sesión");
            alert.setHeaderText(null);
            alert.setContentText("No se encontró el correo electrónico. Inténtelo de nuevo.");
            alert.showAndWait();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error de base de datos");
        alert.setHeaderText(null);
        alert.setContentText("Error al intentar iniciar sesión. Inténtelo de nuevo más tarde.");
        alert.showAndWait();
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
    try {

        Statement stmt = con.createStatement();
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM user_s");
        
        while (rs.next()) {
            
            
            
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }    
    
}



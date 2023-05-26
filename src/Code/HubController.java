/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Code;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import com.mysql.jdbc.*;
import com.sun.jdi.connect.spi.Connection;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import java.sql.*;
import javafx.scene.control.ComboBox;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.awt.Insets;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javafx.scene.control.CheckBox;

/**
 * FXML Controller class
 *
 * @author Jorge
 */
public class HubController implements Initializable {

    @FXML
    private ComboBox<String> choser;

    @FXML
    private Button create;
    
    @FXML
    private CheckBox admin;
    
    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    private BufferedImage generateQRCode(String content, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();

        BufferedImage qrImage = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
        qrImage.createGraphics();

        for (int x = 0; x < matrixWidth; x++) {
            for (int y = 0; y < matrixHeight; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return qrImage;
    }

    @FXML
    private Label label;

    @FXML
    private TextField money;

    @FXML
    private Button qr;

    @FXML
    private Button seo;

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "Admin";
    private static final String pass = "1234";
    private static final String urll = "jdbc:mysql://localhost:3306/pos";
    private java.sql.Connection con;
    Argon2 argon2 = Argon2Factory.create();

    @FXML
public void mode(ActionEvent event) throws IOException {

    if(admin.isSelected() == true){
    Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle("Verificar Usuario");
    dialog.setHeaderText(null);

    // Establecer los botones de aceptar y cancelar
    ButtonType loginButtonType = new ButtonType("Acceder", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Crear los campos de entrada
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    TextField emailField = new TextField();
    emailField.setPromptText("Correo electrónico");
    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Contraseña");

    grid.add(new Label("Correo:"), 0, 0);
    grid.add(emailField, 1, 0);
    grid.add(new Label("Contraseña:"), 0, 1);
    grid.add(passwordField, 1, 1);

    // Habilitar o deshabilitar el botón de inicio de sesión según los campos de entrada
    Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
    loginButton.setDisable(true);

    emailField.textProperty().addListener((observable, oldValue, newValue) -> {
        loginButton.setDisable(newValue.trim().isEmpty() || passwordField.getText().isEmpty());
    });

    passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
        loginButton.setDisable(newValue.trim().isEmpty() || emailField.getText().isEmpty());
    });

    dialog.getDialogPane().setContent(grid);

    // Convertir el resultado del cuadro de diálogo a un par de correo y contraseña
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == loginButtonType) {
            return new Pair<>(emailField.getText(), passwordField.getText());
        }
        return null;
    });

    Optional<Pair<String, String>> result = dialog.showAndWait();

    result.ifPresent(credentials -> {
        try {
            String em = credentials.getKey();
            String pa = credentials.getValue();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM user_s WHERE email = ?");
            ps.setString(1, em);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (argon2.verify(hashedPassword, pa)) { 

                        seo.setVisible(true);
                        create.setVisible(true);
                
                } else {
                   admin.setSelected(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Credenciales incorrectas. Inténtalo de nuevo.");
                    alert.showAndWait();
                }
            } else {
                admin.setSelected(false);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("El correo no está registrado.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            admin.setSelected(false);
        }
    });}else{
    
        seo.setVisible(false);
        create.setVisible(false);
     
    }
}
    
@FXML
public void create(ActionEvent event) throws IOException {
    // Crear el cuadro de diálogo
    Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle("Verificar Usuario");
    dialog.setHeaderText(null);

    // Establecer los botones de aceptar y cancelar
    ButtonType loginButtonType = new ButtonType("Acceder", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Crear los campos de entrada
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    TextField emailField = new TextField();
    emailField.setPromptText("Correo electrónico");
    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Contraseña");

    grid.add(new Label("Correo:"), 0, 0);
    grid.add(emailField, 1, 0);
    grid.add(new Label("Contraseña:"), 0, 1);
    grid.add(passwordField, 1, 1);

    // Habilitar o deshabilitar el botón de inicio de sesión según los campos de entrada
    Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
    loginButton.setDisable(true);

    emailField.textProperty().addListener((observable, oldValue, newValue) -> {
        loginButton.setDisable(newValue.trim().isEmpty() || passwordField.getText().isEmpty());
    });

    passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
        loginButton.setDisable(newValue.trim().isEmpty() || emailField.getText().isEmpty());
    });

    dialog.getDialogPane().setContent(grid);

    // Convertir el resultado del cuadro de diálogo a un par de correo y contraseña
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == loginButtonType) {
            return new Pair<>(emailField.getText(), passwordField.getText());
        }
        return null;
    });

    // Mostrar el cuadro de diálogo y esperar a que el usuario ingrese los datos
    Optional<Pair<String, String>> result = dialog.showAndWait();

    result.ifPresent(credentials -> {
        try {
            String em = credentials.getKey();
            String pa = credentials.getValue();

            // Verificar las credenciales ingresadas en la base de datos
            PreparedStatement ps = con.prepareStatement("SELECT * FROM user_s WHERE email = ?");
            ps.setString(1, em);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (argon2.verify(hashedPassword, pa)) {
                    // Cargar el nuevo FXML y mostrarlo en una nueva ventana
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
                } else {
                    // Mostrar un mensaje de error si las credenciales son incorrectas
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Credenciales incorrectas. Inténtalo de nuevo.");
                    alert.showAndWait();
                }
            } else {
                // Mostrar un mensaje de error si el correo no se encuentra en la base de datos
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("El correo no está registrado.");
                alert.showAndWait();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    });
}

    @FXML
    public void qr(ActionEvent event) {
        try {
            String moneyText = money.getText();
            float price = Float.parseFloat(moneyText);

            String qrContent = "https://671f-77-231-169-162.ngrok-free.app/createInvoice/"
                    + email.replaceAll("\\.", "7b6X")
                    + "/" + moneyText.replaceAll("\\.", "7b6X") + "/"
                    + choser.getValue().replaceAll("\\.", "7b6X");

            System.out.println(qrContent);
            int qrWidth = 300;
            int qrHeight = 300;

            BufferedImage qrImage = generateQRCode(qrContent, qrWidth, qrHeight);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            ImageView qrImageView = new ImageView(new Image(byteArrayInputStream));
            qrImageView.setFitWidth(qrWidth);
            qrImageView.setFitHeight(qrHeight);

            Stage qrStage = new Stage();
            qrStage.setTitle("Código QR");
            qrStage.initModality(Modality.APPLICATION_MODAL);

            VBox qrLayout = new VBox();
            qrLayout.setAlignment(Pos.CENTER);
            qrLayout.setSpacing(10);
            qrLayout.getChildren().add(qrImageView);

            Scene qrScene = new Scene(qrLayout);
            qrStage.setScene(qrScene);

            qrStage.setMinWidth(qrWidth + 40);
            qrStage.setMinHeight(qrHeight + 80);

            qrStage.showAndWait();

        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error en el campo de dinero", "Ingrese un valor numérico válido en el campo de dinero.");
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error al generar el código QR", "Se ha producido un error al generar el código QR.");
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
    void seo(ActionEvent event) throws IOException {
        // Crear el cuadro de diálogo
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Verificar Usuario");
        dialog.setHeaderText(null);

        // Establecer los botones de aceptar y cancelar
        ButtonType loginButtonType = new ButtonType("Acceder", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Crear los campos de entrada
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField emailField = new TextField();
        emailField.setPromptText("Correo electrónico");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        grid.add(new Label("Correo:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passwordField, 1, 1);

        // Habilitar o deshabilitar el botón de inicio de sesión según los campos de entrada
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || passwordField.getText().isEmpty());
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || emailField.getText().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Convertir el resultado del cuadro de diálogo a un par de correo y contraseña
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(emailField.getText(), passwordField.getText());
            }
            return null;
        });

        // Mostrar el cuadro de diálogo y esperar a que el usuario ingrese los datos
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(credentials -> {
            try {
                String em = credentials.getKey();
                String pa = credentials.getValue();

                // Verificar las credenciales ingresadas en la base de datos
                PreparedStatement ps = con.prepareStatement("SELECT * FROM user_s WHERE email = ?");
                ps.setString(1, em);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (argon2.verify(hashedPassword, pa)) {
                        // Cargar el nuevo FXML y mostrarlo en una nueva ventana
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmailFXML.fxml"));
                        Parent root = loader.load();
                        EmailController emailController = loader.getController();
                        emailController.setEmail(email);
                        emailController.loadData();
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
                        // Mostrar un mensaje de error si las credenciales son incorrectas
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Credenciales incorrectas. Inténtalo de nuevo.");
                        alert.showAndWait();
                    }
                } else {
                    // Mostrar un mensaje de error si el correo no se encuentra en la base de datos
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("El correo no está registrado.");
                    alert.showAndWait();
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void setMoney(ActionEvent event) {

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
        } else {

            choser.setValue(null);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        con = null;
        admin.setSelected(true);
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
                    PreparedStatement ps = con.prepareStatement("SELECT price FROM service WHERE name = ?");
                    ps.setString(1, serviceName);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        float price = rs.getFloat("price");
                        money.setText(Float.toString(price));
                    } else {

                        money.setText("");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}

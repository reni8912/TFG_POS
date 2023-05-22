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

    @FXML
    public void create(ActionEvent event) throws IOException {

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

    }

  @FXML
public void qr(ActionEvent event) {
    try {
        String moneyText = money.getText();
        float price = Float.parseFloat(moneyText);

        String qrContent = "https://671f-77-231-169-162.ngrok-free.app/createInvoice/" + email.replaceAll("\\.", "7b6X") + "/" + moneyText.replaceAll("\\.", "7b6X") + "/" + choser.getValue().replaceAll("\\.", "7b6X");

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

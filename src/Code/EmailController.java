/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Code;

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
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmailController implements Initializable {

    @FXML
    private Button EnvTodos;

    @FXML
    private Button back;

    @FXML
    private Button EnvUno;

    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private ComboBox<String> choser;

    @FXML
    private Label label;

    @FXML
    private TextArea textArea;

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "Admin";
    private static final String pass = "1234";
    private static final String urll = "jdbc:mysql://localhost:3306/pos";
    private java.sql.Connection con;
    private static final String API_KEY = "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";

    String host = "smtp.gmail.com";
    int puerto = 587;
    String usuario = "masterbillsmail@gmail.com";
    String contraseña = "oeloqedzbclhfnhi";

    // Propiedades de conexión
    Properties props = new Properties();

    // Autenticación de correo
    Authenticator autenticador = new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(usuario, contraseña);
        }
    };

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML
    void EnvTodos(ActionEvent event) throws AddressException {

        String premises = "";
        String type = "";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT premises_name ,type  from user_s where email = '" + email + "'");
            if (rs.next()) {
                premises = rs.getString("premises_name");
                type = rs.getString("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<InternetAddress> em = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT user_b.email FROM user_b "
                    + "JOIN invoice ON user_b.id = invoice.user_b_id "
                    + "JOIN user_s ON user_s.id = invoice.user_s_id "
                    + "WHERE user_s.email = '" + email + "'");

            while (rs.next()) {
                em.add(new InternetAddress(rs.getString("email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        OpenAiService service = new OpenAiService(API_KEY);

        CompletionRequest request = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt(textArea.getText() + ". No te inventes informacion extra ni ningún dato, pero hazlo de la forma en la cual mejore la efectividad de la comunicación y maximizar la rentabilidad. El nombre del establecimiento se llama " + premises + ", no te inventes su nombre ni lo complementes. El tipo de servicio es de " + type)
                .maxTokens(1000)
                .build();

        String choiceText = service.createCompletion(request).getChoices().get(0).getText();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.timeout", "100000");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", puerto);

        try {
            Session session = Session.getInstance(props, autenticador);

            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(usuario));
            System.out.println(choser.getValue());
            InternetAddress[] recipientAddresses = em.toArray(new InternetAddress[0]);
            mensaje.setRecipients(Message.RecipientType.TO, recipientAddresses);

            mensaje.setSubject(premises);
            mensaje.setText(choiceText);

            Transport.send(mensaje);
            showAlert(AlertType.INFORMATION, "Envio exitoso", "El correo se ha enviado correctamente.");

            System.out.println("Correo enviado exitosamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error al enviar el correo", "Se ha producido un error al enviar el correo.");

        }

    }

    @FXML
    void EnvUno(ActionEvent event) {

        String premises = "";
        String type = "";
        String name = "";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT premises_name ,type  from user_s where email = '" + email + "'");
            if (rs.next()) {
                premises = rs.getString("premises_name");
                type = rs.getString("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
          try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name  from user_b where email = '" + choser.getValue() + "'");
            if (rs.next()) {
                name = rs.getString("name");
             
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        OpenAiService service = new OpenAiService(API_KEY);
        
        String prompt = textArea.getText() + ". No te inventes informacion extra ni ningún dato, pero hazlo de la forma en la cual mejore la efectividad de la comunicación y maximizar la rentabilidad. El nombre del establecimiento se llama " + premises + ", no te inventes su nombre ni lo complementes. El tipo de servicio es de " + type + ". Esto debe de ser en formato correo electronico, y a la persona a la que te debes dirigir se llama "+ name ;
        
        CompletionRequest request = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt(prompt)
                .maxTokens(1000)
                .build();

        String choiceText = service.createCompletion(request).getChoices().get(0).getText();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.timeout", "100000");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", puerto);

        try {
            Session session = Session.getInstance(props, autenticador);

            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(usuario));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(choser.getValue()));
            mensaje.setSubject(premises);
            mensaje.setText(choiceText);

            Transport.send(mensaje);
            showAlert(AlertType.INFORMATION, "Envio exitoso", "El correo se ha enviado correctamente.");

            System.out.println("Correo enviado exitosamente.");
        } catch (MessagingException e) {
            showAlert(AlertType.ERROR, "Error al enviar el correo", "Se ha producido un error al enviar el correo.");
            e.printStackTrace();
        }

    }

    public void mostrarAlerta() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Escriba lo más detallado posible");
        alert.setHeaderText("Cómo desea que sea el correo a sus clientes");
        alert.setContentText("Esta herramienta usa ChatGpt para mejorar la efectividad de la comunicación y maximizar la rentabilidad.");

        alert.showAndWait();
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

    }

    public void loadData() {

        List<String> userNames = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            System.out.println(email);
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT user_b.email FROM user_b "
                    + "JOIN invoice ON user_b.id = invoice.user_b_id "
                    + "JOIN user_s ON user_s.id = invoice.user_s_id "
                    + "WHERE user_s.email = '" + email + "'");
            while (rs.next()) {
                userNames.add(rs.getString("user_b.email"));
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
        mostrarAlerta();
    }
}

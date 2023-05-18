/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Code;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;


public class email {
    
    public static void main(String[] args) {
        
         Argon2 argon2 = Argon2Factory.create();

        // String a encriptar
        String password = "1234";

        // Generar un hash utilizando Argon2
        String hash = argon2.hash(10, 65536, 1, password);

        // Imprimir el hash resultante
        System.out.println("Hash: " + hash);
    }
    }
    

  /*
    
    private static final String API_KEY = "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";


    public static void main(String[] args) {
    	
        OpenAiService service = new OpenAiService(API_KEY);

        CompletionRequest request = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt("De que color es el cielo?")
                .maxTokens(100)
                .build();

        String choiceText = service.createCompletion(request).getChoices().get(0).getText();

        System.out.println(choiceText);
         
    }

}
        
        
     
        
    // Configuración del servidor de correo saliente (SMTP)
    String host = "smtp.gmail.com";
    int puerto = 587;
    String usuario = "renixd6@gmail.com"; // Remplaza con tu dirección de correo
    String contraseña = "psfngdpcbckjvqpe"; // Remplaza con tu contraseña de correo

    // Propiedades de conexión
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", puerto);

    // Autenticación de correo
    Authenticator autenticador = new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(usuario, contraseña);
        }
    };

    try {
        // Crear sesión de correo
        Session sesion = Session.getInstance(props, autenticador);

        // Crear mensaje de correo
        Message mensaje = new MimeMessage(sesion);
        mensaje.setFrom(new InternetAddress(usuario));
        mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse("renixd6@gmail.com")); // Dirección de destino
        mensaje.setSubject("Correo de prueba");
        mensaje.setText("Este es un correo de prueba enviado desde Java.");

        // Enviar correo
        Transport.send(mensaje);

        System.out.println("Correo enviado exitosamente.");
    } catch (MessagingException e) {
        e.printStackTrace();
    }  */

  
    


package projekt;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import projekt.model.Model;
import projekt.view.FXMLProjektController;

/**
 *
 * @author Halacska
 */
public class Projekt extends Application { 
   
    @Override
    public void start(Stage primaryStage) throws IOException {        
        FXMLLoader loader = new
        FXMLLoader(Projekt.class.getResource("view/FXMLProjekt.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Projekt");
        primaryStage.setScene(scene);
        ((FXMLProjektController)loader.getController()).setModel(new Model());
        primaryStage.show(); 
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}


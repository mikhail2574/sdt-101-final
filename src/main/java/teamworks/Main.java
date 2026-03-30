package teamworks;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import teamworks.ui.MainView;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();

        Scene scene = new Scene(mainView.getRoot(), 1100, 700);

        if (getClass().getResource("/styles.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        }

        stage.setTitle("Pet Shop Food Management System");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
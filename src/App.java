import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        //launches the program and creates a new instance of pane organizer
//        PaneOrganizer organizer = new PaneOrganizer();
//        Scene scene = new Scene(organizer.getRoot(), Constants.APP_WIDTH, Constants.APP_HEIGHT);

//        uncomment to test out new moves
        movesDemo dem = new movesDemo();
        Scene scene = new Scene(dem.getRoot(), Constants.APP_WIDTH, Constants.APP_HEIGHT);

        stage.setScene(scene);
        stage.setTitle("Music Video Sim");
        stage.show();
    }

    public static void main(String[] argv) {
        // launch is a method inherited from Application
        launch(argv);
    }
}

package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.ChatController;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.enums.Room;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.states.GameState;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;
  private static GameStateContext context = new GameStateContext();
  private static RoomController roomController;
  private static ChatController chatController;
  private static Room currentRoom;

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException if the FXML file is not found
   */
  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml".
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  public static void setCurrentState(GameState state) {
    context.setState(state);
  }

  public static GameStateContext getGameStateContext() {
    return context;
  }

  // Note that main room will reset every visit, as it's opening the file again
  public static void openMainRoom(MouseEvent event) throws IOException {
    currentRoom = Room.MAIN_ROOM;

    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/room.fxml"));
    // Keeping this here for now, as removing it will break the code :(
    Parent root = loader.load();

    roomController = loader.getController();

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene.setRoot(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Opens the chat view and sets the profession in the chat controller.
   *
   * @param event the mouse event that triggered the method
   * @param profession the profession to set in the chat controller
   * @throws IOException if the FXML file is not found
   */
  public static void openChat(MouseEvent event, String profession) throws IOException {
    currentRoom = Room.CHAT_ROOM;
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/chat.fxml"));
    // Keeping this here for now, as removing it will break the code :(
    Parent root = loader.load();

    chatController = loader.getController();
    chatController.setProfession(profession);

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene.setRoot(root);
    stage.setScene(scene);
    stage.show();
  }

  public static void openScene(MouseEvent event, SceneState state) throws IOException {

    // Default loader on MainMenu
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/mainMenu.fxml"));

    switch (state) {
      case MAIN_MENU:
        context.setState(context.getMainMenuState());
        break;
      case START_GAME:
        openMainRoom(event);
        context.setState(context.getGameStartedState());
        context.startTimer(300); // 5 minutes
        return;
      case START_GUESSING:
        context.stopTimer(); // Stop the timer in the previous state (GameStarted)
        loader = new FXMLLoader(App.class.getResource("/fxml/guessing.fxml"));
        context.setState(context.getGuessingState());
        break;
      case END_GAME:
        loader = new FXMLLoader(App.class.getResource("/fxml/gameOver.fxml"));
        context.setState(context.getGameOverState());
        break;
    }

    loadScene(event, loader);
  }

  public static void loadScene(MouseEvent event, FXMLLoader loader) throws IOException {
    Parent root = loader.load();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene.setRoot(root);
    stage.setScene(scene);
    stage.show();
  }

  public static void updateTimer(int time) {
    switch (currentRoom) {
      case MAIN_ROOM:
        roomController.updateLabelTimer(time);
        break;
      case CHAT_ROOM:
        chatController.updateLabelTimer(time);
        break;
    }
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    Parent root = loadFxml("mainMenu");
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    root.requestFocus();
  }
}

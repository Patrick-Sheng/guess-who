package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {

  @FXML private ImageView exitImage;
  @FXML private Pane paneRoom;
  @FXML private Pane paneMap;
  @FXML private GridPane gridMap;
  @FXML private Text textMap;

  @FXML
  public void initialize() {
    paneMap.setVisible(false);
  }

  @FXML
  private void onGuess() {
    App.setRoot(SceneState.START_GUESSING);
  }

  @FXML
  private void onMap() {
    paneMap.setVisible(true);
    paneRoom.setOpacity(0.5);
  }

  @FXML
  private void onExitMap() {
    paneMap.setVisible(false);
    paneRoom.setOpacity(1);
  }

  @FXML
  private void enterMap(MouseEvent event) {
    System.out.println("enter");

    Pane hoverPane = (Pane) event.getSource();
    String paneId = hoverPane.getId();
    String text = "";

    switch (paneId) {
      case "auntieRoom":
        text = "Auntie Room";
        break;
      case "childRoom":
        text = "Child Room";
        break;
      case "gardenerRoom":
        text = "GardenerRoom";
        break;
      case "mainRoom":
        text = "Crime scene";
        break;
      default:
        text = "";
    }

    textMap.setText(text);
    exitImage.setVisible(false);
  }

  @FXML
  private void exitMap(MouseEvent event) {
    System.out.println("exit");
    textMap.setText("");
    exitImage.setVisible(true);
  }
}

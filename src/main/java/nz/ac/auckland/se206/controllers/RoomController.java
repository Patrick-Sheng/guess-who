package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
  @FXML private Button guessButton;
  @FXML private ImageView exitImage;
  @FXML private Pane paneRoom;
  @FXML private Pane paneMap;
  @FXML private GridPane gridMap;
  @FXML private Text textMap;

  @FXML
  public void initialize() {
    disableLabels();
    disableButton();
    enableButton();
    paneMap.setVisible(false);
  }

  private void disableLabels() {}

  private void disableButton() {
    guessButton.setStyle("-fx-background-color: darkred; -fx-text-fill: #222;");
    guessButton.setDisable(true);
  }

  public void enableButton() {
    guessButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
    guessButton.setDisable(false);
  }

  @FXML
  private void onMakeGuess() {
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
        text = "You are in the Crime scene";
        break;
      default:
        text = "";
    }

    textMap.setText(text);
    exitImage.setVisible(false);
  }

  @FXML
  private void exitMap() {
    System.out.println("exit");
    textMap.setText("");
    exitImage.setVisible(true);
  }

  @FXML
  private void clickRoom(MouseEvent event) {
    System.out.println("click");
    App.setRoot(SceneState.CHAT);
  }
}

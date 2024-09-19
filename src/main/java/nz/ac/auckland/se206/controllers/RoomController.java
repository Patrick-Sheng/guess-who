package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.SceneState;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController extends MapController {
  @FXML private Button guessButton;
  @FXML private Button toGuessRoomButton;
  @FXML private Pane paneTimeIsUp;
  @FXML private Pane paneMap;
  @FXML private Pane paneRoom;
  @FXML private Pane paneClue;
  @FXML private Label timerLabel;
  @FXML private ImageView bagOpen;
  @FXML private ImageView emeraldRoom;
  @FXML private ImageView letterCloseUp;
  @FXML private ImageView reportCloseUp;
  @FXML private ImageView report;

  @FXML
  public void initialize() {
    disableLabels();
    disableButton();
    enableButton();
    paneTimeIsUp.setVisible(false);
    paneMap.setVisible(false);
    paneClue.setVisible(false);
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

  public void updateLblTimer(int time, int red, int green, int blue) {
    if (time == 0) {
      App.stopTimer();
      paneTimeIsUp.setVisible(true);
    }

    int minutes = time / 60;
    int seconds = time % 60;
    timerLabel.setStyle(String.format("-fx-text-fill: rgb(%d, %d, %d);", red, green, blue));
    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
  }

  @FXML
  private void onClue(MouseEvent event) {
    ImageView clickedImageView = (ImageView) event.getSource();
    String ImageViewID = clickedImageView.getId();

    switch (ImageViewID) {
      case "bag":
        setClue(bagOpen);
        report.setVisible(true);
        break;
      case "letter":
        setClue(letterCloseUp);
        break;
      case "door":
        setClue(emeraldRoom);
        break;
      case "report":
        setClue(reportCloseUp);
        break;
    }
    paneRoom.setOpacity(0.2);
    paneClue.setVisible(true);
  }

  private void setClue(ImageView image) {
    bagOpen.setVisible(false);
    letterCloseUp.setVisible(false);
    emeraldRoom.setVisible(false);
    reportCloseUp.setVisible(false);
    report.setVisible(false);
    image.setVisible(true);
  }

  @FXML
  private void onExitClue() {
    bagOpen.setVisible(false);
    paneClue.setVisible(false);
    paneRoom.setOpacity(1);
  }

  @FXML
  private void onMakeGuess() {
    App.stopTimer();
    App.resetColour();
    App.startTimer(60);
    App.setRoot(SceneState.START_GUESSING);
  }
}

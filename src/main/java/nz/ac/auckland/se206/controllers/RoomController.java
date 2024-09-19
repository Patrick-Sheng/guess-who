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
  public Label labelMap;

  public Pane gardenerRoom;
  public Pane auntieRoom;
  public Pane childRoom;
  public Pane guessRoom;

  public ImageView emeraldRoom;

  public ImageView letterCloseUp;
  public ImageView report;
  public ImageView exitClue;

  public Button toGuessRoomButton;

  public ImageView letter;
  public ImageView door;
  public ImageView bag;

  @FXML private Button guessButton;
  @FXML private Pane paneTimeIsUp;
  @FXML private Pane paneMap;
  @FXML private Pane paneRoom;
  @FXML private Pane paneClue;
  @FXML private Label timerLabel;
  @FXML private ImageView bagOpen;

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
        bagOpen.setVisible(true);
        break;
      case "letter":
        bagOpen.setVisible(true);
        break;
      case "door":
        bagOpen.setVisible(true);
        break;
      case "report":
        bagOpen.setVisible(true);
        break;
    }
    paneRoom.setOpacity(0.2);
    paneClue.setVisible(true);
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
    App.setRoot(SceneState.START_GUESSING, App.GUESS_DIALOG);
  }
}

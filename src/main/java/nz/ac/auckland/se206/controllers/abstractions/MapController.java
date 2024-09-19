package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.speech.TextToSpeech;

public abstract class MapController extends ButtonController {
  @FXML private Text textMap;
  @FXML private ImageView exitImage;
  @FXML private Pane paneRoom;
  @FXML private Pane paneMap;
  @FXML private Button guessButton;
  @FXML private Label timerLabel;
  @FXML private Pane paneTimeIsUp;

  @FXML
  public void initialize() {
    enableButton();
    paneTimeIsUp.setVisible(false);
    paneMap.setVisible(false);
  }

  @FXML
  private void onMap() {
    TextToSpeech.speak("Opening map...");
    paneMap.setVisible(true);
    paneRoom.setOpacity(0.5);
  }

  @FXML
  private void onExitMap() {
    TextToSpeech.speak("Closing map!");
    paneMap.setVisible(false);
    paneRoom.setOpacity(1);
  }

  @FXML
  private void enterMap(MouseEvent event) {
    Pane hoverPane = (Pane) event.getSource();
    String paneId = hoverPane.getId();
    String text = switch (paneId) {
      case "auntieRoom" -> "Auntie Room";
      case "childRoom" -> "Child Room";
      case "gardenerRoom" -> "Gardener Room";
      case "mainRoom" -> "Crime scene";
      case "guessRoom" -> "Guess Room";
      default -> "";
    };

    textMap.setText(text);
    exitImage.setVisible(false);
  }

  @FXML
  private void exitMap() {
    textMap.setText("");
    exitImage.setVisible(true);
  }

  @FXML
  private void clickRoom(MouseEvent event) {
    Pane hoverPane = (Pane) event.getSource();
    String paneId = hoverPane.getId();

    switch (paneId) {
      case "auntieRoom":
        App.setRoot(SceneState.CHAT, "Going To Aunt Beatrice's Study");
        break;
      case "childRoom":
        App.setRoot(SceneState.CHAT, "Going To Sophie Baxter's Nursery");
        break;
      case "gardenerRoom":
        App.setRoot(SceneState.CHAT, "Going To Elias Greenfield's Garden");
        break;
      case "mainRoom":
        App.setRoot(SceneState.START_GAME, "Going To The Crime Scene");
        break;
      case "guessRoom":
        App.stopTimer();
        App.resetColour();
        App.startTimer(60);
        App.setRoot(SceneState.START_GUESSING, App.GUESS_DIALOG);
        break;
    }
    paneMap.setVisible(false);
    paneRoom.setOpacity(1);
  }

  private void disableButton() {
    guessButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");
    guessButton.setDisable(true);
    guessButton.setOpacity(1f);
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
  private void onMakeGuess() {
    App.stopTimer();
    App.resetColour();
    App.startTimer(60);
    App.setRoot(SceneState.START_GUESSING, App.GUESS_DIALOG);
  }
}

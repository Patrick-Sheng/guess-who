package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;
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
    checkButton();
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

  public static String EnumToName(Suspect suspect) {
    return switch (suspect) {
      case AUNT -> "Beatrice Worthington";
      case NIECE -> "Sophie Baxter";
      case GARDENER -> "Elias Greenfield";
      default -> "";
    };
  }

  @FXML
  private void exitMap() {
    textMap.setText("");
    exitImage.setVisible(true);
  }

  @FXML
  private void enterAuntie() {
    textMap.setText("Auntie Room");
    exitImage.setVisible(false);
  }

  @FXML
  private void clickAuntie() {
    launchChatWindow(Suspect.AUNT, "Going To Aunt Beatrice's Study");
  }

  @FXML
  private void enterChild() {
    textMap.setText("Child Room");
    exitImage.setVisible(false);
  }

  @FXML
  private void clickChild() {
    launchChatWindow(Suspect.NIECE, "Going To Sophie Baxter's Nursery");
  }

  @FXML
  private void clickGardener() {
    launchChatWindow(Suspect.GARDENER, "Going To Elias Greenfield's Garden");
  }

  @FXML
  private void enterGardener() {
    textMap.setText("Gardener Room");
    exitImage.setVisible(false);
  }

  private void launchChatWindow(Suspect suspect, String text) {
    GameState state = App.getGameState();
    state.setSelectedSuspect(suspect);
    App.setRoot(SceneState.CHAT, text);
    adjustPanel();
  }

  @FXML
  private void enterMain() {
    textMap.setText("Crime scene");
    exitImage.setVisible(false);
  }

  @FXML
  private void clickMain() {
    App.setRoot(SceneState.START_GAME, "Going To The Crime Scene");
    adjustPanel();
  }

  @FXML
  private void enterGuess() {
    textMap.setText("Guess Room");
    exitImage.setVisible(false);
  }

  @FXML
  private void clickGuess() {
    onMakeGuess();
    adjustPanel();
  }

  private void adjustPanel() {
    paneMap.setVisible(false);
    paneRoom.setOpacity(1);
  }

  public void disableButton() {
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
      App.getGameState().stopTimer();
      paneTimeIsUp.setVisible(true);
    }

    int minutes = time / 60;
    int seconds = time % 60;
    timerLabel.setStyle(String.format("-fx-text-fill: rgb(%d, %d, %d);", red, green, blue));
    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
  }

  @FXML
  private void onMakeGuess() {
    GameState state = App.getGameState();

    state.stopTimer();
    state.resetColour();
    state.startTimer(60);
    App.setRoot(SceneState.START_GUESSING, App.GUESS_DIALOG);
  }

  public void checkButton() {
    if (App.getGameState().checkEnableButton()) {
      enableButton();
    } else  {
      disableButton();
    }
  }
}

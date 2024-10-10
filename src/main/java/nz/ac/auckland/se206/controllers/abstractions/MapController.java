package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;
import nz.ac.auckland.se206.speech.TextToSpeech;

public abstract class MapController extends ButtonController {
  public static String enumToName(Suspect suspect) {
    // Use Enum to correspond the name of each suspect
    return switch (suspect) {
      case AUNT -> "Aunt Beatrice";
      case NIECE -> "Sophie Baxter";
      case GARDENER -> "Elias Greenfield";
      case DETECTIVE -> "Detective";
      case NARRATOR -> "Narrator";
      default -> "";
    };
  }

  public static String enumToRoom(Suspect suspect) {
    // Use Enum to correspond the room of each suspect
    return switch (suspect) {
      case AUNT -> "Study";
      case NIECE -> "Nursery";
      case GARDENER -> "Garden";
      default -> "";
    };
  }

  protected boolean timeIsUp = false;

  @FXML private Button guessButton;
  @FXML private ImageView exitImage;
  @FXML private Label timerLabel;
  @FXML private Pane paneRoom;
  @FXML private Pane paneMap;
  @FXML private Pane paneTimeIsUp;
  @FXML private Text textMap;
  @FXML private Rectangle rectFadeBackground;

  @FXML
  public void initialize() {
    checkButton();
    paneTimeIsUp.setVisible(false);
    paneMap.setVisible(false);
    rectFadeBackground.setVisible(false);
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
  private void exitMap() {
    textMap.setText("");
    exitImage.setVisible(true);
  }

  @FXML
  private void enterAuntie() {
    changeMapText(Suspect.AUNT);
  }

  @FXML
  private void clickAuntie() {
    launchChatWindow(Suspect.AUNT);
  }

  @FXML
  private void enterChild() {
    changeMapText(Suspect.NIECE);
  }

  @FXML
  private void clickChild() {
    launchChatWindow(Suspect.NIECE);
  }

  @FXML
  private void enterGardener() {
    changeMapText(Suspect.GARDENER);
  }

  @FXML
  private void clickGardener() {
    launchChatWindow(Suspect.GARDENER);
  }

  private void launchChatWindow(Suspect suspect) {
    GameState state = App.getGameState();
    state.setSelectedSuspect(suspect);

    StringBuilder builder = new StringBuilder();
    builder
        .append("Going To ")
        .append(enumToName(suspect))
        .append("'s ")
        .append(enumToRoom(suspect));

    App.setRoot(SceneState.CHAT, builder.toString());
    adjustPanel();
  }

  private void changeMapText(Suspect suspect) {
    StringBuilder builder = new StringBuilder();
    builder.append(enumToName(suspect)).append("'s ").append(enumToRoom(suspect));
    textMap.setText(builder.toString());

    exitImage.setVisible(false);
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
    textMap.setText("Guess Room" + (App.getGameState().checkEnableButton() ? "" : " (Locked)"));
    exitImage.setVisible(false);
  }

  @FXML
  private void clickGuess() {
    if (App.getGameState().checkEnableButton()) {
      onMakeGuess();
      adjustPanel();
    }
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
      GameState state = App.getGameState();
      state.stopTimer();
      timeIsUp = true;
      rectFadeBackground.setVisible(true);

      // Check if the button should be enabled or if the game should end due to time out
      if (App.getGameState().checkEnableButton()) {
        paneTimeIsUp.setVisible(true); // Show the "time is up" pane
      } else {
        state.setSuspect(Suspect.OUT_OF_TIME);
        App.setRoot(SceneState.END_GAME_LOST, "Looks like you ran out of time!"); // End the game
      }
    }

    // Calculate minutes and seconds from the total time in seconds
    int minutes = time / 60;
    int seconds = time % 60;

    // Set the text color and update the timer label with the remaining time
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
    } else {
      disableButton();
    }
  }
}

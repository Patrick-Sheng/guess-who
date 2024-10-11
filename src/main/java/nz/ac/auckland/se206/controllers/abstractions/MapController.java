package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
      case AUNT -> "Beatrice Worthington";
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

  @FXML private ImageView cluesImage;
  @FXML private ImageView exitImage;
  @FXML private ImageView guessImage;
  @FXML private ImageView suspectsImage;
  @FXML private Label cluesLabel;
  @FXML private Label guessLabel;
  @FXML private Label timerLabel;
  @FXML private Label suspectsLabel;
  @FXML private Pane paneRoom;
  @FXML private Pane paneMap;
  @FXML private Pane paneTimeIsUp;
  @FXML private Text textMap;
  @FXML private Rectangle rectFadeBackground;

  @FXML
  public void initialize() {
    isGuessReadyAndUpdate();
    paneTimeIsUp.setVisible(false);
    paneMap.setVisible(false);
    rectFadeBackground.setVisible(false);
    App.getGameState().callManualTimerUpdate();
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
    textMap.setText("Crime Scene");
    exitImage.setVisible(false);
  }

  @FXML
  private void clickMain() {
    App.setRoot(SceneState.START_GAME, "Going To The Crime Scene");
    adjustPanel();
  }

  @FXML
  private void enterGuess() {
    textMap.setText("Guess Room" + (isGuessReadyAndUpdate() ? "" : " (Locked)"));
    exitImage.setVisible(false);
  }

  @FXML
  private void clickGuess() {
    if (isGuessReadyAndUpdate()) {
      onMakeGuess();
      adjustPanel();
    }
  }

  private void adjustPanel() {
    paneMap.setVisible(false);
    paneRoom.setOpacity(1);
  }

  public void updateLblTimer(int time, int red, int green, int blue) {
    if (time == 0) {
      GameState state = App.getGameState();
      state.stopTimer();
      timeIsUp = true;
      rectFadeBackground.setVisible(true);

      // Check if the button should be enabled or if the game should end due to time out
      if (isGuessReadyAndUpdate()) {
        paneTimeIsUp.setVisible(true); // Show the "time is up" pane
      } else {
        state.setSuspect(Suspect.OUT_OF_TIME);
        App.setRoot(SceneState.END_GAME_LOST, "Looks like you ran out of time!"); // End the game
      }
    }

    // Calculate minutes and seconds from the total time in seconds
    int minutes = time / 60;
    int seconds = time % 60;

    try {
      // Set the text color and update the timer label with the remaining time
      timerLabel.setStyle(String.format("-fx-text-fill: rgb(%d, %d, %d);", red, green, blue));
      timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
    } catch (IllegalStateException e) {
      // For if the timer is called on a non-main GUI thread, just in case
    }
  }

  @FXML
  private void onMakeGuess() {
    GameState state = App.getGameState();
    state.stopTimer();
    state.resetColour();
    state.startTimer(60);
    App.setRoot(SceneState.START_GUESSING, App.GUESS_DIALOG);
  }

  public boolean isGuessReadyAndUpdate() {
    // Check current progress of game
    int objectCount = App.getGameState().getObjectCount();
    int suspectCount = App.getGameState().getSuspectCount();

    boolean hasMetSuspect = suspectCount >= 3;
    boolean hasMetObjects = objectCount > 0;
    boolean hasMetGuess = hasMetSuspect && hasMetObjects;

    // Set notifications of current progress
    Image metCriteriaImage =
        new Image(App.class.getResource("/images/buttons/checked.png").toExternalForm());
    Image notMetCriteriaImage =
        new Image(App.class.getResource("/images/buttons/unchecked.png").toExternalForm());

    suspectsLabel.setText(suspectCount + "/3 Visited Suspects");
    suspectsImage.setImage(hasMetSuspect ? metCriteriaImage : notMetCriteriaImage);

    cluesLabel.setText("Clue" + (!hasMetObjects ? " not" : "") + " visited");
    cluesImage.setImage(hasMetObjects ? metCriteriaImage : notMetCriteriaImage);

    // Set state of guess button
    metCriteriaImage =
        new Image(App.class.getResource("/images/buttons/guess.png").toExternalForm());
    notMetCriteriaImage =
        new Image(App.class.getResource("/images/buttons/no_guess.png").toExternalForm());

    guessImage.setImage(hasMetGuess ? metCriteriaImage : notMetCriteriaImage);
    guessImage.setDisable(hasMetGuess ? false : true);
    guessLabel.setText(hasMetGuess ? "Guess Suspect" : "Cannot Guess Yet");

    return hasMetGuess;
  }
}

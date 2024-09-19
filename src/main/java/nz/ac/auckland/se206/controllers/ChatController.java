package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.SceneState;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController extends MapController {
  public ImageView imageGardener;
  public ImageView imageNiece;
  public ImageView imageAunt;

  public TextArea txtaChat;

  public TextField txtInput;

  public Button btnSend;
  public Button toGuessRoomButton;

  public Pane childRoom;
  public Pane auntieRoom;
  public Pane gardenerRoom;
  public Pane guessRoom;

  public GridPane gridMap;
  public Label labelMap;

  @FXML private Pane paneTimeIsUp;
  @FXML private Pane paneMap;
  @FXML private Label timerLabel;

  @FXML
  public void initialize() {
    paneMap.setVisible(false);
    paneTimeIsUp.setVisible(false);
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

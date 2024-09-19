package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
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
}

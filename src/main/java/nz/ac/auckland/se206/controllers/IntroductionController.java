package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class IntroductionController {

  @FXML ImageView imageFamilyPhoto;
  @FXML ImageView imageEmeraldRoomBefore;
  @FXML ImageView imageEmeraldRoomAfter;
  @FXML ImageView imageDetectiveHouse;
  @FXML ImageView imageDetectiveManor;
  @FXML Label labelDescription;
  @FXML Label labelClickInstruction;
  @FXML Label labelSkipIntro;

  @FXML
  public void initialize() {
    imageEmeraldRoomBefore.setVisible(false);
    imageEmeraldRoomAfter.setVisible(false);
    imageDetectiveHouse.setVisible(false);
    imageDetectiveManor.setVisible(false);

    labelSkipIntro.setVisible(true);
    imageFamilyPhoto.setVisible(true);
    labelDescription.setText(
        "The Worthington family has gathered for a reunion. Celebrating their legacy and heralding"
            + " an exquisite emerald that has been passed down for generations.");
    labelClickInstruction.setText("Click anywhere to go next");
    sendTts("The Worthington's were celebrating");
  }

  public void sendTts(String speech) {
    TextToSpeech.stopSpeak();
    TextToSpeech.speak(speech);
  }

  @FXML
  public void nextScene() {
    if (imageFamilyPhoto.isVisible()) {
      imageFamilyPhoto.setVisible(false);
      imageEmeraldRoomBefore.setVisible(true);
      labelDescription.setText("As night falls, with the family asleep, the emerald is stolen.");
      sendTts("While night fell before them");
    } else if (imageEmeraldRoomBefore.isVisible()) {
      imageEmeraldRoomBefore.setVisible(false);
      imageEmeraldRoomAfter.setVisible(true);
      labelDescription.setText("The next morning, the family discovers the emerald is missing.");
      sendTts("Before long, their emerald had vanished");
    } else if (imageEmeraldRoomAfter.isVisible()) {
      imageEmeraldRoomAfter.setVisible(false);
      imageDetectiveHouse.setVisible(true);
      labelDescription.setText("You, a private detective received a call about the theft.");
      sendTts("They called you, a detective");
    } else if (imageDetectiveHouse.isVisible()) {
      imageDetectiveHouse.setVisible(false);
      imageDetectiveManor.setVisible(true);
      labelDescription.setText("Now, standing outside Worthington Manor, you must find the thief.");
      labelClickInstruction.setText("Click anywhere to start the game!");
      labelSkipIntro.setVisible(false);
      sendTts("Whom now must find the thief");
    } else if (imageDetectiveManor.isVisible()) {
      startGame();
    }
  }

  @FXML
  public void startGame() {
    App.setRoot(SceneState.START_GAME, "Starting game!");
    App.getGameState().startTimer(300);
  }
}

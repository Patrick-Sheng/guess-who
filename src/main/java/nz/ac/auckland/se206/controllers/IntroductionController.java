package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class IntroductionController extends MapController {

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
    labelSkipIntro.setVisible(true);

    progressImage(imageDetectiveManor, imageFamilyPhoto);

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
      progressImage(imageFamilyPhoto, imageEmeraldRoomBefore);
      labelDescription.setText("As night falls, with the family asleep, the emerald is stolen.");
      sendTts("While night fell before them");
    } else if (imageEmeraldRoomBefore.isVisible()) {
      progressImage(imageEmeraldRoomBefore, imageEmeraldRoomAfter);
      labelDescription.setText("The next morning, the family discovers the emerald is missing.");
      sendTts("Before long, their emerald had vanished");
    } else if (imageEmeraldRoomAfter.isVisible()) {
      progressImage(imageEmeraldRoomAfter, imageDetectiveHouse);
      labelDescription.setText("You, a private detective received a call about the theft.");
      sendTts("They called you, a detective");
    } else if (imageDetectiveHouse.isVisible()) {
      progressImage(imageDetectiveHouse, imageDetectiveManor);
      labelDescription.setText("Now, standing outside Worthington Manor, you must find the thief.");
      labelClickInstruction.setText("Click anywhere to start the game!");
      labelSkipIntro.setVisible(false);
      sendTts("Whom now must find the thief");
    } else if (imageDetectiveManor.isVisible()) {
      startGame();
    }
  }

  private void progressImage(ImageView oldImage, ImageView newImage) {
    oldImage.setVisible(false);

    Rectangle clip = new Rectangle(newImage.getFitWidth(), newImage.getFitHeight());
    clip.setArcWidth(40);
    clip.setArcHeight(40);
    newImage.setClip(clip);

    newImage.setVisible(true);
  }

  @FXML
  public void startGame() {
    App.setRoot(SceneState.START_GAME, "Starting game!");
  }
}

package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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

  private boolean canSkipIntro;

  /**
   * Initializes the introduction controller by setting the initial visibility of images and labels,
   * configuring the description text, and starting a thread to allow skipping the intro after a
   * delay.
   */
  @FXML
  public void initialize() {
    imageEmeraldRoomBefore.setVisible(false);
    imageEmeraldRoomAfter.setVisible(false);
    imageDetectiveHouse.setVisible(false);
    labelClickInstruction.setVisible(true);
    labelSkipIntro.setVisible(true);

    canSkipIntro = false;

    progressImage(imageDetectiveManor, imageFamilyPhoto);

    labelDescription.setText(
        "The Worthington family has gathered for a reunion. Celebrating their legacy and heralding"
            + " an exquisite emerald that has been passed down for generations.");
    labelClickInstruction.setText("Click anywhere or any key to go next");
    labelSkipIntro.setText("Click here to skip intro");
    sendTts("The Worthington's were celebrating");

    // Prevent user from spam clicking too fast
    Thread textThread =
        new Thread() {
          public void run() {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            canSkipIntro = true;
          }
        };
    textThread.setDaemon(true);
    textThread.start();
  }

  /**
   * Sends text-to-speech output for the given speech string after stopping any ongoing speech.
   *
   * @param speech the text to be converted to speech
   */
  public void sendTts(String speech) {
    TextToSpeech.stopSpeak();
    TextToSpeech.speak(speech);
  }

  /**
   * Progresses to the next scene based on the currently visible image, updating the displayed image
   * and description text while also providing appropriate text-to-speech feedback.
   */
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
      labelClickInstruction.setVisible(false);
      labelSkipIntro.setText("Click anywhere to start the game!");
      sendTts("Whom now must find the thief");
    } else if (imageDetectiveManor.isVisible()) {
      startGame();
    }
  }

  /**
   * Progresses the visibility from the old image to the new image with a rounded rectangle clip.
   *
   * @param oldImage the ImageView of the currently visible image
   * @param newImage the ImageView of the image to be displayed next
   */
  private void progressImage(ImageView oldImage, ImageView newImage) {
    oldImage.setVisible(false);

    Rectangle clip = new Rectangle(newImage.getFitWidth(), newImage.getFitHeight());
    clip.setArcWidth(40);
    clip.setArcHeight(40);
    newImage.setClip(clip);

    newImage.setVisible(true);
  }

  /** Starts the game by changing the root scene to the game state if the intro can be skipped. */
  @FXML
  public void startGame() {
    if (!canSkipIntro) {
      return;
    }
    App.setRoot(SceneState.START_GAME, "Starting game!");
  }

  /**
   * Handles key release events to progress to the next scene in the introduction.
   *
   * @param event the KeyEvent triggered by releasing a key
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    nextScene();
  }
}

package nz.ac.auckland.se206.states;

import java.io.IOException;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.CountdownTimer;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneState;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * The GameStarted state of the game. Handles the initial interactions when the game starts,
 * allowing the player to chat with characters and prepare to make a guess.
 */
public class GameStarted implements GameState {

  private final GameStateContext context;
  private CountdownTimer timer;

  /**
   * Constructs a new GameStarted state with the given game state context.
   *
   * @param context the context of the game state
   */
  public GameStarted(GameStateContext context) {
    this.context = context;
  }

  public void startTimer(int time) {
    timer = new CountdownTimer(time);
    timer.start();
  }

  public void stopTimer() {
    timer.stop();
  }

  /**
   * Handles the event when a rectangle is clicked. Depending on the clicked rectangle, it either
   * provides an introduction or transitions to the chat view.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    if (rectangleId.equals("rectGuess") || rectangleId.equals("lblGuess")) {
      App.openScene(event, SceneState.START_GUESSING);
      return;
    }

    // Transition to chat view or provide an introduction based on the clicked rectangle
    switch (rectangleId) {
      case "rectCashier":
        TextToSpeech.speak("Welcome to my cafe!");
        return;
      case "rectWaitress":
        TextToSpeech.speak("Hi, let me know when you are ready to order!");
        return;
      case "rectBackToMainRoom":
        App.openMainRoom(event);
        return;
      default:
        App.openChat(event, context.getProfession(rectangleId));
    }
  }
}

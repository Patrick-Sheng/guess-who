package nz.ac.auckland.se206.states;

import java.io.IOException;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.CountdownTimer;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneState;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * The Guessing state of the game. Handles the logic for when the player is making a guess about the
 * profession of the characters in the game.
 */
public class Guessing implements GameState {

  private final GameStateContext context;
  private String selectedPerson;
  private CountdownTimer timer;

  /**
   * Constructs a new Guessing state with the given game state context.
   *
   * @param context the context of the game state
   */
  public Guessing(GameStateContext context) {
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
   * Handles the event when a rectangle is clicked. Checks if the clicked rectangle is a customer
   * and updates the game state accordingly.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {

    switch (rectangleId) {
      case "rectPerson1":
        selectedPerson = context.getProfession(rectangleId);
        return;
      case "rectPerson2":
        selectedPerson = context.getProfession(rectangleId);
        return;
      case "rectPerson3":
        selectedPerson = context.getProfession(rectangleId);
        return;
      case "rectSubmit":
        if (selectedPerson != null) {
          if (selectedPerson.equals(context.getProfessionToGuess())) {
            TextToSpeech.speak("Correct! You won! This is the " + selectedPerson);
          } else {
            TextToSpeech.speak("You lost! This is the " + selectedPerson);
          }
          App.openScene(event, SceneState.END_GAME);
        }
    }
    return;
  }
}

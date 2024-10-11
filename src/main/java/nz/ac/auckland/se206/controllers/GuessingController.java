package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** Controller for the guessing scene. */
public class GuessingController extends ButtonController {
  @FXML private Pane paneTimeIsUp;
  @FXML private Label systemDescriptionLabel;
  @FXML private Label chosenSuspectLabel;
  @FXML private Button moveToNextScene;
  @FXML private Rectangle rectFadeBackground;

  @FXML private Rectangle rectAunt;
  @FXML private Rectangle rectGardener;
  @FXML private Rectangle rectNiece;

  @FXML private Button submitButton;

  @FXML private Label timerLabel;
  @FXML private TextArea explanationTextArea;

  private Suspect chosenSuspect;

  private boolean foundSuspect;
  private boolean foundExplanation;

  /**
   * Initializes the guessing controller by setting the visibility of UI elements, initializing
   * state variables, and adding a listener to the explanation text area.
   */
  @FXML
  public void initialize() {
    paneTimeIsUp.setVisible(false); // Hide the "time is up" pane initially
    rectFadeBackground.setVisible(false); // Hide the fade background initially
    foundSuspect = false;
    foundExplanation = false;
    disableButton(); // Disable the submit button initially
    setSelectedSuspect("Please Select");
    explanationTextArea
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // Listener to check if an explanation has been provided
              if (explanationTextArea.getText().trim().isEmpty()) {
                foundExplanation = false;
                disableButton(); // Disable the button if no explanation is provided
              } else {
                foundExplanation = true;
                if (!isNotValidResponse()) {
                  enableButton(); // Enable the button if both suspect and explanation are provided
                }
              }
            });
  }

  /** Disables the submit button and sets its style to indicate it is inactive. */
  public void disableButton() {
    submitButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");
    submitButton.setDisable(true);
    submitButton.setOpacity(1f);
  }

  /** Enables the submit button and updates its style to indicate it is active. */
  public void enableButton() {
    submitButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
    submitButton.setDisable(false);
  }

  /**
   * Updates the timer label with the remaining time and changes its color. If the time reaches
   * zero, it triggers the time-up event.
   *
   * @param time the remaining time in seconds
   * @param red the red component of the timer text color
   * @param green the green component of the timer text color
   * @param blue the blue component of the timer text color
   */
  public void updateLblTimer(int time, int red, int green, int blue) {
    if (time == 0) {
      timeIsUp(); // If time is up, trigger the time-up event
    }

    // Calculate minutes and seconds from the total time in seconds
    int minutes = time / 60;
    int seconds = time % 60;
    // Set the text color and update the timer label with the remaining time
    timerLabel.setStyle(String.format("-fx-text-fill: rgb(%d, %d, %d);", red, green, blue));
    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
  }

  /**
   * Handles the event when the time is up, stops the timer, speaks a notification to the user, and
   * updates the UI accordingly.
   */
  private void timeIsUp() {
    App.getGameState().stopTimer();

    // Speak to the user, indicating that time is up
    TextToSpeech.speak("Time is up!");

    if (isNotValidResponse()) {
      // If neither a suspect nor an explanation was provided, inform the user and prompt them to go
      // back to the main menu
      App.getGameState().setSuspect(Suspect.OUT_OF_TIME);
      App.setRoot(SceneState.END_GAME_LOST, "Looks like you ran out of time!"); // End the game
    } else {
      rectFadeBackground.setVisible(true); // Show the fade background
      paneTimeIsUp.setVisible(true);
      explanationTextArea.setEditable(false);
      // If a suspect and explanation were provided, allow the user to submit their choice
      systemDescriptionLabel.setText("Click submit to see if you are correct.");
      moveToNextScene.setText("Submit");
    }
  }

  /**
   * Checks if the user has provided a valid response (both a suspect and an explanation).
   *
   * @return true if the response is not valid; false otherwise
   */
  private boolean isNotValidResponse() {
    return !foundSuspect || !foundExplanation;
  }

  /** Handles the user's choice of the Aunt as a suspect. */
  @FXML
  private void chooseAunt() {
    runChoose(Suspect.AUNT);
  }

  /** Highlights the Aunt suspect when the user hovers over it. */
  @FXML
  private void choosingAunt() {
    runHighlight(Suspect.AUNT);
  }

  /** Handles the user's choice of the Gardener as a suspect. */
  @FXML
  private void chooseGardener() {
    runChoose(Suspect.GARDENER);
  }

  /** Highlights the Gardener suspect when the user hovers over it. */
  @FXML
  private void choosingGardener() {
    runHighlight(Suspect.GARDENER);
  }

  /** Handles the user's choice of the Niece as a suspect. */
  @FXML
  private void chooseNiece() {
    runChoose(Suspect.NIECE);
  }

  /** Highlights the Niece suspect when the user hovers over it. */
  @FXML
  private void choosingNiece() {
    runHighlight(Suspect.NIECE);
  }

  /**
   * Processes the user's selection of a suspect and enables the submit button if both a suspect and
   * an explanation have been provided.
   *
   * @param suspect the selected suspect
   */
  private void runChoose(Suspect suspect) {
    chosenSuspect = suspect;
    foundSuspect = true;
    String name = MapController.enumToName(suspect);
    setSelectedSuspect(name);
    if (!isNotValidResponse()) {
      enableButton(); // Enable the button if both suspect and explanation are provided
    }
    TextToSpeech.speak(name);
    highlightCharacterPane(suspect); // Highlight the selected suspect's image
  }

  /**
   * Highlights the selected suspect's image if no suspect has been chosen yet.
   *
   * @param suspect the suspect to highlight
   */
  private void runHighlight(Suspect suspect) {
    if (chosenSuspect == null) {
      highlightCharacterPane(suspect);
    }
  }

  /**
   * Updates the label to display the name of the selected suspect.
   *
   * @param suspectName the name of the chosen suspect
   */
  private void setSelectedSuspect(String suspectName) {
    chosenSuspectLabel.setText("Selected Suspect: " + suspectName);
  }

  /**
   * Adjusts the opacity of suspect rectangles based on the chosen suspect.
   *
   * @param suspect the suspect whose pane will be highlighted
   */
  private void highlightCharacterPane(Suspect suspect) {
    rectAunt.setOpacity(suspect == Suspect.AUNT ? 0 : 0.5);
    rectGardener.setOpacity(suspect == Suspect.GARDENER ? 0 : 0.5);
    rectNiece.setOpacity(suspect == Suspect.NIECE ? 0 : 0.5);
  }

  /** Resets the opacity of suspect rectangles when exiting the choosing state. */
  @FXML
  private void exitChoosing() {
    if (chosenSuspect == null) {
      rectAunt.setOpacity(0.5);
      rectGardener.setOpacity(0.5);
      rectNiece.setOpacity(0.5);
    }
  }

  /**
   * Submits the user's feedback (chosen suspect and explanation) and navigates to the appropriate
   * end game scene.
   */
  @FXML
  private void onSubmitFeedback() {
    App.getGameState().stopTimer();

    // Create a background task to process the feedback submission
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() {
            // Set the chosen suspect in the game state
            App.getGameState().setSuspect(chosenSuspect);

            // Navigate to the appropriate end game scene based on the chosen suspect
            switch (chosenSuspect) {
              case AUNT:
                App.setRoot(
                    SceneState.END_GAME_WON,
                    "Wow, you got it! Who knew it could have been Aunt Beatrice?");
                App.getGameState()
                    .setExplaination(
                        explanationTextArea
                            .getText()); // Set the explanation for the correct choice
                break;
              case GARDENER:
                App.setRoot(
                    SceneState.END_GAME_LOST, "Unfortunately, Gardener Elias was not the thief.");
                break;
              case NIECE:
                App.setRoot(
                    SceneState.END_GAME_LOST, "Unfortunately, Niece Sophie was not the thief.");
                break;
              default:
                return null;
            }
            return null;
          }
        };
    // Start the task in a new thread to avoid blocking the main thread
    new Thread(task).start();
  }

  /**
   * Moves to the next scene based on the user's responses. If the responses are not valid,
   * navigates back to the main menu; otherwise, submits the feedback.
   */
  @FXML
  public void onMoveToNextScene() {
    App.getGameState().stopTimer(); // Stop the game timer
    if (isNotValidResponse()) {
      App.setRoot(SceneState.MAIN_MENU, "Going back to main menu...");
    } else {
      onSubmitFeedback(); // Submit the feedback if a valid response was provided
    }
  }
}

package nz.ac.auckland.se206.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class GuessingController extends ButtonController {
  @FXML private Pane paneTimeIsUp;
  @FXML private Label systemDescriptionLabel;
  @FXML private Label chosenSuspectLabel;
  @FXML private Button moveToNextScene;

  @FXML private Rectangle rectAunt;
  @FXML private Rectangle rectGardener;
  @FXML private Rectangle rectNiece;

  @FXML private Button submitButton;

  @FXML private Label timerLabel;
  @FXML private TextArea explanationTextArea;

  private ChatCompletionRequest chatCompletionRequest;
  private Suspect chosenSuspect;

  private boolean foundSuspect;
  private boolean foundExplanation;

  @FXML
  public void initialize() {
    paneTimeIsUp.setVisible(false); // Hide the "time is up" pane initially
    setAiProxyConfig(); // Set up the AI proxy configuration
    foundSuspect = false;
    foundExplanation = false;
    disableButton(); // Disable the submit button initially
    chosenSuspectLabel.setText("");
    explanationTextArea
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // Listener to check if an explanation has been provided
              if (!foundExplanation) {
                foundExplanation = true;
                if (!isNotValidResponse()) {
                  enableButton(); // Enable the button if both suspect and explanation are provided
                }
              }
            });
  }

  public void disableButton() {
    submitButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");
    submitButton.setDisable(true);
    submitButton.setOpacity(1f);
  }

  public void enableButton() {
    submitButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
    submitButton.setDisable(false);
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    return PromptEngineering.getPrompt("feedback_instruction.txt", null);
  }

  public void setAiProxyConfig() {

    Task<Void> setGptModelTask =
        new Task<>() {
          @Override
          protected Void call() {
            try {
              // Configure the chatCompletionRequest with specific parameters
              chatCompletionRequest =
                  new ChatCompletionRequest(App.getConfig())
                      .setTemperature(0.1)
                      .setTopP(0.2)
                      .setMaxTokens(200);
              runGpt(new ChatMessage("system", getSystemPrompt()));
              // Run the GPT model with a system prompt to initialize the chat
            } catch (ApiProxyException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

    // Start the task in a new thread to avoid blocking the main thread
    new Thread(setGptModelTask).start();
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      // Runs the GPT model with a given chat message.
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      // Catch any error if there is any
      e.printStackTrace();
      return null;
    }
  }

  private void setExplanation() {
    // Create a background task to get the explanation from the GPT model
    Task<String> getExplanationTask =
        new Task<>() {
          @Override
          protected String call() {
            String message = explanationTextArea.getText().trim();
            if (!message.isEmpty()) {
              try {
                // Run the GPT model with the user's message and return the response
                ChatMessage response = runGpt(new ChatMessage("user", message));
                assert response != null;
                System.out.println(response.getContent());
                return response.getContent();
              } catch (ApiProxyException e) {
                e.printStackTrace();
              }
            }
            return null;
          }
        };

    // Update the game state with the explanation when the task succeeds
    getExplanationTask.setOnSucceeded(
        event -> {
          String explanation = getExplanationTask.getValue();
          if (explanation != null) {
            App.getGameState().updateFeedbackPrompt(explanation);
          }
        });
    // Handle task failure by printing the exception
    getExplanationTask.setOnFailed(
        event -> {
          Throwable e = getExplanationTask.getException();
          e.printStackTrace();
        });

    // Start the task in a new thread to avoid blocking the main thread
    new Thread(getExplanationTask).start();
  }

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

  private void timeIsUp() {
    App.getGameState().stopTimer();
    paneTimeIsUp.setVisible(true);
    explanationTextArea.setEditable(false);

    // Speak to the user, indicating that time is up
    TextToSpeech.speak("Time is up!");

    if (isNotValidResponse()) {
      // If neither a suspect nor an explanation was provided, inform the user and prompt them to go
      // back to the main menu
      systemDescriptionLabel.setText(
          "You did not select a suspect amd explanation within time limit. Game is now over.");
      moveToNextScene.setText("Back to Main Menu");
    } else {
      // If a suspect and explanation were provided, allow the user to submit their choice
      systemDescriptionLabel.setText("Click submit to see if you are correct.");
      moveToNextScene.setText("Submit");
    }
  }

  private boolean isNotValidResponse() {
    return !foundSuspect || !foundExplanation;
  }

  @FXML
  private void chooseAunt() {
    runChoose(Suspect.AUNT, "Aunt Beatrice");
  }

  @FXML
  private void choosingAunt() {
    runHighlight(Suspect.AUNT);
  }

  @FXML
  private void chooseGardener() {
    runChoose(Suspect.GARDENER, "Elias Greenfield");
  }

  @FXML
  private void choosingGardener() {
    runHighlight(Suspect.GARDENER);
  }

  @FXML
  private void chooseNiece() {
    runChoose(Suspect.NIECE, "Sophie Baxter");
  }

  @FXML
  private void choosingNiece() {
    runHighlight(Suspect.NIECE);
  }

  private void runChoose(Suspect suspect, String speech) {
    chosenSuspect = suspect;
    foundSuspect = true;
    chosenSuspectLabel.setText(speech);
    if (!isNotValidResponse()) {
      enableButton(); // Enable the button if both suspect and explanation are provided
    }
    TextToSpeech.speak(speech);
    highlightCharacterPane(suspect); // Highlight the selected suspect's image
  }

  private void runHighlight(Suspect suspect) {
    if (chosenSuspect == null) {
      highlightCharacterPane(suspect);
    }
  }

  private void highlightCharacterPane(Suspect suspect) {
    rectAunt.setOpacity(suspect == Suspect.AUNT ? 0 : 0.5);
    rectGardener.setOpacity(suspect == Suspect.GARDENER ? 0 : 0.5);
    rectNiece.setOpacity(suspect == Suspect.NIECE ? 0 : 0.5);
  }

  @FXML
  private void exitChoosing() {
    if (chosenSuspect == null) {
      rectAunt.setOpacity(0.5);
      rectGardener.setOpacity(0.5);
      rectNiece.setOpacity(0.5);
    }
  }

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
                    "Wow, you got it! Who knew it could have been Beatrice?");
                setExplanation(); // Set the explanation for the correct choice
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

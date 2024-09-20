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
  @FXML private Button moveToNextScene;

  @FXML private Rectangle rectAunt;
  @FXML private Rectangle rectGardener;
  @FXML private Rectangle rectNiece;

  @FXML private Label timerLabel;
  @FXML private TextArea explanationTextArea;

  private ChatCompletionRequest chatCompletionRequest;
  private Suspect chosenSuspect;

  @FXML
  public void initialize() {
    paneTimeIsUp.setVisible(false);
    setAiProxyConfig();
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
              chatCompletionRequest =
                  new ChatCompletionRequest(App.getConfig())
                      .setTemperature(0.1)
                      .setTopP(0.2)
                      .setMaxTokens(200);
              runGpt(new ChatMessage("system", getSystemPrompt()));
            } catch (ApiProxyException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

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
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void setExplanation() {
    Task<String> getExplanationTask =
        new Task<>() {
          @Override
          protected String call() {
            String message = explanationTextArea.getText().trim();
            if (!message.isEmpty()) {
              try {
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

    getExplanationTask.setOnSucceeded(
        event -> {
          String explanation = getExplanationTask.getValue();
          if (explanation != null) {
            App.getGameState().updateFeedbackPrompt(explanation);
          }
        });
    getExplanationTask.setOnFailed(
        event -> {
          Throwable e = getExplanationTask.getException();
          e.printStackTrace();
        });

    new Thread(getExplanationTask).start();
  }

  public void updateLblTimer(int time, int red, int green, int blue) {
    if (time == 0) {
      timeIsUp();
    }

    int minutes = time / 60;
    int seconds = time % 60;
    timerLabel.setStyle(String.format("-fx-text-fill: rgb(%d, %d, %d);", red, green, blue));
    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
  }

  private void timeIsUp() {
    App.getGameState().stopTimer();
    paneTimeIsUp.setVisible(true);

    TextToSpeech.speak("Time is up!");

    if (chosenSuspect == null) {
      systemDescriptionLabel.setText(
          "You did not select a suspect within time limit. Game is now over.");
      moveToNextScene.setText("Back to Main Menu");
    } else {
      systemDescriptionLabel.setText("Click submit to see if you are correct.");
      moveToNextScene.setText("Submit");
    }
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
    TextToSpeech.speak(speech);
    highlightCharacterPane(suspect);
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

    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() {
            App.getGameState().setSuspect(chosenSuspect);
            switch (chosenSuspect) {
              case AUNT:
                App.setRoot(
                    SceneState.END_GAME_WON,
                    "Wow, you got it! Who knew it could have been Beatrice?");
                setExplanation();
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

    new Thread(task).start();
  }

  @FXML
  public void onMoveToNextScene() {
    App.getGameState().stopTimer();
    if (chosenSuspect == null) {
      App.setRoot(SceneState.MAIN_MENU, "Going back to main menu...");
    } else {
      onSubmitFeedback();
    }
  }
}

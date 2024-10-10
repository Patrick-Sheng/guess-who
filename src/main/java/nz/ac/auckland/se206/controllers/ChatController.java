package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.Suspect;
import nz.ac.auckland.se206.models.InteractionLog;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.speech.TextToSpeech;
import org.fxmisc.richtext.InlineCssTextArea;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController extends MapController {

  private static boolean hasDetectiveInteraction(List<InteractionLog> logs) {
    for (InteractionLog log : logs) {
      if (log.suspect().equals(Suspect.DETECTIVE)) {
        return true;
      }
    }
    return false;
  }

  private static String enumToPrefix(Suspect suspect) {
    return switch (suspect) {
      case AUNT -> "Aunt";
      case NIECE -> "Niece";
      case GARDENER -> "Gardener";
      default -> "";
    };
  }

  private static String enumToPrompt(Suspect suspect) {
    return switch (suspect) {
      case AUNT -> "auntie";
      case NIECE -> "child";
      case GARDENER -> "gardener";
      default -> "";
    };
  }

  @FXML private Button chatButton;
  @FXML private ImageView imageGardener;
  @FXML private ImageView imageNiece;
  @FXML private ImageView imageAunt;
  @FXML private InlineCssTextArea logArea;
  @FXML private Label suspectLabel;
  @FXML private TextField userField;

  public void disableButton() {
    chatButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");
    chatButton.setDisable(true);
    chatButton.setOpacity(1f);
  }

  public void enableButton() {
    chatButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
    chatButton.setDisable(false);
  }

  @FXML
  public void initialize() {
    super.initialize();
    disableButton();
    enterUser(App.getGameState().getSelectedSuspect());

    userField
        .textProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() > 0) {
                  enableButton();
                } else {
                  disableButton();
                }
              }
            });
  }

  @FXML
  private void onEnter() {
    // Skip the interaction if the time is up
    if (timeIsUp == true) {
      return;
    }

    GameState state = App.getGameState();
    Suspect suspect = App.getGameState().getSelectedSuspect();

    // Check if this is the first interaction with the suspect
    if (state.getInteractionLogs().containsKey(suspect)) {
      if (!hasDetectiveInteraction(state.getInteractionLogs().get(suspect))) {
        increasePersonAmount(); // Increase the interaction count for this suspect
      }
    }

    String text = userField.getText();
    addMessage(text, state.getSelectedSuspect(), Suspect.DETECTIVE, true);
    userField.clear(); // Clear the input field after the text is send

    isGuessReadyAndUpdate(); // Check if any buttons need to be enabled or disabled

    addLog(
        new InteractionLog(suspect, "is thinking..."), true); // Log that the suspect is processing

    logArea.requestFollowCaret(); // Ensure the log area scrolls to the latest message
    fetchMessage(suspect);
  }

  private void fetchMessage(Suspect current) {
    // Initialize the task to fetch the voice and message.
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() {
            try {
              // Request the chat message (in text)
              GameState state = App.getGameState();

              ChatCompletionRequest request = state.getChatMessages().get(current);
              ChatCompletionResult chatCompletionResult = request.execute();

              Choice result = chatCompletionResult.getChoices().iterator().next();

              String message = result.getChatMessage().getContent();

              // Add the chat message to the related textbox.
              Platform.runLater(
                  () -> {
                    addMessage(message, current, current, true);
                    enterUser(App.getGameState().getSelectedSuspect());
                    logArea.requestFollowCaret();
                  });
            } catch (ApiProxyException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

    // Start the thread for getting the message.
    new Thread(task).start();
  }

  private void addMessage(
      String message, Suspect conversation, Suspect from, boolean newLineBefore) {
    GameState state = App.getGameState();

    InteractionLog log = new InteractionLog(from, message);
    state.getInteractionLogs().get(conversation).add(log);

    // Only update the chat log if the conversation is with the selected suspect
    if (conversation == state.getSelectedSuspect()) {
      addLog(log, newLineBefore);
    }

    // Send the message to the GPT model if the conversation is with an actor
    if (isActor(conversation)) {
      String type = "assistant";

      if (from == Suspect.NARRATOR) {
        return;
      } else if (from == Suspect.DETECTIVE) {
        type = "user";
      }

      state.getChatMessages().get(conversation).addMessage(type, message);
    }
  }

  public void enterUser(Suspect suspect) {
    // Prepare the introductory message based on the suspect
    String intro;

    switch (suspect) {
      case AUNT:
        intro = "You walk into the room where " + enumToName(suspect) + " sits calmly in a chair.";
        break;
      case NIECE:
        intro =
            "You race into the room where "
                + enumToName(suspect)
                + " sits with her bear, wide eyed.";
        break;
      case GARDENER:
        intro =
            "You peek into the manor's green house were you see "
                + enumToName(suspect)
                + " standing attentive.";
        break;
      default:
        intro = "";
        break;
    }

    startDialog(intro, suspect); // Start the dialog with the suspect

    // Set the visibility of the suspect's image
    imageGardener.setVisible(suspect == Suspect.GARDENER);
    imageNiece.setVisible(suspect == Suspect.NIECE);
    imageAunt.setVisible(suspect == Suspect.AUNT);

    // Set the label to the suspect's name
    suspectLabel.setText(enumToPrefix(suspect) + " " + enumToName(suspect));
  }

  private boolean isActor(Suspect objectType) {
    return objectType == Suspect.NIECE
        || objectType == Suspect.AUNT
        || objectType == Suspect.GARDENER;
  }

  private void addLog(InteractionLog log, boolean newLineBefore) {
    if (newLineBefore) {
      logArea.appendText("\n");
    }

    // Append the suspect's name and the message to the log area
    logArea.append(enumToName(log.suspect()) + ": ", "-fx-font-weight: bold");
    logArea.append(log.message(), "");
  }

  private void startDialog(String intro, Suspect suspect) {
    GameState state = App.getGameState();

    state.setSelectedSuspect(suspect);

    logArea.clear();

    // If the player has not yet interacted with this character.
    if (!state.getInteractionLogs().containsKey(suspect)) {
      // Stop previous character from speaking
      TextToSpeech.stopSpeak();
      TextToSpeech.speak(intro);
      state.getInteractionLogs().put(suspect, new ArrayList<>());

      // Send character description to ai to begin conversation.
      if (isActor(suspect)) {
        ChatCompletionRequest chatCompletionRequest = getChatCompletionRequest(suspect);

        state.getChatMessages().put(suspect, chatCompletionRequest);

        fetchMessage(suspect);
      }

      addMessage(intro, suspect, Suspect.NARRATOR, false);
    } else {
      // If the character has interacted with this character before.
      boolean isFirst = true;

      // Add all logs in from the previous session.
      for (InteractionLog log : state.getInteractionLogs().get(suspect)) {
        addLog(log, !isFirst);
        isFirst = false;
      }

      logArea.requestFollowCaret();
    }

    // Disable the textbox if the character/object is not interactable.
    if (isActor(suspect)) {
      userField.setDisable(false);
    } else {
      userField.setDisable(true);

      if (suspect != Suspect.NARRATOR && suspect != Suspect.DETECTIVE) {
        state.increaseObjects();
      }
    }

    // Re/set components to current state
    userField.clear();
    isGuessReadyAndUpdate();
  }

  private void increasePersonAmount() {
    App.getGameState().increasePeople();
    isGuessReadyAndUpdate();
  }

  private ChatCompletionRequest getChatCompletionRequest(Suspect suspect) {
    ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest(App.getConfig());

    // Base prompt for all AI agents.
    String s1 = PromptEngineering.getPrompt("chat.txt", new HashMap<>());
    String s2 = PromptEngineering.getPrompt(enumToPrompt(suspect) + ".txt", new HashMap<>());

    String s = s1 + s2;

    // Set up AI properties and send base prompt.
    chatCompletionRequest.addMessage("system", s);

    chatCompletionRequest.setN(1);
    chatCompletionRequest.setTemperature(1.5);
    chatCompletionRequest.setTopP(0.05);
    chatCompletionRequest.setMaxTokens(100);

    // Return with set-up request agent.
    return chatCompletionRequest;
  }
}

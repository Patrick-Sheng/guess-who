package nz.ac.auckland.se206.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.Suspect;
import nz.ac.auckland.se206.models.InteractionLog;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.speech.TextToSpeech;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController extends MapController {
  public ImageView imageGardener;
  public ImageView imageNiece;
  public ImageView imageAunt;

  public Pane childRoom;
  public Pane auntieRoom;
  public Pane gardenerRoom;
  public Pane guessRoom;

  public GridPane gridMap;
  public Label labelMap;
  
  public TextField userField;
  public InlineCssTextArea logArea;
  public Label suspectLabel;

  @FXML
  public void initialize() {
    super.initialize();
    enterUser(App.getSelectedSuspect());
  }

  @FXML
  private void onEnter() {
    String text = userField.getText();

    addMessage(text, App.getSelectedSuspect(), Suspect.DETECTIVE, true);

    userField.clear();

    checkButton();

    addLog(new InteractionLog(App.getSelectedSuspect(), "is thinking..."), true);

    logArea.requestFollowCaret();

    fetchMessage(App.getSelectedSuspect());
  }

  private void fetchMessage(Suspect current) {
    // Initialize the task to fetch the voice and message.
    Task<Void> task =
            new Task<>() {
              @Override
              protected Void call() {
                try {
                  // Request the chat message (in text)
                  ChatCompletionRequest request = App.chatMessages.get(current);
                  ChatCompletionResult chatCompletionResult = request.execute();

                  Choice result = chatCompletionResult.getChoices().iterator().next();

                  String message = result.getChatMessage().getContent();

                  // Add the chat message to the related textbox.
                  Platform.runLater(
                          () -> {
                            addMessage(message, current, current, true);
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
    InteractionLog log = new InteractionLog(from, message);
    App.interactionLogs.get(conversation).add(log);

    if (conversation == App.getSelectedSuspect()) {
      addLog(log, newLineBefore);
    }

    if (isActor(conversation)) {
      String type = "assistant";

      if (from == Suspect.NARRATOR) {
        return;
      } else if (from == Suspect.DETECTIVE) {
        type = "user";
      }

      App.chatMessages.get(conversation).addMessage(type, message);
    }
  }

  private static String EnumToPrompt(Suspect suspect) {
    return switch (suspect) {
      case AUNT -> "auntie";
      case NIECE -> "child";
      case GARDENER -> "gardener";
      default -> "";
    };
  }

  public void enterUser(Suspect suspect) {
    String intro = switch (suspect) {
      case AUNT -> "You walk into the room where " + EnumToName(suspect) + " sits calmly in a chair.";
      case NIECE -> "You race into the room where " + EnumToName(suspect) + " sits with her bear, wide eyed.";
      case GARDENER -> "You peek into the manor's green house were you see " + EnumToName(suspect) + " standing attentive.";
      default -> "";
    };

    startDialog(intro, suspect);

    imageGardener.setVisible(suspect == Suspect.GARDENER);
    imageNiece.setVisible(suspect == Suspect.NIECE);
    imageAunt.setVisible(suspect == Suspect.AUNT);

    suspectLabel.setText(EnumToName(suspect));
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

    logArea.append(
            EnumToName(log.suspect()) + " (" + log.suspect().name() + "): ", "-fx-font-weight: bold");
    logArea.append(log.message(), "");
  }

  private void startDialog(String intro, Suspect suspect) {
    App.setSelectedSuspect(suspect);

    logArea.clear();

    // If the player has not yet interacted with this character.
    if (!App.interactionLogs.containsKey(suspect)) {
      App.increasePeople();
      checkButton();

      // Stop previous character from speaking
      TextToSpeech.stopSpeak();

      TextToSpeech.speak(intro);

      App.interactionLogs.put(suspect, new ArrayList<>());

      // Send character description to ai to begin conversation.
      if (isActor(suspect)) {
        ChatCompletionRequest chatCompletionRequest = getChatCompletionRequest(suspect, intro);

        App.chatMessages.put(suspect, chatCompletionRequest);

        fetchMessage(suspect);
      }

      addMessage(intro, suspect, Suspect.NARRATOR, false);
    } else {
      // If the character has interacted with this character before.
      boolean isFirst = true;

      // Add all logs in from the previous session.
      for (InteractionLog log : App.interactionLogs.get(suspect)) {
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
        App.increaseObjects();
      }
    }

    // Re/set components to current state
    userField.clear();
    checkButton();
  }

  private ChatCompletionRequest getChatCompletionRequest(Suspect suspect, String context) {
    ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest(App.getConfig());

    // Base prompt for all AI agents.
    String s1 = PromptEngineering.getPrompt("chat.txt", new HashMap<>());
    String s2 = PromptEngineering.getPrompt(EnumToPrompt(suspect) + ".txt", new HashMap<>());

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

package nz.ac.auckland.se206;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.controllers.ChatController;
import nz.ac.auckland.se206.controllers.GameOverController;
import nz.ac.auckland.se206.controllers.GuessingController;
import nz.ac.auckland.se206.controllers.IntroductionController;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;
import nz.ac.auckland.se206.models.InteractionLog;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.timer.CountdownTimer;

/**
 * This class represents the state of the game. It stores the current state of the game, the timer,
 * the controllers, the chat messages, the interaction logs, the selected suspect, the chosen
 * suspect, the number of objects interfaced, the number of people interfaced, the colour of the
 * timer, and the message.
 */
public class GameState {
  private CountdownTimer timer;

  private Parent roomControllerRoot;
  private Parent chatControllerRoot;

  private RoomController roomController;
  private ChatController chatController;

  private ChatCompletionRequest chatCompletionRequest;

  private IntroductionController introductionController;
  private GuessingController guessingController;
  private GameOverController gameOverController;

  private final Map<Suspect, List<InteractionLog>> interactionLogs = new HashMap<>();
  private final Map<Suspect, ChatCompletionRequest> chatMessages = new HashMap<>();

  private int objectsInterfaced = 0;
  private int peopleInterfaced = 0;

  private Suspect selectedSuspect;
  private Suspect chosenSuspect;

  private int red = 50;
  private int green = 255;
  private int blue = 70;

  private String message = "";

  public int getSuspectCount() {
    return peopleInterfaced;
  }

  public int getObjectCount() {
    return objectsInterfaced;
  }

  public void increaseObjects() {
    objectsInterfaced += 1;
  }

  public void increasePeople() {
    peopleInterfaced += 1;
  }

  public void startTimer(int time) {
    timer = new CountdownTimer(time + 1); // 1 extra second to account for delay (for now)
    timer.start();
  }

  /**
   * Stops the timer if it is running. This method is used when the game moves to a different state.
   */
  public void stopTimer() {
    if (timer != null) {
      timer.stop();
    }
  }

  /**
   * Calls the updateLabel method in the timer class to update the timer label. This method is used
   * for manual timer updates.
   */
  public void callManualTimerUpdate() {
    if (timer != null) {
      timer.updateLabel();
    }
  }

  /**
   * Updates the timer label in relevant controller based on the current state of the game.
   *
   * @param time the time to update the timer label with.
   */
  public void updateTimer(int time) {
    SceneState currentState = App.getCurrentState();

    // If timer doesn't exist or current state is null, don't update timer label
    if (timer != null && currentState != null) {
      if (time <= 0) {
        stopTimer();
      }
      // Changes the colour of the timer by 1 iteration
      changeTimerColor();
      switch (currentState) {
        case START_GAME:
          if (roomController != null) {
            roomController.updateLblTimer(time, red, green, blue);
          }
          break;
        case CHAT:
          if (chatController != null) {
            chatController.updateLblTimer(time, red, green, blue);
          }
          break;
        case START_GUESSING:
          if (guessingController != null) {
            guessingController.updateLblTimer(time, red, green, blue);
          }
          break;
        case INTRODUCTION:
          if (introductionController != null) {
            introductionController.updateLblTimer(time, 255, 255, 255);
          }
          break;
        default:
          System.out.println("Other: " + time);
          break;
      }
    }
  }

  /**
   * Changes the colour of the timer at different increments based on the current state of the game.
   */
  public void changeTimerColor() {
    SceneState currentState = App.getCurrentState();

    // Sets how fast colours change based on current state
    if (currentState.equals(SceneState.START_GAME)
        || currentState.equals(SceneState.CHAT)
        || currentState.equals(SceneState.INTRODUCTION)) {
      if (blue - 2 > 31) {
        blue -= 2;
      } else if (red + 2 < 256) {
        red += 2;
      } else if (green - 2 > 31) {
        green -= 2;
      }
    } else if (currentState.equals(SceneState.START_GUESSING)) {
      if (blue - 10 > 31) {
        blue -= 10;
      } else if (red + 10 < 256) {
        red += 10;
      } else if (green + 10 > 31) {
        green -= 10;
      }
    }
  }

  public void setExplaination(String text) {
    message = text.trim();
    setAiProxyConfig();
  }

  /**
   * Sets the AI proxy configuration for the GPT model. This method is used to configure the GPT
   * model with specific parameters.
   */
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
                      .setMaxTokens(1000);
              runGpt(new ChatMessage("system", getSystemPrompt()));
              sendExplaination();
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
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    return PromptEngineering.getPrompt("feedback_instruction.txt", null);
  }

  /**
   * Sends the explanation to the GPT model to generate a response. This method is used to get the
   * explanation for the user's message.
   */
  public void sendExplaination() {
    // Create a background task to get the explanation from the GPT model
    Task<String> getExplanationTask =
        new Task<>() {
          @Override
          protected String call() {
            if (!message.isEmpty()) {
              try {
                // Run the GPT model with the user's message and return the response
                ChatMessage response = runGpt(new ChatMessage("user", message));
                assert response != null;
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
          sendExplaination();
        });

    // Start the task in a new thread to avoid blocking the main thread
    new Thread(getExplanationTask).start();
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

  /**
   * Passes the feedback prompt to the game over controller.
   *
   * @param prompt the feedback prompt to pass.
   */
  public void updateFeedbackPrompt(String prompt) {
    if (gameOverController != null) {
      gameOverController.setFeedbackPrompt(prompt);
    }
  }

  public void setSuspect(Suspect suspect) {
    chosenSuspect = suspect;
  }

  public Suspect getSelectedSuspect() {
    return selectedSuspect;
  }

  public Suspect getChosenSuspect() {
    return chosenSuspect;
  }

  public void setSelectedSuspect(Suspect selectedSuspect) {
    this.selectedSuspect = selectedSuspect;
  }

  public Parent getChatControllerRoot() {
    return chatControllerRoot;
  }

  public Parent getRoomControllerRoot() {
    return roomControllerRoot;
  }

  public RoomController getRoomController() {
    return roomController;
  }

  public ChatController getChatController() {
    return chatController;
  }

  public void setIntroductionController(IntroductionController introductionController) {
    this.introductionController = introductionController;
  }

  public void setGameOverController(GameOverController gameOverController) {
    this.gameOverController = gameOverController;
  }

  public void setGuessingController(GuessingController guessingController) {
    this.guessingController = guessingController;
  }

  public void setChatController(ChatController chatController) {
    this.chatController = chatController;
  }

  public void setRoomController(RoomController roomController) {
    this.roomController = roomController;
  }

  public void setRoomControllerRoot(Parent roomControllerRoot) {
    this.roomControllerRoot = roomControllerRoot;
  }

  public void setChatControllerRoot(Parent chatControllerRoot) {
    this.chatControllerRoot = chatControllerRoot;
  }

  public GameOverController getGameOverController() {
    return gameOverController;
  }

  /**
   * Resets the colour of the timer to the default colour. This method is used when the timer is
   * reset.
   */
  public void resetColour() {
    red = 50;
    green = 255;
    blue = 70;
  }

  public Map<Suspect, ChatCompletionRequest> getChatMessages() {
    return chatMessages;
  }

  public Map<Suspect, List<InteractionLog>> getInteractionLogs() {
    return interactionLogs;
  }
}

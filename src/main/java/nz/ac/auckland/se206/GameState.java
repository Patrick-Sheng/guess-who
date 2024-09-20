package nz.ac.auckland.se206;

import javafx.scene.Parent;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.controllers.ChatController;
import nz.ac.auckland.se206.controllers.GameOverController;
import nz.ac.auckland.se206.controllers.GuessingController;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;
import nz.ac.auckland.se206.models.InteractionLog;
import nz.ac.auckland.se206.timer.CountdownTimer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private CountdownTimer timer;

    private Parent roomControllerRoot;
    private Parent chatControllerRoot;

    private RoomController roomController;
    private ChatController chatController;

    private GuessingController guessingController;
    private GameOverController gameOverController;

    private Map<Suspect, List<InteractionLog>> interactionLogs = new HashMap<>();
    private Map<Suspect, ChatCompletionRequest> chatMessages = new HashMap<>();

    private int objectsInterfaced = 0;
    private int peopleInterfaced = 0;

    private Suspect selectedSuspect;
    private Suspect chosenSuspect;

    private int red = 50;
    private int green = 255;
    private int blue = 70;

    public void increaseObjects() { objectsInterfaced += 1; }
    public void increasePeople() { peopleInterfaced += 1; }

    public boolean checkEnableButton() {
        return objectsInterfaced >= 1 && peopleInterfaced >= 3;
    }

    public void startTimer(int time) {
        timer = new CountdownTimer(time + 1); // 1 extra second to account for delay (for now)
        timer.start();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void updateTimer(int time) {
        SceneState currentState = App.getCurrentState();

        if (timer != null && currentState != null) {
            if (time <= 0) {
                stopTimer();
            }
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
                default:
                    System.out.println("Other: " + time);
                    break;
            }
        }
    }

    public void changeTimerColor() {
        SceneState currentState = App.getCurrentState();

        if (currentState.equals(SceneState.START_GAME) || currentState.equals(SceneState.CHAT)) {
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

    public void setChosenSuspect(Suspect chosenSuspect) {
        this.chosenSuspect = chosenSuspect;
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

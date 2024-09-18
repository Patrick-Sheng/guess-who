package nz.ac.auckland.se206;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.controllers.ChatController;
import nz.ac.auckland.se206.controllers.GameOverController;
import nz.ac.auckland.se206.controllers.GuessingController;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.enums.Suspect;
import nz.ac.auckland.se206.timer.CountdownTimer;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {
  private static Scene scene;
  private static MediaPlayer music;
  private static String currentlyPlaying;
  private static AudioClip hoverSound;
  private static CountdownTimer timer;
  private static SceneState currentState;
  private static Parent roomControllerRoot;
  private static Parent chatControllerRoot;
  private static RoomController roomController;
  private static ChatController chatController;
  private static GuessingController guessingController;
  private static GameOverController gameOverController;
  private static Suspect chosenSuspect;

  private static int red = 50;
  private static int green = 255;
  private static int blue = 70;

  private static boolean isMuted;

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  public static void setMuted(boolean newMuteState) {
    isMuted = newMuteState;
  }

  public static AudioClip getSfx() {
    return hoverSound;
  }

  public static MediaPlayer getMusic() {
    return music;
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param state the state to transition to
   */
  public static void setRoot(SceneState state) {
    currentState = state;
    String fxml = getSceneName(state);

    if (fxml.equals("room") && roomController != null) {
      Stage stage = (Stage) scene.getWindow();
      SetStage(stage, roomControllerRoot, state);
      return;
    } else if (fxml.equals("chat") && chatController != null) {
      Stage stage = (Stage) scene.getWindow();
      SetStage(stage, chatControllerRoot, state);
      return;
    }

    Task<Parent> task =
        new Task<>() {
          @Override
          protected Parent call() throws IOException {
            return loadFxml(fxml);
          }
        };

    task.setOnSucceeded(
        event -> {
          Parent root = task.getValue();
          Platform.runLater(
              () -> {
                Stage stage = (Stage) scene.getWindow();
                SetStage(stage, root, state);
              });
        });

    task.setOnFailed(
        event -> {
          Throwable e = task.getException();
          e.printStackTrace();
        });

    new Thread(task).start();
  }

  private static void SetStage(Stage stage, Parent root, SceneState state) {
    scene.setRoot(root);
    stage.setScene(scene);
    stage.show();
    root.requestFocus();

    String music = getSceneMusic(state);
    playMusic(music);
  }

  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml".
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    Parent root = loader.load();

    if (fxml.equals("mainMenu")) { // Reset controllers if main menu is loaded
      resetGame();
    } else if (fxml.equals("room") && roomController == null) {
      roomController = loader.getController();
      roomControllerRoot = root;
    } else if (fxml.equals("chat") && chatController == null) {
      chatController = loader.getController();
      chatControllerRoot = root;
    } else if (fxml.equals("guessing")) {
      guessingController = loader.getController();
    } else if (fxml.equals("gameOver")) {
      gameOverController = loader.getController();
      gameOverController.setGameOverImage(chosenSuspect);
    }

    return root;
  }

  private static void resetGame() {
    roomController = null;
    chatController = null;
    guessingController = null;
    gameOverController = null;
    chosenSuspect = null;
    resetColour();
  }

  public static void resetColour() {
    red = 50;
    green = 255;
    blue = 70;
  }

  private static void playMusic(String name) {
    if (currentlyPlaying.equals(name)) {
      return;
    }

    boolean muted = false;
    double volume = .25;

    if (music != null) {
      muted = music.isMute();
      volume = music.getVolume();

      music.stop();
      music.dispose();
    }

    URL resource = App.class.getResource("/music/" + name + ".mp3");

    assert resource != null;
    music = new MediaPlayer(new Media(resource.toString()));

    music.setMute(muted);
    music.setVolume(volume);

    music.setOnEndOfMedia(
        new Runnable() {
          public void run() {
            music.seek(Duration.ZERO);
          }
        });

    music.play();

    currentlyPlaying = name;
  }

  public static void playHover() {
    if (!isMuted) {
      hoverSound.play();
    }
  }

  public static void stopHover() {
    hoverSound.stop();
  }

  private static String getSceneName(SceneState state) {
    return switch (state) {
      case MAIN_MENU -> "mainMenu";
      case SETTINGS -> "settings";
      case START_GAME -> "room";
      case CHAT -> "chat";
      case START_GUESSING -> "guessing";
      case END_GAME_WON, END_GAME_LOST -> "gameOver";
    };
  }

  private static String getSceneMusic(SceneState state) {
    return switch (state) {
      case MAIN_MENU, SETTINGS, CHAT -> "menuMusic";
      case START_GAME, START_GUESSING -> "guessingMusic";
      case END_GAME_WON -> "winMusic";
      case END_GAME_LOST -> "gameOverMusic";
    };
  }

  public static void startTimer(int time) {
    timer = new CountdownTimer(time + 1); // 1 extra second to account for delay (for now)
    timer.start();
  }

  public static void stopTimer() {
    if (timer != null) {
      timer.stop();
    }
  }

  public static void updateTimer(int time) {
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

  public static void changeTimerColor() {
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

  public static void setSuspect(Suspect suspect) {
    chosenSuspect = suspect;
  }

  public static void sendEndGameStats(Suspect suspect) {
    if (gameOverController != null) {
      gameOverController.setGameOverImage(suspect);
    }
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    SceneState defaultState = SceneState.MAIN_MENU;

    currentlyPlaying = "";
    isMuted = false;

    hoverSound =
        new AudioClip(
            Objects.requireNonNull(App.class.getResource("/sounds/buttonHover.wav"))
                .toExternalForm());
    hoverSound.setVolume(.25f);

    Parent root = loadFxml(getSceneName(defaultState));

    scene = new Scene(root);
    scene
        .getStylesheets()
        .add(Objects.requireNonNull(App.class.getResource("/css/style.css")).toExternalForm());

    stage.setResizable(false);
    stage.setTitle("PI Masters: Whispers of Emeralds");
    stage
        .getIcons()
        .add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/images/logo.png"))));

    SetStage(stage, root, defaultState);
  }
}

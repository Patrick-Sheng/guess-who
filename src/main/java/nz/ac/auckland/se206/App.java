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
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.controllers.ChatController;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.enums.SceneState;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {
  public static final String GUESS_DIALOG = "Let's get to guessing!";

  private static Scene scene;
  private static SceneState currentState;
  private static GameState gameState;
  private static ApiProxyConfig config;
  private static MediaPlayer music;
  private static String currentlyPlaying;
  private static AudioClip hoverSound;
  private static boolean isMuted;
  private static boolean isRunning = true;

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
  public static void setRoot(SceneState state, String speech) {

    TextToSpeech.stopSpeak();
    TextToSpeech.speak(speech);

    currentState = state;
    String fxml = getSceneName(state);

    // Check if the current game state is null
    if (gameState != null) {
      // Prepares the controller for room and chat scenes
      RoomController roomController = gameState.getRoomController();
      ChatController chatController = gameState.getChatController();

      // Set relevant scene on stage
      if (fxml.equals("room") && roomController != null) {
        Stage stage = (Stage) scene.getWindow();
        setStage(stage, gameState.getRoomControllerRoot(), state);

        roomController.isGuessReadyAndUpdate();
        return;
      } else if (fxml.equals("chat") && chatController != null) {
        Stage stage = (Stage) scene.getWindow();
        setStage(stage, gameState.getChatControllerRoot(), state);

        chatController.isGuessReadyAndUpdate();
        chatController.enterUser(gameState.getSelectedSuspect());
        return;
      }
    }

    Task<Parent> task = getParentTask(state, fxml);

    new Thread(task).start();
  }

  private static Task<Parent> getParentTask(SceneState state, String fxml) {

    // Load FXML and set scene on stage
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
          // Set the scene on the JavaFX Application Thread
          Platform.runLater(
              () -> {
                Stage stage = (Stage) scene.getWindow();
                setStage(stage, root, state);
              });
        });

    task.setOnFailed(
        event -> {
          Throwable e = task.getException();
          e.printStackTrace();
        });
    return task;
  }

  private static void setStage(Stage stage, Parent root, SceneState state) {
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
      gameState = new GameState();
    } else if (gameState != null) {
      if (fxml.equals("room") && gameState.getRoomController() == null) {
        gameState.setRoomController(loader.getController());
        gameState.setRoomControllerRoot(root);
      } else if (fxml.equals("chat") && gameState.getChatController() == null) {
        gameState.setChatController(loader.getController());
        gameState.setChatControllerRoot(root);
      } else if (fxml.equals("guessing")) {
        gameState.setGuessingController(loader.getController());
      } else if (fxml.equals("gameOver")) {
        gameState.setGameOverController(loader.getController());
        gameState.getGameOverController().setGameOverImage(gameState.getChosenSuspect());
      }
    }

    return root;
  }

  private static void playMusic(String name) {
    // Check if the music currently playing is the same as the music to be played
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

    // Find mp3 file and plays the music
    URL resource = App.class.getResource("/music/" + name + ".mp3");

    assert resource != null;
    music = new MediaPlayer(new Media(resource.toString()));

    // Set volume and mute status
    music.setMute(muted);
    music.setVolume(volume);

    music.setOnEndOfMedia(() -> music.seek(Duration.ZERO));

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
    // Returns relevant state name as a string based on current state
    return switch (state) {
      case MAIN_MENU -> "mainMenu";
      case SETTINGS -> "settings";
      case INTRODUCTION -> "introduction";
      case START_GAME -> "room";
      case CHAT -> "chat";
      case START_GUESSING -> "guessing";
      case END_GAME_WON, END_GAME_LOST -> "gameOver";
    };
  }

  private static String getSceneMusic(SceneState state) {
    return switch (state) {
      case MAIN_MENU, SETTINGS, CHAT -> "menuMusic";
      case INTRODUCTION, START_GAME, START_GUESSING -> "guessingMusic";
      case END_GAME_WON -> "winMusic";
      case END_GAME_LOST -> "gameOverMusic";
    };
  }

  public static SceneState getCurrentState() {
    return currentState;
  }

  public static ApiProxyConfig getConfig() {
    return config;
  }

  public static GameState getGameState() {
    return gameState;
  }

  public static boolean isRunning() {
    return isRunning;
  }

  public static void playCustomSoundEffect(String name) {
    if (!music.isMute()) {
      AudioClip customSound =
          new AudioClip(
              Objects.requireNonNull(App.class.getResource("/sounds/" + name)).toExternalForm());

      customSound.setVolume(hoverSound.getVolume());
      customSound.play();
    }
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException, ApiProxyException {
    SceneState defaultState = SceneState.START_GUESSING;
    TextToSpeech.doStartSpeech();

    // Initialize game state, variables, and API proxy config
    config = ApiProxyConfig.readConfig();
    gameState = new GameState();

    currentlyPlaying = "";
    isMuted = false;

    hoverSound =
        new AudioClip(
            Objects.requireNonNull(App.class.getResource("/sounds/buttonHover.wav"))
                .toExternalForm());
    hoverSound.setVolume(.5f);

    // Set main menu scene (default state) as root on stage
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

    setStage(stage, root, defaultState);
  }

  @Override
  public void stop() throws Exception {
    isRunning = false;
    TextToSpeech.stopSpeak();
    super.stop();
  }
}

package nz.ac.auckland.se206;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import  javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.enums.SceneState;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {
  private static Scene scene;
  private static MediaPlayer music;
  private static String currentlyPlaying;
  private static AudioClip hoverSound;

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
    String fxml = getSceneName(state);

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
              Platform.runLater(() -> {
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
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
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
    if (!isMuted)
      hoverSound.play();
  }

  public static void stopHover() {
    hoverSound.stop();
  }

  private static String getSceneName(SceneState state) {
    return switch (state) {
      case MAIN_MENU -> "mainMenu";
      case SETTINGS -> "settings";
      case START_GAME -> "room";
      case START_GUESSING -> "guessing";
      case END_GAME_WON, END_GAME_LOST -> "gameOver";
    };
  }

  private static String getSceneMusic(SceneState state) {
    return switch (state) {
      case MAIN_MENU, SETTINGS -> "menuMusic";
      case START_GAME, START_GUESSING -> "guessingMusic";
      case END_GAME_WON -> "winMusic";
      case END_GAME_LOST -> "gameOverMusic";
    };
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

    hoverSound = new AudioClip(Objects.requireNonNull(App.class.getResource("/sounds/buttonHover.wav")).toExternalForm());
    hoverSound.setVolume(.25f);

    Parent root = loadFxml(getSceneName(defaultState));

    scene = new Scene(root);
    scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("/css/style.css")).toExternalForm());

    stage.setResizable(false);
    stage.setTitle("PI Masters: Whispers of Emeralds");
    stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/images/logo.png"))));

    SetStage(stage, root, defaultState);
  }
}

package nz.ac.auckland.se206.speech;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest;
import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Provider;
import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Voice;
import nz.ac.auckland.apiproxy.tts.TextToSpeechResult;
import nz.ac.auckland.se206.App;

/** Text-to-speech API using the JavaX speech library. */
public class TextToSpeech {
  private static final Map<String, String> textToMp3Map = new HashMap<>();

  private static final BlockingQueue<Runnable> dialogQueue = new LinkedBlockingQueue<>();
  private static final AtomicBoolean stopFlag = new AtomicBoolean(false);

  public static void doStartSpeech() {
    loadMp3Files();
    processQueue();
  }

  public static void speak(final String text) {
    speak(text, Voice.OPENAI_ALLOY, Provider.OPENAI);
  }

  ////

  public static void speak(String message, Voice voice, Provider provider) {
    if (message == null) {
      throw new IllegalArgumentException("Text cannot be null.");
    }

    stopFlag.set(false);

    Text text = new Text(message, voice, provider);

    String mp3FilePath = getMp3FilePath(text.text());

    dialogQueue.offer(
            () -> {
              if (mp3FilePath != null) {
                playMp3(mp3FilePath);
              } else {
                cloudTts(text);
              }
            });
  }

  private static void processQueue() {
    Thread voiceThread = new Thread(
            () -> {
              while (App.isRunning()) {
                if (dialogQueue.isEmpty() || stopFlag.get()) {
                  continue;
                }

                try {
                  Runnable task = dialogQueue.take();
                  task.run();
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                }

                try {
                  Thread.sleep(10);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            });

    voiceThread.setDaemon(true);
    voiceThread.start();
  }

  private static void playMp3(String mp3FilePath) {
    CountDownLatch latch = new CountDownLatch(1);

    Media media = new Media(new File(mp3FilePath).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);

    mediaPlayer.setVolume(App.getSfx().getVolume());

    mediaPlayer.setOnEndOfMedia(latch::countDown);
    mediaPlayer.setOnError(
            () -> {
              System.err.println("Error occurred: " + mediaPlayer.getError());
              latch.countDown();
            });

    Platform.runLater(mediaPlayer::play);

    try {
      latch.await(); // Block until the playback is complete
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private static void cloudTts(Text text) {
    try {
      // Make request to servers.
      TextToSpeechRequest ttsRequest = new TextToSpeechRequest(App.getConfig());
      ttsRequest.setText(text.text()).setProvider(text.provider()).setVoice(text.voice());

      // Received TTS result.
      TextToSpeechResult ttsResult = ttsRequest.execute();
      String audioUrl = ttsResult.getAudioUrl();

      System.out.println("Found audio for '" + text.text() + "': " + audioUrl);

      // Play TTS via JavaFX player.
      try (InputStream inputStream = new BufferedInputStream(new URL(audioUrl).openStream())) {
        Player player = new Player(inputStream);
        player.play();
      } catch (JavaLayerException | IOException e) {
        e.printStackTrace();
      }
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  public static void stopSpeak() {
    stopFlag.set(true);
    dialogQueue.clear();
  }

  private static void loadMp3Files() {
    try (Stream<Path> paths =
                 Files.walk(Paths.get(Objects.requireNonNull(TextToSpeech.class.getResource("/tts")).toURI()))) {
      paths
              .filter(Files::isRegularFile)
              .filter(path -> path.toString().endsWith(".mp3"))
              .forEach(
                      path -> {
                        String fileName = path.getFileName().toString();
                        String key =
                                fileName
                                        .toLowerCase()
                                        .replaceAll("[^a-z0-9]", "")
                                        .substring(0, fileName.length() - 4); // Remove ".mp3"
                        textToMp3Map.put(key, path.toString());
                      });
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }

  private static String getMp3FilePath(String text) {
    if (text == null) {
      return null;
    }

    String transformedText = text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

    return textToMp3Map.get(transformedText);
  }
}

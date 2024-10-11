package nz.ac.auckland.se206.timer;

import javafx.application.Platform;
import javafx.concurrent.Task;
import nz.ac.auckland.se206.App;

/** Countdown timer that runs in the game. */
public class CountdownTimer {
  private int remainingTime;
  private boolean isRunning;
  private Thread timerThread;

  public CountdownTimer(int time) {
    this.remainingTime = time;
    isRunning = true;
  }

  /**
   * Starts the timer by creating a new thread that updates the timer every second. The timer will
   * stop when the remaining time reaches 0.
   */
  public void start() {

    // Updates timer by decreasing timer by 1 every 1 second
    Task<Void> timerTask =
        new Task<>() {
          @Override
          protected Void call() throws Exception {
            // If timer is stopped, don't change the time
            while (remainingTime > 0 && isRunning) {
              Platform.runLater(() -> updateLabel());
              remainingTime--;
              Thread.sleep(1000);
            }
            System.out.println("Time's up!");
            return null;
          }
        };

    timerThread = new Thread(timerTask);
    timerThread.setDaemon(true);
    timerThread.start();
  }

  public void pause() {
    isRunning = false;
  }

  /** Stops the timer and interrupts the timer thread. */
  public void stop() {
    pause();
    if (timerThread != null && timerThread.isAlive()) {
      timerThread.interrupt();
    }
  }

  public void updateLabel() {
    App.getGameState().updateTimer(remainingTime);
  }
}

package nz.ac.auckland.se206.timer;

import javafx.application.Platform;
import javafx.concurrent.Task;
import nz.ac.auckland.se206.App;

public class CountdownTimer {

  private int remainingTime = 0;
  private boolean isRunning = false;
  private Thread timerThread;

  public CountdownTimer(int time) {
    this.remainingTime = time;
    isRunning = true;
  }

  public void start() {
    Task<Void> timerTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            while (remainingTime > 0 && isRunning == true) {
              Platform.runLater(
                  new Runnable() {
                    @Override
                    public void run() {
                      updateLabel();
                    }
                  });
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

  public void stop() {
    pause();
    if (timerThread != null && timerThread.isAlive()) {
      timerThread.interrupt();
    }
  }

  public void updateLabel() {
    App.updateTimer(remainingTime);
  }
}
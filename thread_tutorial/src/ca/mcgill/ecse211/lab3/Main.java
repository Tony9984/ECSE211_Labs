package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import java.io.FileNotFoundException;
import lejos.hardware.Button;

/**
 * The main class.
 */
public class Main {
  
  /**
   * Set this to true to print to a file.
   */
  public static final boolean WRITE_TO_FILE = false;

  /**
   * Main entry point.
   * 
   * @param args
   */
  public static void main(String[] args) {

    Log.setLogging(true, true, false, true);

    if (WRITE_TO_FILE) {
      setupLogWriter();
    }
    
    new Thread(usPoller).start();
    new Thread(odometer).start();
    new Thread(obstacleAvoidance).start();

    completeCourse();

    while (Button.waitForAnyPress() != Button.ID_ESCAPE)
      ; // do nothing
    
    System.exit(0);
  }

  public static void setupLogWriter() {
    try {
      Log.setLogWriter(System.currentTimeMillis() + ".log");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Completes a course.
   */
  private static void completeCourse() {
    int[][] waypoints = {{60, 30}, {30, 30}, {30, 60}, {60, 0}};

    for (int[] point : waypoints) {
      Navigation.travelTo(point[0], point[1], true);
      while (ObstacleAvoidance.traveling) {
        Main.sleepFor(500);
      }
    }
  }
  
  /**
   * Sleeps current thread for the specified duration.
   * 
   * @param duration sleep duration in milliseconds
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
  
}

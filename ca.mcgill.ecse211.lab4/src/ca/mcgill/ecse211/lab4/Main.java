package ca.mcgill.ecse211.lab4;

import lejos.hardware.Button;
import static ca.mcgill.ecse211.lab4.Resources.*;

/**
 * The main driver class for the odometry lab.
 */
public class Main {

  /**
   * The main entry point.
   * 
   * @param args
   */
  public static void main(String[] args) {
    int buttonChoice;

    // Odometer thread

    new Thread(odometer).start();

    buttonChoice = chooseClockType();

    if (buttonChoice == Button.ID_RIGHT) {
      new Thread(new Display()).start();
      UltrasonicLocalizer UltrasonicLocalizer = new UltrasonicLocalizer();
      UltrasonicLocalizer.localize(false);
    }

    else {
      new Thread(new Display()).start();
      UltrasonicLocalizer UltrasonicLocalizer = new UltrasonicLocalizer();
      UltrasonicLocalizer.localize(true);
    }
    
    while (Button.waitForAnyPress() != Button.ID_DOWN);
    LightLocalizer LightLocalizer = new LightLocalizer();
    LightLocalizer.localize();
    
    if (Button.waitForAnyPress() == Button.ID_ESCAPE) {
      System.exit(0);
    }

  }

  private static int chooseClockType() {
    int buttonChoice;
    Display.showText(
        "< Left | Right >",
        "       |        ",
        "Rising | Falling",
        "  Edge |  Edge  ",
        "       |        ");

    do {
      buttonChoice = Button.waitForAnyPress(); // left or right press
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
    return buttonChoice;
  }
}

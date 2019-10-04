package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;

/**
 * Class to display information on the LCD.
 */
public class LCDInfo implements Runnable {
  
  public static final int LCD_REFRESH = 100;
  
  // arrays for displaying data
  private double [] pos;
  
  public LCDInfo() {    
    // initialize the arrays for displaying data
    pos = new double [3];
  }
  
  public void run() { 
    odometer.populatePosition(pos);
    LCD.clear();
    LCD.drawString("X: ", 0, 0);
    LCD.drawString("Y: ", 0, 1);
    LCD.drawString("H: ", 0, 2);
    LCD.drawInt((int)(pos[0] * 10), 3, 0);
    LCD.drawInt((int)(pos[1] * 10), 3, 1);
    LCD.drawInt((int)pos[2], 3, 2);
  }
}

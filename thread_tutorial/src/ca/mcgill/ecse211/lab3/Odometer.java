package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;

/*
 * File: Odometer.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * Changed to Thread - Jonah Caplan
 * 2015
 * Refactored codebase (see GitHub for future changes) - Younes Boubekeur
 * Winter 2019
 * 
 * Class which controls the odometer for the robot
 * 
 * Odometer defines coordinate system as such...
 * 
 *                    90Deg:pos y-axis
 *                         |
 *                         |
 *                         |
 *                         |
 * 180Deg:neg x-axis------------------0Deg:pos x-axis
 *                         |
 *                         |
 *                         |
 *                         |
 *                   270Deg:neg y-axis
 * 
 * The odometer is initialized to 90 degrees, assuming the robot is facing up the positive y-axis
 * 
 */


// static imports to avoid duplicating variables and make the code easier to read
import static java.lang.Math.*;

/**
 * The odometer class.
 */
public class Odometer implements Runnable {

  private double x, y, theta;
  private double[] oldDH = new double[2];
  private double[] dDH = new double[2];

  /**
   * Constructs an Odometer object.
   */
  public Odometer() {
    this.x = 0.0;
    this.y = 0.0;
    this.theta = 90.0;
  }

  /**
   * Calculates displacement and heading.
   */
  private void calculateDisplacementAndHeading(double[] data) {
    int leftTacho = leftMotor.getTachoCount();
    int rightTacho = rightMotor.getTachoCount();

    data[0] = (leftTacho * LEFT_RADIUS + rightTacho * RIGHT_RADIUS) * PI / 360.0;
    data[1] = (rightTacho * RIGHT_RADIUS - leftTacho * LEFT_RADIUS) / WIDTH;
  }

  /*
   * Recomputes the odometer values using the displacement and heading changes.
   */
  public void run() {
    while (true) {
      calculateDisplacementAndHeading(dDH);
      dDH[0] -= oldDH[0];
      dDH[1] -= oldDH[1];

      // update the position in a critical section
      synchronized (this) {
        theta += dDH[1];
        theta = fixDegAngle(theta);

        x += dDH[0] * cos(toRadians(theta));
        y += dDH[0] * sin(toRadians(theta));
      }

      oldDH[0] += dDH[0];
      oldDH[1] += dDH[1];

      try {
        Thread.sleep(TIMEOUT_PERIOD);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      Log.log(Log.Sender.odometer,String.format("x: %f, y: %f, a: %f",
            getX(), getY(), getTheta()));
      
    }
  }

  /**
   * Returns the value of x.
   * 
   * @return the value of x.
   */
  public double getX() {
    synchronized (this) {
      return x;
    }
  }

  /**
   * Returns the value of y.
   * 
   * @return the value of y.
   */
  public double getY() {
    synchronized (this) {
      return y;
    }
  }

  /**
   * Returns the value of theta.
   * 
   * @return the value of theta.
   */
  public double getTheta() {
    synchronized (this) {
      return theta;
    }
  }

  /**
   * Sets x, y, and theta.
   * 
   * @param position an array containing [x, y, theta].
   * @param update a boolean array that indicates which variables to update, 
   * eg [false, true, false] to update y.
   */
  public void setPosition(double[] position, boolean[] update) {
    synchronized (this) {
      if (update[0])
        x = position[0];
      if (update[1])
        y = position[1];
      if (update[2])
        theta = position[2];
    }
  }

  /**
   * Populates the input position array in the format [x, y, theta].
   */
  public void populatePosition(double[] position) {
    synchronized (this) {
      position[0] = x;
      position[1] = y;
      position[2] = theta;
    }
  }

  /**
   * Returns the position array in the format [x, y, theta].
   * 
   * @return the position array in the format [x, y, theta].
   */
  public double[] getPosition() {
    synchronized (this) {
      return new double[] { x, y, theta };
    }
  }

  // static 'helper' methods
  
  /**
   * Constrains the input angle to the 0 - 360 degree range.
   * 
   * @param angle
   * @return the input angle rewritten in the 0 - 360 degree range.
   */
  public static double fixDegAngle(double angle) {
    if (angle < 0.0)
      angle = 360.0 + (angle % 360.0);

    return angle % 360.0;
  }

  /**
   * Calculates the minimum angle between two angles.
   * 
   * @param a the first angle.
   * @param b the second angle.
   * @return the minimum angle value.
   */
  public static double minimumAngleFromTo(double a, double b) {
    double d = fixDegAngle(b - a);

    if (d < 180.0)
      return d;
    else
      return d - 360.0;
  }
}

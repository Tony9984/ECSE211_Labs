package ca.mcgill.ecse211.lab3;
/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * Helper methods - Jonah Caplan
 * 2015
 * Refactored codebase (see GitHub for future changes) - Younes Boubekeur
 * Winter 2019
 * 
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */

import static ca.mcgill.ecse211.lab3.Resources.*;
//static imports to avoid duplicating variables and make the code easier to read
import static java.lang.Math.*;

/**
 * Class that offers static methods used for navigation.
 */
public class Navigation {
  
  static {
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
  }

  /**
   * Sets the motor speeds jointly.
   */
  public static void setSpeeds(float leftSpeed, float rightSpeed) {
    leftMotor.setSpeed(leftSpeed);
    rightMotor.setSpeed(rightSpeed);
    if (leftSpeed < 0) {
      leftMotor.backward();
    } else {
      leftMotor.forward();
    }
    if (rightSpeed < 0) {
      rightMotor.backward();
    } else {
      rightMotor.forward();
    }
  }

  /**
   * Floats the two motors jointly.
   */
  public static void setFloat() {
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.flt(true);
    rightMotor.flt(true);
  }

  /**
   * Travels to designated position, while constantly updating its heading.
   * 
   * @param x the destination x, in cm.
   * @param y the destination y, in cm.
   */
  public static void travelTo(double x, double y) {
    double minAng;
    while (!isDone(x, y)) {
      minAng = getDestAngle(x, y);
      turnTo(minAng, false);
      setSpeeds(FAST, FAST);
    }
    setSpeeds(0, 0);
  }
  
  /**
   * Travels to designated position, while constantly updating the heading.
   * @param x
   * @param y
   * @param avoid
   */
  public static void travelTo(double x, double y, boolean avoid) {
    if (avoid) {
      ObstacleAvoidance.destx = x;
      ObstacleAvoidance.desty = y;
      
      // This will trigger the state machine running in the obstacleAvoidance thread
      ObstacleAvoidance.traveling = true; 
    } else {
      Navigation.travelTo(x, y);
    }
  }

  /**
   * Returns {@code true} when done.
   * 
   * @param x
   * @param y
   * @return {@code true} when done.
   */
  public static boolean isDone(double x, double y) {
    return abs(x - odometer.getX()) < CM_ERR
        && abs(y - odometer.getY()) < CM_ERR;
  }

  /**
   * Returns {@code true} when facing destination.
   * 
   * @param angle
   * @return {@code true} when facing destination.
   */
  public static boolean facingDest(double angle) {
    return abs(angle - odometer.getTheta()) < DEG_ERR;
  }

  /**
   * Returns the destination angle.
   * 
   * @param x
   * @param y
   * @return the destination angle.
   */
  public static double getDestAngle(double x, double y) {
    double minAng = (atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / PI);
    if (minAng < 0) {
      minAng += 360.0;
    }
    return minAng;
  }

  /**
   * Turns robot towards the indicated angle.
   * 
   * @param angle
   * @param stop controls whether or not to stop the motors when the turn is completed
   */
  public static void turnTo(double angle, boolean stop) {
    double error = angle - odometer.getTheta();

    while (abs(error) > DEG_ERR) {
      error = angle - odometer.getTheta();

      if (error < -180.0) {
        setSpeeds(-SLOW, SLOW);
      } else if (error < 0.0) {
        setSpeeds(SLOW, -SLOW);
      } else if (error > 180.0) {
        setSpeeds(SLOW, -SLOW);
      } else {
        setSpeeds(-SLOW, SLOW);
      }
    }

    if (stop) {
      setSpeeds(0, 0);
    }
  }
  
  /**
   * Moves robot forward a set distance in cm.
   * 
   * @param distance
   * @param avoid
   */
  public static void goForward(double distance, boolean avoid) {
    double x = odometer.getX() + cos(toRadians(odometer.getTheta())) * distance;
    double y = odometer.getY() + sin(toRadians(odometer.getTheta())) * distance;

    travelTo(x, y, avoid);
  }
}

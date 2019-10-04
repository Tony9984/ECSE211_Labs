package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class UltrasonicLocalizer {

  // Initialize starting variables

  private static SampleProvider myDistance = US_SENSOR.getMode("Distance");
  private static float[] usData = new float[myDistance.sampleSize()];

  // Set detection constants

  private double wall_distance = 30;
  private double margin = 3;

  /**
   * Method that checks if it is risingEdge or fallingEdge
   * 
   * @param isRisingEdge
   */

  public void localize(boolean isRisingEdge) {

    double angleA, angleB, deltaT;

    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);

    if (isRisingEdge) {
      angleA = getAngleARise();
      angleB = getAngleBRise();
    }
    else {
      angleA = getAngleAFall();
      angleB = getAngleBFall();
    }

    // Calculate deltaT (degrees)

    if (angleA < angleB) {
      deltaT = 45 - (angleA + angleB) / 2.0;
    }
    else {
      deltaT = 225 - (angleA + angleB) / 2.0;
    }

    // Calculate the turning angle (degrees)

    double updatedAngle = odometer.getXYT()[2] + deltaT;
    odometer.setXYT(0.0, 0.0, updatedAngle);

    // Turn to 0 degrees

    turnTo(0);

    // Stop motors

    Sound.beep();
    leftMotor.stop(true);
    rightMotor.stop();

  }

  private double getAngleARise() {

    // Turn left until wall is detected

    while (readUSData() > wall_distance) {
      leftMotor.backward();
      rightMotor.forward();
    }

    Sound.beep();

    // Keep turning until open area is detected

    while (readUSData() < wall_distance + margin) {
      leftMotor.backward();
      rightMotor.forward();
    }

    // Stop motors

    Sound.beep();
    leftMotor.stop(true);
    rightMotor.stop();

    // Record the first angle value (degrees)

    return odometer.getXYT()[2];

  }

  private double getAngleBRise() {

    // Turn right until wall is detected

    while (readUSData() > wall_distance) {
      leftMotor.forward();
      rightMotor.backward();
    }

    Sound.beep();

    // Keep turning until open area is detected

    while (readUSData() < wall_distance + margin) {
      leftMotor.forward();
      rightMotor.backward();
    }

    // Alert with sound and stop motors

    Sound.beep();
    leftMotor.stop(true);
    rightMotor.stop();

    // Record the second angle value (degrees)

    return odometer.getXYT()[2];
  }

  private double getAngleAFall() {

    // Turn left until open area is detected

    while (readUSData() < wall_distance + margin) {
      leftMotor.backward();
      rightMotor.forward();
    }

    // Keep turning until wall is detected

    while (readUSData() > wall_distance) {
      leftMotor.backward();
      rightMotor.forward();
    }

    // Alert with sound and stop motors

    Sound.beep();
    leftMotor.stop(true);
    rightMotor.stop();

    // Record the first angle value (degrees)

    return odometer.getXYT()[2];
  }

  private double getAngleBFall() {

    // Turn right until open area is detected

    while (readUSData() < wall_distance + margin) {
      leftMotor.forward();
      rightMotor.backward();
    }

    // Keep turning until wall is detected

    while (readUSData() > wall_distance) {
      leftMotor.forward();
      rightMotor.backward();
    }

    // Alert with sound and stop motors

    Sound.beep();
    leftMotor.stop(true);
    rightMotor.stop();

    // Record the second angle value (degrees)

    return odometer.getXYT()[2];

  }

  /**
   * This method makes the robot turn to theta (taken from lab3)
   * 
   * @param theta
   */

  public void turnTo(double theta) {

    // Get the minimal turn angle

    double turnAngle = getMinAngle(theta - Math.toRadians(odometer.getXYT()[2]) - Math.PI);

    // Return one value immediately so only 3 threads are running
    // Rotate each motor to the right direction

    leftMotor.rotate(radToDeg(turnAngle), true);
    rightMotor.rotate(-radToDeg(turnAngle), false);
  }

  /**
   * This method returns the current value of the ultrasonic sensor
   * 
   * @return
   */

  private float readUSData() {
    US_SENSOR.getDistanceMode().fetchSample(usData, 0);
    float distance = usData[0] * 100;
    return distance > 100 ? 100 : distance;
  }

  /**
   * This method takes a distance and converts it to the number of wheel
   * rotations needed (taken from lab3)
   * 
   * @param distance
   * @return
   */

  private int distanceToRotations(double distance) {
    return (int) (180.0 * distance / (Math.PI * WHEEL_RAD));
  }

  /**
   * This method converts the distance angle (taken from lab3)
   * 
   * @param angle
   * @return
   */

  private int radToDeg(double angle) {
    return distanceToRotations(TRACK * angle / 2);
  }

  /**
   * This method calculates the minimum value of an angle
   * (taken from lab3)
   * 
   * @param angle
   * @return
   */

  public double getMinAngle(double angle) {
    if (angle > Math.PI) {
      angle -= 2 * Math.PI;
    } else if (angle < -Math.PI) {
      angle += 2 * Math.PI ;
    }
    return angle;
  }

}

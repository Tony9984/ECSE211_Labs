package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class UltrasonicLocalizer {
  
  // Initialize starting variables

  private static SampleProvider myDistance = US_SENSOR.getMode("Distance");
  private static float[] usData = new float[myDistance.sampleSize()];
  private double deltaT;
  
  // Set detection constants

  private double wall_distance = 30.0;
  private double gap = 2;

 
  
  /**
   * Method that checks if it is risingEdge or fallingEdge
   * 
   * @param isRisingEdge
   */

  public void localize(boolean isRisingEdge) {
    if (isRisingEdge) {
      risingEdge();
    }
    else {
      fallingEdge();
    }
  }
  
  /**
   * The rising edge localization method
   * 
   */

  public void risingEdge() {
    
    // Initializes angles

    double firstAngle, secondAngle, turnAngle;
    
    // Turn left until wall is detected

    while (readUSData() > wall_distance) {
      leftMotor.backward();
      rightMotor.forward();
    }
    
    // Keep turning until open area is detected

    while (readUSData() < wall_distance + gap) {
      leftMotor.backward();
      rightMotor.forward();
    }
    
    // Alert with sound

    Sound.beep();
    
    // Record the first angle value (degrees)

    firstAngle = odometer.getXYT()[2];
    
    // Turn right until wall is detected

    while (readUSData() > wall_distance) {
      leftMotor.forward();
      rightMotor.backward();
    }
    
    // Keep turning until open area is detected

    while (readUSData() < wall_distance + gap) {
      leftMotor.forward();
      rightMotor.backward();
    }
    
    // Alert with sound

    Sound.beep();
    
    // Record the second angle value (degrees)

    secondAngle = odometer.getXYT()[2];
    
    // Stop both motors

    leftMotor.stop(true);
    rightMotor.stop();
    
    // Calculate the rotation angle by comparing both angles (degrees)

    if (firstAngle < secondAngle) {
      deltaT = 45 - (firstAngle + secondAngle) / 2 + 180;
    }
    else if (secondAngle > firstAngle) {
      deltaT = 225 - (firstAngle + secondAngle) / 2 + 180;
    }
    
    // Calculate the turning angle (degrees)

    turnAngle = deltaT + odometer.getXYT()[2];
    
    // Rotate to 0 degrees

    leftMotor.rotateTo(-radToDeg(turnAngle - 1), true);
    rightMotor.rotateTo(radToDeg(turnAngle - 1), false);
    
    // Reset the odometer

    odometer.setXYT(0.0, 0.0, 0.0);

  }
  
  /**
   * The falling edge localization method
   * 
   */

  public void fallingEdge() {
    
    // Initializes angles

    double firstAngle, secondAngle, turnAngle;
    
    // Turn left until open area is detected

    while (readUSData() < wall_distance + gap) {
      leftMotor.backward();
      rightMotor.forward();
    }
    
    // Keep turning until wall is detected

    while (readUSData() > wall_distance) {
      leftMotor.backward();
      rightMotor.forward();
    }
    
    // Alert with sound

    Sound.beep();
    
    // Record the first angle value (degrees)

    firstAngle = odometer.getXYT()[2];
    
    // Turn right until open area is detected

    while (readUSData() < wall_distance + gap) {
      leftMotor.forward();
      rightMotor.backward();
    }
    
    // Keep turning until wall is detected

    while (readUSData() > wall_distance) {
      leftMotor.forward();
      rightMotor.backward();
    }
    
    // Alert with sound

    Sound.beep();
    
    // Record the second angle value (degrees)

    secondAngle = odometer.getXYT()[2];
    
    // Stop both motors

    leftMotor.stop(true);
    rightMotor.stop();
    
    // Calculate the rotation angle by comparing both angles (degrees)

    if (firstAngle < secondAngle) {
      deltaT = 45 - (firstAngle + secondAngle) / 2 + 180;
    }
    else if (secondAngle > firstAngle) {
      deltaT = 225 - (firstAngle + secondAngle) / 2 + 180;
    }
    
    // Calculate the turning angle (degrees)

    turnAngle = deltaT + odometer.getXYT()[2];
    
    // Rotate to 0 degrees

    leftMotor.rotateTo(-radToDeg(turnAngle - 1), true);
    rightMotor.rotateTo(radToDeg(turnAngle - 1), false);
    
    // Reset the odometer

    odometer.setXYT(0.0, 0.0, 0.0);

  }
  
  /**
   * This method returns the current value of the ultrasonic sensor
   * 
   * @return
   */

  private int readUSData() {
    US_SENSOR.getDistanceMode().fetchSample(usData, 0);
    return (int) (usData[0] * 100.0);
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
}

package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
  
  // Initialize variables for the color sensor

  private static final Port colourSampler = LocalEV3.get().getPort("S2");
  private SensorModes colourSamplerSensor = new EV3ColorSensor(colourSampler);
  private SampleProvider colourSensorValue = colourSamplerSensor.getMode("Red");
  private float[] colourSensorValues = new float[colourSamplerSensor.sampleSize()];
  
  // Initialize the colorValue
  
  private float colorValue;
  
  // Initialize array to store line angles
  
  double[] lineMeasures;
  
  /**
   * This method localizes the robot using the light sensor
   * 
   */

  public void localize() {
    
    // Initializes required values
    
    double dx, dy, thetaX, thetaY;
    
    // Starting index for line detection
    
    int index = 0;
    
    // Set default speed for motors and go to the origin (0,0)
    
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    goToOrigin();
    
    // Keep running the loop until all lines are detected
    
    while (index < 4) {
      
      // Constantly turning right
      
      leftMotor.forward();
      rightMotor.backward();
      
      // Read the color sensor value
      
      colorValue = readColorData();
      
      // If a line is detected, update the array with its theta value
      
      if (colorValue < 0.38) {
        lineMeasures[index] = odometer.getXYT()[2];
        Sound.beep();
        index++;
      }
    }
    
    // Interrupt the motors
    
    leftMotor.stop(true);
    rightMotor.stop();
    
    // Calculate thetas from 0 using angles in array
    
    thetaX = lineMeasures[2] - lineMeasures[0];
    thetaY = lineMeasures[3] - lineMeasures[1];
    
    // Calculate distance from 0
    
    dx = -1 * COLOR_SENSOR_LENGTH * Math.cos(Math.toRadians(thetaY / 2));
    dy = -1 * COLOR_SENSOR_LENGTH * Math.cos(Math.toRadians(thetaX / 2));
    
    // Update odometer values and move to origin
    
    odometer.setXYT(dx, dy, odometer.getXYT()[2]);
    travelTo(0.0, 0.0);
    
    leftMotor.setSpeed(ROTATE_SPEED/2);
    rightMotor.setSpeed(ROTATE_SPEED/2);
    
    // Rotate robot until it is at 0 degree
    
    if (odometer.getXYT()[2] > 10.0 && odometer.getXYT()[2] <= 350.0) {
      Sound.beep();
      leftMotor.rotate(radToDeg(-odometer.getXYT()[2]), true);
      rightMotor.rotate(-radToDeg(-odometer.getXYT()[2]), false);
    }
    
    // Stop motors
    
    leftMotor.stop(true);
    rightMotor.stop();
    
  }
  
  /**
   * This method makes the robot go to the origin
   * 
   */
  
  public void goToOrigin() {
    
    // Make the robot face the origin by turning 45 degrees
    
    turnTo(Math.PI / 4);
    
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    
    colorValue = readColorData();
    
    // Move robot until it detects a line
    
    while (colorValue > 0.38) {
      colorValue = readColorData();
      leftMotor.forward();
      rightMotor.forward();
    }
    
    // Interrupt motors and alert with sound
    
    leftMotor.stop(true);
    rightMotor.stop();
    Sound.beep();
    
    // Return to the origin
    
    leftMotor.rotate(distanceToRotations(-12), true);
    rightMotor.rotate(distanceToRotations(-12), false);
    
  }
  
  /**
   * This method calculates the vehicle displacement through x/y
   * and the turning angle (theta). It then sets the rotations 
   * needed to reach the x/y point. (taken from lab3)
   * 
   * @param x
   * @param y
   */
  
  public void travelTo(double x, double y) {
    
    // Initializes variables

    double dx, dy, displacement, theta;
    
    // Get the current x/y position

    double currentX = odometer.getXYT()[0];
    double currentY = odometer.getXYT()[1];
    
    // Calculate x and y components from destination
    
    dx = x - currentX;
    dy = y - currentY;
    
    // Calculate displacement and turning angle needed
    
    displacement = Math.hypot(dx, dy);
    theta = Math.atan2(dx, dy);
    
    // Turn to destination

    turnTo(theta);
    
    // Travel the calculated distance

    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);

    leftMotor.rotate(distanceToRotations(displacement), true);
    rightMotor.rotate(distanceToRotations(displacement), false);

    leftMotor.stop(true);
    rightMotor.stop(true);
  }
  
  /**
   * This method makes the robot turn to theta (taken from lab3)
   * 
   * @param theta
   */
  
  public void turnTo(double theta) {
    
    // Set rotate speed
    
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    
    // Get the minimum angle
    
    theta = getMinAngle(theta - Math.toRadians(odometer.getXYT()[2]));
    
    // If angle is negative, turn left
    
    if (theta < 0) {
      leftMotor.rotate(-radToDeg(-theta), true);
      rightMotor.rotate(radToDeg(-theta), false);
    }
    
    // If angle is positive, turn right
    
    else {
      leftMotor.rotate(radToDeg(theta), true);
      rightMotor.rotate(-radToDeg(theta), false);
    }
  }
  
  /**
   * This method returns the current value of the color sensor
   * 
   * @return
   */
  
  private float readColorData() {
    colourSensorValue.fetchSample(colourSensorValues, 0);
    return colourSensorValues[0];
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

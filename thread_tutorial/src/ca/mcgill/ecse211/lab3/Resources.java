package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid 
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>{@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {

  /**
   * The wheel radius.
   */
  public static final double WHEEL_RADIUS = 2.130;
  
  /**
   * The robot width.
   */
  public static final double TRACK = 15;
  
  /**
   * The left radius.
   */
  public static final double LEFT_RADIUS = 2.75;
  
  /**
   * The right radius.
   */
  public static final double RIGHT_RADIUS = 2.75;
  
  /**
   * The width.
   */
  public static final double WIDTH = 15.8;
  
  /**
   * The odometer timeout period.
   */
  public static final int TIMEOUT_PERIOD = 50;
  
  /**
   * The fast speed.
   */
  public static final int FAST = 200;
  
  /**
   * The slow speed.
   */
  public static final int SLOW = 100;
  
  /**
   * The acceleration.
   */
  public static final int ACCELERATION = 4000;
  
  /**
   * The degree error.
   */
  public static final double DEG_ERR = 3.0;
  
  /**
   * The cm error.
   */
  public static final double CM_ERR = 1.0;
  
  /**
   * The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
  
  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);
  
  /**
   * The ultrasonic sensor.
   */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
  
  /**
   * The color sensor.
   */
  public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);
  
  /**
   * The LCD.
   */
  public static final TextLCD LCD = LocalEV3.get().getTextLCD();
  
  /**
   * The ultrasonic poller.
   */
  public static UltrasonicPoller usPoller = new UltrasonicPoller();
  
  /**
   * The odometer.
   */
  public static Odometer odometer = new Odometer();
  
  /**
   * The obstacle avoidance.
   */
  public static ObstacleAvoidance obstacleAvoidance = new ObstacleAvoidance();

}

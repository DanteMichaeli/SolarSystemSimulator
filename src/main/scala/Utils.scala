// THIS FILE CONTAINTS ALL METHODS IMPLEMENTING THE PHYSICS AND NUMERICAL METHODS OF THE CELESTIAL BODIES, AS WELL AS CONSTANTS
import scala.math.*
import scala.collection.mutable.Buffer


//PHYSICS CONSTANTS:
val G = 6.6743*pow(10,-0.5) //m^3 * kg^(-1) * s^(-2)
val dt: Double = 0.5 //larger time-step means faster simulation, but less accurate


//GUI CONSTANTS
val GUIwidth: Int = 800
val GUIheight: Int = 800
val scalingFactor = 65/696340e30 //adjust based on set sun radius of 65 pixels

//calculates new velocity and position vectors of body based on current position, velocity and acceleration and new acceleration.
def velocityVerlet(currentPos: Vector2D, currentVel: Vector2D, currentAcc: Vector2D, newAcc: Vector2D): (Vector2D, Vector2D) =
  val newPos = currentPos + currentVel * dt + currentAcc * pow(dt,2) * 0.5
  val newVel = currentVel + (currentAcc + newAcc) * dt * 0.5
  (newPos, newVel)
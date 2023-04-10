// THIS FILE CONTAINTS ALL METHODS IMPLEMENTING THE PHYSICS AND NUMERICAL METHODS OF THE CELESTIAL BODIES, AS WELL AS CONSTANTS
import scala.math.*
import scala.collection.mutable.Buffer


//PHYSICS CONSTANTS:
val G = 6.6743*pow(10,-11) //m^3 * kg^(-1) * s^(-2)
var dayAdjuster = 10.0
var dt: Double = (60*60*24*dayAdjuster) / 60 //larger time-step means faster simulation, but less accurate. In seconds (SI-units). Divide by 60 since the gui updates 60 fps


//GUI CONSTANTS
val GUIwidth: Int = 1600
val GUIheight: Int = 800
val scalingFactor: Double = 2000000000.00   // 1 pixel = 2 billion meters

//calculates new velocity and position vectors of body based on current position, velocity and acceleration and new acceleration.
def velocityVerlet(currentPos: Vector2D, currentVel: Vector2D, currentAcc: Vector2D, newAcc: Vector2D): (Vector2D, Vector2D) =
  val newPos = currentPos + (currentVel * dt + currentAcc * pow(dt,2) * 0.5) * (1 / scalingFactor)
  val newVel = currentVel + (currentAcc + newAcc) * dt * 0.5
  (newPos, newVel)
  

//CUSTOM EXCEPTIONS
class NonPositiveMassException extends IllegalArgumentException("Non-positive mass error - Make sure none of your masses are negative or zero.")
class NonPositiveRadiusException extends IllegalArgumentException("Non-positive radius error - Make sure none of your radii are negative or zero.")
class NegativeCoordinatesException extends IllegalArgumentException("Negative coordinates error - Make sure none of your initializing coordinates are negative.")
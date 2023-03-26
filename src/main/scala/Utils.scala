// THIS FILE CONTAINTS ALL METHODS IMPLEMENTING THE PHYSICS AND NUMERICAL METHODS OF THE CELESTIAL BODIES, AS WELL AS CONSTANTS
import scala.math.*
import scala.collection.mutable.Buffer


//PHYSICS CONSTANTS:
val G = 6.6743*pow(10,-11) //m^3 * kg^(-1) * s^(-2)
val dt: Double = 1 //1 day timestep


//GUI CONSTANTS
val GUIwidth: Int = 800
val GUIheight: Int = 800
val neptuneAphelion = 4558.857e9      //maximum distance from the sun
val scale: Double = (0.5*GUIwidth) / neptuneAphelion    //scaling factor for the GUI: 1 pixel = 1 million kilometers


//calculates new velocity and position vectors of body based on current position, velocity and acceleration and new acceleration.
def velocityVerlet(currentPos: Vector2D, currentVel: Vector2D, currentAcc: Vector2D, newAcc: Vector2D): (Vector2D, Vector2D) =
  val newPos = currentPos + currentVel * dt + currentAcc * pow(dt,2) * 0.5 * scale
  val newVel = currentVel + (currentAcc + newAcc) * dt * 0.5 * scale
  (newPos, newVel)
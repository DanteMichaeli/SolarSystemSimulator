// THIS FILE CONTAINTS ALL METHODS IMPLEMENTING THE PHYSICS AND NUMERICAL METHODS OF THE CELESTIAL BODIES, AS WELL AS CONSTANTS
import scala.math.*
import scala.collection.mutable.Buffer

//GUI CONSTANTS
val width = 800
val height = 800
val scale = 100/ AU   //scaling factor for the GUI: 100 pixels per AU


//PHYSICS CONSTANTS:
val G = 6.6743 * pow(10,-11) //m^3 * kg^(-1) * s^(-2)
val AU = 149597871*1000 //m
val dt = 3600 * 24 //1 day timestep


//calculates new velocity and position vectors of body based on current position, velocity and acceleration and new acceleration.
def velocityVerlet(currentPos: Vector2D, currentVel: Vector2D, currentAcc: Vector2D, newAcc: Vector2D): (Vector2D, Vector2D) =
  val newPos = currentPos + currentVel * dt + currentAcc * pow(dt,2) * 0.5
  val newVel = currentVel + (currentAcc + newAcc) * dt * 0.5
  (newPos, newVel)
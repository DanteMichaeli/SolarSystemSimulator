// THIS FILE CONTAINTS ALL METHODS IMPLEMENTING THE PHYSICS AND NUMERICAL METHODS OF THE CELESTIAL BODIES, AS WELL AS CONSTANTS
import scala.math.*

//GUI constants
val width = 800
val height = 800
val scale = 100/ AU   //scaling factor for the GUI: 100 pixels per AU


//Physics constants:
val G = 6.6743 * pow(10,-11) //m^3 * kg^(-1) * s^(-2)
val AU = 149597871*1000 //m
val dt = 3600 * 24 //1 day timestep


//Physics methods:
def gravitationalForces(body1: CelestialBody, body2: CelestialBody): (Double, Double) =
  val xDistance = body2.xPos - body1.xPos
  val yDistance = body2.yPos - body1.yPos
  val r = sqrt(pow(xDistance,2) + pow(yDistance,2))
  val angle = atan(yDistance / xDistance)
  val force = G * (body1.mass * body2.mass)/ pow(r,2)
  val xForce = force * cos(angle)
  val yForce = force * sin(angle)
  (xForce, yForce)

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


//PHYSICS METHODS:

//calculates force vector acting upon body 1 due to gravitation from body 2
def gravitationalForce(body1: CelestialBody, body2: CelestialBody): Vector2D =
  val distanceVector = Vector2D(body2.xPos - body1.xPos, body2.yPos-body1.yPos)
  val forceVector: Vector2D = distanceVector.normalized * ( G * (body1.mass * body2.mass) / pow(distanceVector.magnitude,2) )
  forceVector



//calculates total force vector acting upon a body due to all other bodies in the system
def totalForce(body: CelestialBody, bodies: Buffer[CelestialBody]): Vector2D =
  var forceGatherer = Vector2D(0,0)
  for otherBody <- bodies do
    if otherBody != body then
      forceGatherer += gravitationalForce(body, otherBody)
  forceGatherer


//updates and returns new acceleration values for a body based on forces and mass
def updateAcceleration(body: CelestialBody, bodies: Buffer[CelestialBody]): (Double, Double) =
  val xAcceleration = totalForce(body, bodies)(0) / body.mass
  val yAcceleration = totalForce(body, bodies)(1) / body.mass
  body.xAcc = xAcceleration
  body.yAcc = yAcceleration
  (xAcceleration, yAcceleration)

//calculates new velocity and position of body based on current position, velocity and acceleration and new acceleration.
def velocityVerlet(currentPos: Double, currentVel: Double, currentAcc: Double, newAcc: Double): (Double, Double) =
  val newPos = currentPos + currentVel * dt + 0.5 * currentAcc * pow(dt,2)
  val newVel = currentVel + 0.5 * (currentAcc + newAcc) * dt
  (newPos, newVel)
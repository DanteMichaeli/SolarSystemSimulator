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
  val distanceVector = body2.pos - body1.pos //distansvektorn = differensen av kropparnas ortsvektorer från top left corner (0,0)
  val forceVector: Vector2D = distanceVector.normalized * ( G * (body1.mass * body2.mass) / pow(distanceVector.magnitude,2) )
  forceVector



//calculates total force vector acting upon a body due to all other bodies in the system
def totalForce(body: CelestialBody, bodies: Buffer[CelestialBody]): Vector2D =
  var forceGatherer = Vector2D(0,0)
  for otherBody <- bodies do
    if otherBody != body then
      forceGatherer += gravitationalForce(body, otherBody)
  forceGatherer


//updates and returns new acceleration vector for a body based on forces and mass. NOTE: also returns the acceleration vector to be used in updatePositions
def updateAcceleration(body: CelestialBody, bodies: Buffer[CelestialBody]) =
  body.acc = totalForce(body, bodies) * (1 / body.mass) // a = F/m = F * 1/m
  body.acc

//calculates new velocity and position vectors of body based on current position, velocity and acceleration and new acceleration.
def velocityVerlet(currentPos: Vector2D, currentVel: Vector2D, currentAcc: Vector2D, newAcc: Vector2D): (Vector2D, Vector2D) =
  val newPos = currentPos + currentVel * dt + currentAcc * pow(dt,2) * 0.5
  val newVel = currentVel + (currentAcc + newAcc) * dt * 0.5
  (newPos, newVel)
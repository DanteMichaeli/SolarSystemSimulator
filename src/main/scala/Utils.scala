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

//calculates forces acting upon a body due to gravitation from another body
def gravitationalForces(body1: CelestialBody, body2: CelestialBody): (Double, Double) =
  val xDistance = body2.xPos - body1.xPos
  val yDistance = body2.yPos - body1.yPos
  val r = sqrt(pow(xDistance,2) + pow(yDistance,2))
  val angle = atan(yDistance / xDistance)
  val force = G * (body1.mass * body2.mass)/ pow(r,2)
  val xForce = force * cos(angle)
  val yForce = force * sin(angle)
  (xForce, yForce)


//calculates total x- and y-forces on all bodies due to the rest of the bodies
def totalForces(body: CelestialBody, bodies: Buffer[CelestialBody]): (Double, Double) =
  var totalX = 0.0
  var totalY = 0.0
  for otherBody <- bodies do
    if otherBody != body then
      totalX += gravitationalForces(body, otherBody)(0)
      totalY += gravitationalForces(body, otherBody)(1)
  (totalX, totalY)


//updates and returns new acceleration values for a body based on forces and mass
def updateAcceleration(body: CelestialBody, bodies: Buffer[CelestialBody]): (Double, Double) =
  val xAcceleration = totalForces(body, bodies)(0) / body.mass
  val yAcceleration = totalForces(body, bodies)(1) / body.mass
  body.xAcc = xAcceleration
  body.yAcc = yAcceleration
  (xAcceleration, yAcceleration)

//calculates new velocity and position of body based on current position, velocity and acceleration and new acceleration.
def velocityVerlet(currentPos: Double, currentVel: Double, currentAcc: Double, newAcc: Double): (Double, Double) =
  val newPos = currentPos + currentVel * dt + 0.5 * currentAcc * pow(dt,2)
  val newVel = currentVel + 0.5 * (currentAcc + newAcc) * dt
  (newPos, newVel)
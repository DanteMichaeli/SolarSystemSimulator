//CLASS FOR THE SIMULATION LOGIG. EACH INSTANTIATION REPRESENTS A SINGLE SIMULATION
import scala.collection.mutable.Buffer
import scala.math.{atan, cos, pow, sin, sqrt}

class Simulation:
  var time = 0
  val celestialBodies = Buffer[CelestialBody]()

  //reads in a text file and instantiates the celestial bodies
  def parseData(): Unit = ???

  //updates all planets accelerations, positions and velocities
  def updatePositions(): Unit =
    for body <- celestialBodies do
      val newX = velocityVerlet( body.xPos, body.xVel, body.xAcc, updateAcceleration(body,celestialBodies) )
      val newY = velocityVerlet( body.yPos, body.yVel, body.yAcc, updateAcceleration(body,celestialBodies) )
      body.xPos = newX(0)
      body.yPos = newY(0)
      body.xVel = newX(1)
      body.yVel = newY(1)

  // updates the state of the entire simulation
  def timePasses(): Unit =
    updatePositions()
    time += dt




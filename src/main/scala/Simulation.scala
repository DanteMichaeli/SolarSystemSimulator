//CLASS FOR THE SIMULATION LOGIG. EACH INSTANTIATION REPRESENTS A SINGLE SIMULATION
import scala.collection.mutable.Buffer
import scala.math.{atan, cos, pow, sin, sqrt}

class Simulation:
  var time = 0
  val celestialBodies = Buffer[CelestialBody]()

  //reads in a text file and instantiates the celestial bodies
  def parseData(): Unit = ???

  //updates all planets accelerations, positions and velocities using the velocityVerlet method defined in Utils
  def updatePositions(): Unit =
    for body <- celestialBodies do
      val newValues = velocityVerlet( body.pos, body.vel, body.acc, body.updateAcceleration(celestialBodies) )
      body.pos = newValues(0)
      body.vel = newValues(1)

  // updates the state of the entire simulation
  def timePasses(): Unit =
    updatePositions()
    time += dt




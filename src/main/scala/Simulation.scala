//CLASS FOR THE SIMULATION LOGIG. EACH INSTANTIATION REPRESENTS A SINGLE SIMULATION
import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer
import scala.math.{atan, cos, pow, sin, sqrt}

class Simulation:
  var time = 0.00
  val celestialBodies = Buffer[CelestialBody]()

  //reads in a (properly formatted) text file and instantiates the celestial bodies

  def parseData() =
    val source = scala.io.Source.fromFile("theSolarSystem.txt")
    val lines = source.getLines().toList
    source.close()

    var counter = 0
    for line <- lines do

      val cols = line.split(", ").map(_.trim)
      if counter == 0 then
        val body = new Sun( cols(0), cols(1).toDouble, cols(2).toDouble, Vector2D(cols(3).toDouble, cols(4).toDouble), Vector2D(0,0), Color.web(cols(5)) )
        celestialBodies += body
      else
        val body = new Planet( cols(0), cols(1).toDouble, cols(2).toDouble, Vector2D(cols(3).toDouble, cols(4).toDouble), Vector2D(cols(5).toDouble, cols(6).toDouble), Color.web(cols(7)) )
        celestialBodies += body

      counter += 1



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




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

    var firstLine = true

    for line <- lines do
      if firstLine then
        time = line.toDouble //time (in seconds) for how long to run the simulation.
        firstLine = false
      else
        val cols = line.split(", ").map(_.trim)
        if cols(0) == "sun" then
          val body = new Sun( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(0,0), Color.web(cols(6)) )
          celestialBodies += body
        else if cols(0) == "pla" then
          val body = new Planet( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)) )
          celestialBodies += body
        else
          val body = new Satellite( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)) )
          celestialBodies += body



  //updates all planets accelerations, positions and velocities using the velocityVerlet method defined in Utils
  def updatePositions(): Unit =
    for body <- celestialBodies do
      val newValues = velocityVerlet( body.pos, body.vel, body.acc, body.updateAcceleration(celestialBodies) )
      body.pos = newValues(0)
      body.vel = newValues(1)

  // updates the state of the entire simulation
  def timePasses(): Unit =
    celestialBodies.foreach( (body: CelestialBody) => body.trajectory += body.pos )
    updatePositions()




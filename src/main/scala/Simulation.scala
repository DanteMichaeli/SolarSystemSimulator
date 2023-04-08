//CLASS FOR THE SIMULATION LOGIG. EACH INSTANTIATION REPRESENTS A SINGLE SIMULATION
import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer
import scala.math.{atan, cos, pow, sin, sqrt}

class Simulation:
  var time = 0.00
  val celestialBodies = Buffer[CelestialBody]()

  //reads in a (properly formatted) text file and instantiates the celestial bodies or throws an exception
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
          try
            if cols(2).toDouble <= 0 then throw new IllegalArgumentException("Non-positive radius error - Make sure none of your radii are negative or zero.")
            if cols(3).toDouble <= 0 then throw new IllegalArgumentException("Non-positive mass error - Make sure none of your masses are negative or zero.")
            if cols(4).toDouble < 0 || cols(5).toDouble < 0 then throw new IllegalArgumentException("Negative coordinates error - Make sure none of your initializing coordinates are negative.")

            if cols(0) == "sun" then
              val body = new Sun( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(0,0), Color.web(cols(6)) )
              celestialBodies += body
            else if cols(0) == "pla" then
              val body = new Planet( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)) )
              celestialBodies += body
            else
              val body = new Satellite( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)) )
              celestialBodies += body

          catch
            case e: IllegalArgumentException => throw e
            case _: Exception => throw new IllegalArgumentException("File Structure Error - Make sure your input file has the correct structure:\n\ntime of simulation (s)\nplanet type (3-letter), name, mass (kg), radius (m), x position (px), y position (px), x velocity (m/s), y velocity (m/s), color (hex code)\n.\n.\n.")



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




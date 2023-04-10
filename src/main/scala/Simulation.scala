//CLASS FOR THE SIMULATION LOGIG. EACH INSTANTIATION REPRESENTS A SINGLE SIMULATION
import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer
import scala.math.{atan, cos, pow, sin, sqrt}

class Simulation:
  var name = ""
  var time = 0.00
  val celestialBodies = Buffer[CelestialBody]()
  var collision = false
  var collisionData = ""


  //reads in a (properly formatted) text file and instantiates the celestial bodies or throws an exception
  def parseData(fileName: String) =
    name = fileName
    val source = scala.io.Source.fromFile(fileName)
    val lines = source.getLines().toList
    source.close()

    var counter = 0

    try
      for line <- lines do
          if counter == 0 then
            time = line.toDouble //time (in seconds) for how long to run the simulation.
            counter += 1
          else if counter == 1 then
            dayAdjuster = line.toDouble //user-input on dt (in days) for the timestep
            counter += 1
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
              case _: Exception => throw new IllegalArgumentException("File Structure Error - Make sure your input file has the correct structure:\n\ntime of simulation (s)\ntimestep dt (days)\nplanet type (3-letter), name, mass (kg), radius (m), x position (px), y position (px), x velocity (m/s), y velocity (m/s), color (hex code)\n.\n.\n.")
    catch
      case _: Exception => throw new IllegalArgumentException("File Structure Error - Make sure your input file has the correct structure:\n\ntime of simulation (s)\ntimestep dt (days)\nplanet type (3-letter), name, mass (kg), radius (m), x position (px), y position (px), x velocity (m/s), y velocity (m/s), color (hex code)\n\nConsider exampleFile.txt for more guidance")


  //method for writing and saving the simulation data to a .txt file, to be saved on the user's computer. In a format that can be read by the parseData method
  def saveData(fileName: String): Unit =
    val file = new java.io.File(fileName)
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(file))
    bw.write(time.toString + "\n")
    bw.write(dayAdjuster.toString + "\n")
    for n <- celestialBodies.indices do
      if n == 0 then
        bw.write(s"${celestialBodies(n).sort}, ${celestialBodies(n).name}, ${celestialBodies(n).radius}, ${celestialBodies(n).mass}, ${celestialBodies(n).pos.x}, ${celestialBodies(n).pos.y}, ${celestialBodies(n).colorCode}\n")
      else if n == celestialBodies.length-1 then
        bw.write(s"${celestialBodies(n).sort}, ${celestialBodies(n).name}, ${celestialBodies(n).radius}, ${celestialBodies(n).mass}, ${celestialBodies(n).pos.x}, ${celestialBodies(n).pos.y}, ${celestialBodies(n).vel.x}, ${celestialBodies(n).vel.y}, ${celestialBodies(n).colorCode}")
      else
        bw.write(s"${celestialBodies(n).sort}, ${celestialBodies(n).name}, ${celestialBodies(n).radius}, ${celestialBodies(n).mass}, ${celestialBodies(n).pos.x}, ${celestialBodies(n).pos.y}, ${celestialBodies(n).vel.x}, ${celestialBodies(n).vel.y}, ${celestialBodies(n).colorCode}\n")
    bw.close()


  //checks if any two celestial bodies have collided (also checks with itself, but makes code easier)
  def detectCollisions(): Unit =
    for i <- celestialBodies.indices do
      for j <- i+1 until celestialBodies.length do
        if celestialBodies(i).isColliding(celestialBodies(j)) then
          collision = true
          collisionData = "Collision detected between " + celestialBodies(i).name + " and " + celestialBodies(j).name + " at time " + time + "."
    


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
    detectCollisions()






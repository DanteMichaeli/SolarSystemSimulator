//CLASS FOR THE SIMULATION LOGIG. EACH INSTANTIATION REPRESENTS A SINGLE SIMULATION
import scalafx.scene.paint.Color
import java.io.IOException
import scala.collection.mutable.Buffer
import scala.math.{atan, cos, pow, sin, sqrt}

class Simulation:

  var name = ""
  var time = 0.00
  val celestialBodies = Buffer[CelestialBody]()
  var bodyOnDisplay: Option[CelestialBody] = None
  var collision = false
  var collisionData = ""
  
  //GUI TOGGLES
  var trajectoriesOn = false
  var directionVectorsOn = false
  var accelerationVectorsOn = false
  var lagrangeLinesOn = false
  var isPaused = false


  def parseData(fileName: String): Unit =
      name = fileName
      val source = scala.io.Source.fromFile(fileName)
      val lines = source.getLines().toList
      source.close()
      var counter = 0
        for line <- lines do
            if counter == 0 then
              time = line.toDouble
              if time <= 0 then throw new IllegalArgumentException("Simulation time must be positive.")
              counter += 1
            else if counter == 1 then
              dayAdjuster = line.toDouble //user-input on dt (in days) for the timestep
              if dayAdjuster <= 0 then throw new IllegalArgumentException("Timestep must be positive.")
              counter += 1
            else
              val cols = line.split(", ").map(_.trim)
                if cols.length != 9 then
                  throw new IllegalArgumentException("Invalid input format. Please enter the new body in the format:\nsort, name, radius, mass, x-pos, y-pos, x-vel, y-vel, color code.")
                else if cols(2).toDouble <= 0 then
                  throw new IllegalArgumentException(s"Radius of ${cols(1)} must be positive.")
                else if cols(3).toDouble <= 0 then
                  throw new IllegalArgumentException(s"Mass of ${cols(1)} must be positive.")
                else if cols(4).toDouble < 0 || cols(5).toDouble < 0 then
                 throw new IllegalArgumentException(s"Initial position of ${cols(1)} must not be negative.")
                else
                  if cols(0) == "sun" then
                    celestialBodies += new Sun( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)) )
                  else if cols(0) == "pla" then
                    celestialBodies += new Planet( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)) )
                  else if cols(0) == "sat" then
                    celestialBodies += new Satellite( cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)) )
                  else
                    throw new IllegalArgumentException(s"Invalid sort: ${cols(0)}. Body must be of sort 'sun', 'pla', or 'sat'.")

  def saveData(fileName: String): Unit =
    val file = new java.io.File(fileName)
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(file))
    bw.write(time.toString + "\n")
    bw.write(dayAdjuster.toString + "\n")
    for n <- celestialBodies.indices do
      if n == celestialBodies.length-1 then
        bw.write(s"${celestialBodies(n).sort}, ${celestialBodies(n).name}, ${celestialBodies(n).radius}, ${celestialBodies(n).mass}, ${celestialBodies(n).pos.x}, ${celestialBodies(n).pos.y}, ${celestialBodies(n).vel.x}, ${celestialBodies(n).vel.y}, ${celestialBodies(n).colorCode}")
      else
        bw.write(s"${celestialBodies(n).sort}, ${celestialBodies(n).name}, ${celestialBodies(n).radius}, ${celestialBodies(n).mass}, ${celestialBodies(n).pos.x}, ${celestialBodies(n).pos.y}, ${celestialBodies(n).vel.x}, ${celestialBodies(n).vel.y}, ${celestialBodies(n).colorCode}\n")
    bw.close()

  def detectCollisions(): Unit =
    for i <- celestialBodies.indices; j <- i+1 until celestialBodies.length do
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
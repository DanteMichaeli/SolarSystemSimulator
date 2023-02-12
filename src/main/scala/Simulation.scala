//CLASS FOR THE SIMULATION LOGIG. EACH INSTANTIATION REPRESENTS A SINGLE SIMULATION
import scala.collection.mutable.Buffer
import scala.math.{atan, cos, pow, sin, sqrt}

class Simulation:

  val celestialBodies = Buffer[CelestialBody]()


  //reads in a text file and instantiates the celestial bodies
  def parseData(): Unit = ???


  //method onTick to update everything (all planets forces, positions, accelerations et.c. Should use above methods)
  def updateState(): Unit = ???
  //calculate total force for all bodies
  //velocity verlet using old and new acceleration


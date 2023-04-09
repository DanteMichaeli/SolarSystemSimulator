// THE ABSTRACT SUPERCLASS CELESTIALBODY, AS WELL AS ITS SUB-CLASSES SUN, PLANET AND SATELLITE
import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer
import scala.math.pow

//mass: kg, radius: m
abstract class CelestialBody(val name: String, val radius: Double, val mass: Double, var pos: Vector2D, var vel: Vector2D, val color: scalafx.scene.paint.Color):

  //var distanceToSun = ??? //distance between the planet's center of mass and the sun's
  var trajectory = Buffer[Vector2D](this.pos) //stores pos vectors through time for drawing trajectories. Starts with the initial position of the body
  var acc: Vector2D = Vector2D(0,0) //will be immediately recalculated with total force acting on the body

  //calculates force vector acting upon this body due to gravitation from another body
  def gravitationalForce(another: CelestialBody): Vector2D =
    val distanceVector = (another.pos - this.pos) * scalingFactor //distansvektorn = differensen av kropparnas ortsvektorer från top left corner (0,0)
    val forceVector: Vector2D = distanceVector.normalized * ( G * (this.mass * another.mass) / pow(distanceVector.magnitude,2) )
    forceVector

  //calculates and returns total force vector acting upon this body due to all other bodies in the system
  def totalForce(bodies: Buffer[CelestialBody]): Vector2D =
    var forceGatherer = Vector2D(0,0)
    for another <- bodies do
      if another != this then
        forceGatherer += gravitationalForce(another)
    forceGatherer

  //updates and returns new acceleration vector for a body based on forces and mass. NOTE: also returns the acceleration vector to be used in updatePositions
  def updateAcceleration(bodies: Buffer[CelestialBody]) =
    this.acc = totalForce(bodies) * (1 / this.mass) // a = F/m = F * 1/m
    this.acc
    
  //compares distance between two bodies to their sum of radii to check for collision
  def isColliding(another: CelestialBody): Boolean =
    val distance = (this.pos - another.pos).magnitude
    val sumOfRadii = this.radius + another.radius
    if distance <= sumOfRadii then true else false




class Sun(name: String, radius: Double, mass: Double, pos: Vector2D, vel: Vector2D, color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
end Sun


class Planet(name: String, radius: Double, mass: Double, pos: Vector2D, vel: Vector2D, color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
end Planet


class Satellite(name: String, radius: Double, mass: Double, pos: Vector2D, vel: Vector2D, color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
end Satellite



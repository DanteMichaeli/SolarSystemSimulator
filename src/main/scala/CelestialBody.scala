import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer
import scala.math.pow

abstract class CelestialBody(val name: String, var radius: Double, val mass: Double, var pos: Vector2D, var vel: Vector2D, val color: scalafx.scene.paint.Color):

  var trajectory = Buffer[Vector2D](this.pos)
  var acc: Vector2D = Vector2D(0,0)
  var colorCode: String = "#" + color.toString.substring(7,13).toUpperCase //used for saving files, converts back from color to hex code
  val sort: String

  def gravitationalForce(another: CelestialBody): Vector2D =
    val distanceVector = (another.pos - this.pos) * scalingFactor
    val forceVector: Vector2D = distanceVector.normalized * ( G * (this.mass * another.mass) / pow(distanceVector.magnitude,2) )
    forceVector

  def totalForce(bodies: Buffer[CelestialBody]): Vector2D =
    var forceGatherer = Vector2D(0,0)
    for another <- bodies do
      if another != this then
        forceGatherer += gravitationalForce(another)
    forceGatherer

  //NOTE: also returns the acceleration vector to be used in Velocity Verlet for updating positions
  def updateAcceleration(bodies: Buffer[CelestialBody]) =
    this.acc = totalForce(bodies) * (1 / this.mass) // a = F/m = F * 1/m
    this.acc

  def isColliding(another: CelestialBody): Boolean =
    val distance = (this.pos - another.pos).magnitude
    val sumOfRadii = this.radius + another.radius
    if distance <= sumOfRadii then true else false

  def distanceTo(other: CelestialBody): Double =
    val distance = (this.pos - other.pos).magnitude
    distance


class Sun(name: String, radius: Double, mass: Double, pos: Vector2D, vel: Vector2D, color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
  val sort = "sun"
end Sun

class Planet(name: String, radius: Double, mass: Double, pos: Vector2D, vel: Vector2D, color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
  val sort = "pla"
end Planet

class Satellite(name: String, radius: Double, mass: Double, pos: Vector2D, vel: Vector2D, color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
  val sort = "sat"
end Satellite
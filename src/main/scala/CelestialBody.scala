// THE ABSTRACT SUPERCLASS CELESTIALBODY, AS WELL AS ITS SUB-CLASSES SUN, PLANET AND SATELLITE
import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer

//mass: kg, radius: m
abstract class CelestialBody(val name: String, val radius: Double, val mass: Double, var pos: Vector2D, var vel: Vector2D, val color: scalafx.scene.paint.Color):
  var distanceToSun = ??? //distance between the planet's center of mass and the sun's
  var orbit = Buffer[Vector2D]() //stores pos vectors through time for drawing trajectories
  var acc: Vector2D = Vector2D(0,0) //will be immediately recalculated with total force acting on the body




class Sun(val name: String, val radius: Double, val mass: Double, var pos: Vector2D, val vel: Vector2D, val color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
end Sun


class Planet(val name: String, val radius: Double, val mass: Double, var pos: Vector2D, val vel: Vector2D, val color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
end Planet


class Satellite(val name: String, val radius: Double, val mass: Double, var pos: Vector2D, val vel: Vector2D, val color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, pos, vel, color):
end Satellite



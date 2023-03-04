// THE ABSTRACT SUPERCLASS CELESTIALBODY, AS WELL AS ITS SUB-CLASSES SUN, PLANET AND SATELLITE
import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer

//mass: kg, radius: m
abstract class CelestialBody(val name: String, val radius: Double, val mass: Double, var xPos: Double, var yPos: Double, var xVel: Double, var yVel: Double, val color: scalafx.scene.paint.Color):
  var distanceToSun = ??? //distance between the planet's center of mass and the sun's
  var orbit = Buffer[(Double, Double)]() //stores xPos and yPos pairs through time for drawing trajectories
  var xAcc: Double = 0 //will be immediately recalculated with total force acting on the body
  var yAcc: Double = 0



class Sun(val name: String, val radius: Double, val mass: Double, var xPos: Double, var yPos: Double, val xVel: Double, val yVel: Double, val color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, xPos, yPos, xVel, yVel, color):
end Sun


class Planet(val name: String, val radius: Double, val mass: Double, var xPos: Double, var yPos: Double, val xVel: Double, val yVel: Double, val color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, xPos, yPos, xVel, yVel, color):
end Planet


class Satellite(val name: String, val radius: Double, val mass: Double, var xPos: Double, var yPos: Double, val xVel: Double, val yVel: Double, val color: scalafx.scene.paint.Color) extends CelestialBody(name, radius, mass, xPos, yPos, xVel, yVel, color):
end Satellite



// THIS FILE CONTAINTS AUXILIARY METHODS AND CONSTANTS FOR THE PHYSICS AN GUI:
import javafx.scene.shape.Circle
import scalafx.scene.Group
import scalafx.Includes.jfxCircle2sfx
import scala.math.*
import scala.collection.mutable.Buffer



//PHYSICS CONSTANTS:
val G = 6.6743*pow(10,-11) //m^3 * kg^(-1) * s^(-2)
var dayAdjuster = 10.0
var dt: Double = (60*60*24*dayAdjuster) / 60 //larger time-step means faster simulation, but less accurate. In seconds (SI-units). Divide by 60 since the gui updates 60 fps



//VELOCITY VERLET ALGORITHM: CALCULATES NEW VELOCITY AND POSITION OF BODY BASED ON CURRENT POSITION, VELOCITY AND ACCELERATION AND NEW ACCELERATION
def velocityVerlet(currentPos: Vector2D, currentVel: Vector2D, currentAcc: Vector2D, newAcc: Vector2D): (Vector2D, Vector2D) =
  val newPos = currentPos + (currentVel * dt + currentAcc * pow(dt,2) * 0.5) * (1 / scalingFactor)
  val newVel = currentVel + (currentAcc + newAcc) * dt * 0.5
  (newPos, newVel)



//GUI CONSTANTS
val GUIwidth: Double = 1500
val GUIheight: Double = 800
val scalingFactor: Double = 2000000000.00   // 1 pixel = 2 billion meters
val zoomFactor = 1/2.0



//GUI AUXILIARY METHODS:
def outOfBounds(circle: Circle): Boolean =
  val leftX = circle.centerX() - circle.radius()
  val rightX = circle.centerX() + circle.radius()
  val topY = circle.centerY() - circle.radius()
  val bottomY = circle.centerY() + circle.radius()
  leftX < 0 || rightX > GUIwidth || topY < 0 || bottomY > GUIheight

def zoomOut(group: Group): Unit =
  group.setScaleX(zoomFactor)
  group.setScaleY(zoomFactor)
  val bounds = group.getBoundsInParent
  val centerX = (bounds.getMinX + bounds.getMaxX) / 2
  val centerY = (bounds.getMinY + bounds.getMaxY) / 2
  val translateX = (GUIwidth / 2) - centerX
  val translateY = (GUIheight / 2) - centerY
  group.setTranslateX(translateX)
  group.setTranslateY(translateY)

def drawBodies(simulation: Simulation): Group =
  val bodies = simulation.celestialBodies
  val group = new Group
  for body <- bodies do
    val circle = new Circle
    circle.setCenterX(body.pos.x)
    circle.setCenterY(body.pos.y)
    circle.setRadius(body.radius)
    circle.setFill(body.color)
    group.getChildren.add(circle)
    circle.setOnMouseClicked( e =>
      if simulation.bodyOnDisplay == Some(body) then
        simulation.bodyOnDisplay = None
      else
        simulation.bodyOnDisplay = Some(body)
    )

  group
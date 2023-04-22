// THIS FILE CONTAINTS AUXILIARY METHODS AND CONSTANTS FOR THE PHYSICS AN GUI:
import SolarSystemSimulatorApp.domain
import javafx.scene.shape.Circle
import scalafx.scene.Group
import scalafx.Includes.jfxCircle2sfx
import scalafx.scene.paint.Color.{Purple, White}
import scalafx.scene.shape.{Line, Polygon, Polyline}
import scala.collection.mutable
import scala.math.*
import scala.collection.mutable.Buffer
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//PHYSICS CONSTANTS:
val G = 6.6743*pow(10,-11) //m^3 * kg^(-1) * s^(-2)
var dayAdjuster = 10.0
var dt: Double = (60*60*24*dayAdjuster) / 60 //larger time-step means faster simulation, but less accurate. In seconds (SI-units). Divide by 60 since the gui updates 60 fps

//VELOCITY VERLET ALGORITHM: CALCULATES NEW VELOCITY AND POSITION OF BODY BASED ON CURRENT POSITION, VELOCITY AND ACCELERATION AND NEW ACCELERATION
def velocityVerlet(currentPos: Vector2D, currentVel: Vector2D, currentAcc: Vector2D, newAcc: Vector2D): (Vector2D, Vector2D) =
  val newPos = currentPos + (currentVel * dt + currentAcc * pow(dt,2) * 0.5) * (1 / scalingFactor)
  val newVel = currentVel + (currentAcc + newAcc) * dt * 0.5
  (newPos, newVel)
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
  val group = new Group
  for body <- simulation.celestialBodies do
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

def drawTrajectories(simulation: Simulation, toggled: Boolean): Group =
  val group = new Group
  if toggled then
    for body <- simulation.celestialBodies do
      val polyline = new Polyline()
      polyline.setStroke(body.color)
      polyline.setStrokeWidth(1)
      group.getChildren.add(polyline)
      for (pos, i) <- body.trajectory.zipWithIndex do
        if i % 15 == 0 then polyline.getPoints.addAll(pos.x, pos.y)  //only trace every 15th point to reduce lag
  group

def drawVectors(simulation: Simulation, vectorCode: String, color: String, toggled: Boolean): Group =
  val group = new Group
  if toggled then
    for body <- simulation.celestialBodies do
      if body.sort != "sun" then
        val direction = if vectorCode == "vel" then body.vel.normalized else body.acc.normalized
        val angle = math.atan2(direction.y, direction.x) * 180 / math.Pi
        val arrowLength = body.radius + 10
        //endpoint of arrow
        val endX = body.pos.x + direction.x * arrowLength
        val endY = body.pos.y + direction.y * arrowLength
        //line segment
        val segment = new Line()
        segment.setStartX(body.pos.x)
        segment.setStartY(body.pos.y)
        segment.setEndX(endX)
        segment.setEndY(endY)
        segment.setStroke(if color == "white" then White else Purple)
        //arrowhead, a triangle pointing in the direction of the vector
        val arrowheadSize = 5
        val arrowhead = new Polygon()
        arrowhead.getPoints.addAll(endX, endY,
          endX-arrowheadSize*math.cos(angle*math.Pi/180+math.Pi/6),
          endY-arrowheadSize*math.sin(angle*math.Pi/180+math.Pi/6),
          endX-arrowheadSize*math.cos(angle*math.Pi/180-math.Pi/6),
          endY-arrowheadSize*math.sin(angle*math.Pi/180-math.Pi/6)
        )
        arrowhead.setFill(if color == "white" then White else Purple)
        group.getChildren.addAll(segment, arrowhead)
  group

def drawLagrangeLines(simulation: Simulation, toggled: Boolean): Group =
  val group = new Group
  if toggled then
    val bodies = simulation.celestialBodies
    for n <- bodies; m <- bodies if n != m do
      val line = new Line()
      line.setStartX(n.pos.x)
      line.setStartY(n.pos.y)
      line.setEndX(m.pos.x)
      line.setEndY(m.pos.y)
      line.setStroke(White)
      line.setStrokeWidth(1)
      group.getChildren.add(line)
  group

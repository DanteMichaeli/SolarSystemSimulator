import SolarSystemSimulatorApp.stage
import javafx.scene.shape.Circle
import scalafx.application.JFXApp3
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.animation.AnimationTimer


object SolarSystemSimulatorApp extends JFXApp3 :

  //domain of the simulation
  val domain = new Simulation
  domain.parseData()

  //method for drawing the celestial bodies of Simulation into the GUI app:
  def drawBodies(): Group =
    val bodies = domain.celestialBodies
    val group = new Group
    for body <- bodies do
      val circle = new Circle
      circle.setCenterX(body.pos.x)
      circle.setCenterY(body.pos.y)
      circle.setRadius(body.radius)
      circle.setFill(body.color)
      group.getChildren.add(circle)
    stage.scene.value.content = group
    group

  override def start(): Unit =
  //create a new ScalaFX stage with all that is needed to display the celestial Bodies in their correct positions
    stage = new JFXApp3.PrimaryStage:
      title = "Solar System Simulator"
      width = GUIwidth
      height = GUIheight
      scene = new Scene:
        fill = Color.Black

    stage.scene().content = drawBodies()

 //animation using the AnimationTimer
    val timer = AnimationTimer(t =>
      domain.timePasses()
      drawBodies()
    )
    timer.start()


end SolarSystemSimulatorApp
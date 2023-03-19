import SolarSystemSimulatorApp.stage
import javafx.scene.shape.Circle
import scalafx.application.JFXApp3
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.animation.Timeline
import scalafx.animation.KeyFrame
import scalafx.util.Duration
import scala.language.postfixOps


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

  stage.onShown = _ =>
//create a ScalaFX timeline for updating the GUI on every tick
    val timeline = new Timeline:
      cycleCount = Timeline.Indefinite
      keyFrames = Seq(KeyFrame(Duration(16), onFinished = _ =>
        domain.timePasses()
        stage.scene.value.content = drawBodies()
      ))
    timeline.play()

  //start the simulation
  stage.show()


end SolarSystemSimulatorApp
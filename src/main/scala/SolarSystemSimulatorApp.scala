//THE GUI CODE, SEPARATE FROM THE INTERNAL LOGIC OF THE PROGRAM
import javafx.scene.shape.Circle
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle



object SolarSystemSimulatorApp extends JFXApp3:

  val domain = new Simulation()
  domain.parseData()


  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title.value = "Solar System Simulator"
      width = 800
      height = 800

      scene = new Scene:
          fill = Black
          def drawCelestialBodies(): Unit =
            domain.celestialBodies.foreach( (body: CelestialBody) => content += new Circle(body.pos.x, body.pos.y, body.radius, body.color) )

          content = drawCelestialBodies() //get the celestial Bodies drawn onto the scene

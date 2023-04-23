import SolarSystemSimulatorApp.stage
import javafx.scene.shape.{Circle, LineTo, MoveTo, Path, PathElement}
import scalafx.application.JFXApp3
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color

object SolarSystemSimulatorApp extends JFXApp3 :

  var domain = new Simulation
  domain.parseData("theSolarSystem.txt")

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "Solar System Simulator"
      width = GUIwidth
      height = GUIheight
      scene = new Scene:
        fill = Color.Black

    setupDisplays()
    setupMenu()
    setupMenuActions()
    setupButtons()
    setupButtonActions()
    setupTimer()

end SolarSystemSimulatorApp
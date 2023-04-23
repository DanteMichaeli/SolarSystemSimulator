import SolarSystemSimulatorApp.stage
import javafx.scene.shape.{Circle, LineTo, MoveTo, Path, PathElement}
import scalafx.application.JFXApp3
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.animation.{AnimationTimer, FadeTransition}
import scalafx.scene.control.{Alert, Button, CheckMenuItem, Label, Menu, MenuBar, MenuItem, RadioMenuItem, SeparatorMenuItem, Slider, ToggleGroup}
import scalafx.scene.paint.Color.{Pink, Purple, White}
import scalafx.scene.shape.{Line, Polygon, Polyline}
import scala.collection.mutable
import scala.language.postfixOps
import javafx.beans.property.SimpleDoubleProperty
import javafx.util.Duration
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.FileChooser
import java.io.IOException
import scalafx.Includes.*
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.scene.control.TextInputDialog
import scalafx.scene.transform.Scale
import java.math.RoundingMode


object SolarSystemSimulatorApp extends JFXApp3 :


  //domain of the simulation
  var domain = new Simulation
  domain.parseData("theSolarSystem.txt")
  var isComplete = false


  override def start(): Unit =
  //ScalaFX stage with all that is needed to display the celestial Bodies in their correct positions
    stage = new JFXApp3.PrimaryStage:
      title = "Solar System Simulator"
      width = GUIwidth
      height = GUIheight
      scene = new Scene:
        fill = Color.Black


    def drawSimulation(): Group =

      val bodiesGroup = drawBodies(domain)
      val trajectoriesGroup = drawTrajectories(domain)
      val dirVectorsGroup = drawVectors(domain, "vel", "white", domain.directionVectorsOn)
      val accVectorsGroup = drawVectors(domain, "acc", "purple", domain.accelerationVectorsOn)
      val lagrangeLinesGroup = drawLagrangeLines(domain)
      val simulationGroup = new Group(bodiesGroup, trajectoriesGroup, dirVectorsGroup, accVectorsGroup, lagrangeLinesGroup)
      val circlesWithBodies = bodiesGroup.getChildren.zip(domain.celestialBodies)
      for circleWithBody <- circlesWithBodies do
        if outOfBounds((circleWithBody._1).asInstanceOf[Circle]) then
          zoomOut(simulationGroup)
          displayMessage(s"Planet ${circleWithBody._2.name} has ventured out of local scope. Zooming out.")
      simulationGroup

    setupDisplays()


    setupMenu()
    setupMenuActions()
    setupButtons()
    setupButtonActions()




  //animation timer for the gui, that pauses if variable isPaused is true. Updates the GUI at ≈ 60 fps
    val timer = AnimationTimer(t =>
      if !domain.isPaused && domain.time > 0 && !domain.collision then
        domain.timePasses()

        stage.scene().content = Group(menuBar, playPause, reset, slider, timeLabel, messageDisplayer, infoDisplayer, drawSimulation())
        displayInfo()
        domain.time -= 1.0/60.0   //to account for refresh rate of 60 fps
        timeProperty.set(domain.time)

      else if domain.collision then
        displayMessage(domain.collisionData)

      else if domain.time <= 0 && !isComplete then
        displayMessage("Simulation complete.")
        isComplete = true
    )
    timer.start()



end SolarSystemSimulatorApp
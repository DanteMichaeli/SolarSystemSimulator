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


  // play/pause button for the gui:
    val playPause = new Button("Pause")
        playPause.setLayoutX(1)
        playPause.setLayoutY(740)
  //when playPause is pressed, the animation is paused and the button text is changed to "Play"
    var isPaused = false
    playPause.onAction = _ =>
      if isPaused then
        isPaused = false
        playPause.text = "Pause"
        displayMessage("Simulation resumed.")
      else
        isPaused = true
        playPause.text = "Play"
        displayMessage("Simulation paused.")


  // button for resetting the simulation:
    val reset = new Button("Reset")
        reset.setLayoutX(55)
        reset.setLayoutY(740)
    reset.onAction = _ =>
      val name = domain.name
      domain = new Simulation
      domain.parseData(name)
      displayMessage("Reset simulation.")



  // slider for adjusting dayAdjuster (dt), and therefore simulation speed. from 1/10 of dayAdjuster days to 2*dayAdjuster days
    val slider = new Slider(1/10*dayAdjuster, 2*dayAdjuster, dayAdjuster)
        slider.setLayoutX(200)
        slider.setLayoutY(735)
        slider.setShowTickLabels(true)
        slider.setShowTickMarks(true)
        slider.setMajorTickUnit(10)
        slider.setMinorTickCount(1)
        slider.setBlockIncrement(10)
        slider.setSnapToTicks(true)
        slider.setPrefWidth(200)
    slider.valueProperty().addListener((_, oldValue, newValue) =>
      dayAdjuster = newValue.intValue()
      dt = (60*60*24*dayAdjuster) / 60
      displayMessage("Time step adjusted: 1 simulation second = " + dayAdjuster + " days.")
    )

    setupMenu()
    actionSetup()




  //animation timer for the gui, that pauses if variable isPaused is true. Updates the GUI at ≈ 60 fps
    val timer = AnimationTimer(t =>
      if !isPaused && domain.time > 0 && !domain.collision then
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
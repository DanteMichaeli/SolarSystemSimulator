import SolarSystemSimulatorApp.stage
import javafx.scene.shape.{Circle, LineTo, MoveTo, Path, PathElement}
import scalafx.application.JFXApp3
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.control.{Button, CheckMenuItem, Menu, MenuBar, MenuItem, RadioMenuItem, SeparatorMenuItem, Slider, ToggleGroup}
import scalafx.scene.shape.Polyline

import scala.language.postfixOps

object SolarSystemSimulatorApp extends JFXApp3 :

  // toggled by playPause, stops the animation when true
  var isPaused = false

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
    group


  // method for tracing the trajectory of all bodie using the bodies' trajectory buffer, and drawing it into the GUI app:
  def drawTrajectories(): Group =
    val bodies = domain.celestialBodies
    val group = new Group
    for body <- bodies do
      val polyline = new Polyline()
      polyline.setStroke(body.color)
      polyline.setStrokeWidth(1)
      group.getChildren.add(polyline)
      for (pos, i) <- body.trajectory.zipWithIndex do
  //only trace every ith point to improve efficiency
        if i % 15 == 0 then
          polyline.getPoints.addAll(pos.x, pos.y)
    group


  //super group that combines all drawings of bodies, trajectories, vectors et.c.
  def drawSimulation(): Group =
    val bodiesGroup = drawBodies()
    val trajectoriesGroup = drawTrajectories()
    val simulationGroup = new Group(bodiesGroup, trajectoriesGroup)
    simulationGroup




  override def start(): Unit =
  //ScalaFX stage with all that is needed to display the celestial Bodies in their correct positions
    stage = new JFXApp3.PrimaryStage:
      title = "Solar System Simulator"
      width = GUIwidth
      height = GUIheight
      scene = new Scene:
        fill = Color.Black

  // play/pause button for the gui:
    val playPause = new Button("Pause")
        playPause.setLayoutX(200)
        playPause.setLayoutY(700)
  //when playPause is pressed, the animation is paused and the button text is changed to "Play"
    playPause.onAction = _ =>
      if isPaused then
        isPaused = false
        playPause.text = "Pause"
      else
        isPaused = true
        playPause.text = "Play"


  // slider for adjusting dt, and therefore simulation speed (and accuracy):
    val slider = new Slider(0.1*dt, dt, 3*dt)
        slider.setLayoutX(400)
        slider.setLayoutY(750)
        slider.setShowTickLabels(true)
        slider.setShowTickMarks(true)
        slider.setMajorTickUnit(10)
        slider.setMinorTickCount(5)
        slider.setBlockIncrement(10)
        slider.value.onChange((_, _, newValue) => dt = newValue.doubleValue())

    //menu bar with menus:
    val menuBar = new MenuBar
    val viewMenu = new Menu("View")
    val directionVectors = new CheckMenuItem("Direction Vectors")
    val accelerationVectors = new CheckMenuItem("Acceleration Vectors")
    val trajectories = new CheckMenuItem("Trajectories")
    viewMenu.items = List(directionVectors, new SeparatorMenuItem, accelerationVectors, new SeparatorMenuItem, trajectories)

    menuBar.menus = List(viewMenu)




    stage.scene().content = Group(menuBar,playPause, slider, drawSimulation())


  //animation timer for the gui, that pauses if variable isPaused is true
    val timer = AnimationTimer(t =>
      if !isPaused then
        domain.timePasses()
        stage.scene().content = Group(menuBar,playPause, slider, drawSimulation())
    )
    timer.start()



end SolarSystemSimulatorApp
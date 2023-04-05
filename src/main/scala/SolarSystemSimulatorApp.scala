import SolarSystemSimulatorApp.stage
import javafx.scene.shape.{Circle, Path, PathElement}
import scalafx.application.JFXApp3
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.control.{Button, Menu, MenuBar, MenuItem, SeparatorMenuItem, CheckMenuItem, RadioMenuItem, ToggleGroup, Slider}

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
    stage.scene.value.content = group
    group

    //method for drawing the trajectories of the celestial bodies of Simulation into the GUI app. Should make use of path and path elements
    //def drawTrajectories(): Group =



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
        stage.scene().content = Group(playPause, slider, drawBodies())

    //menu bar with menus:
    val menuBar = new MenuBar
    val viewMenu = new Menu("View")
    val directionVectors = new CheckMenuItem("Direction Vectors")
    val accelerationVectors = new CheckMenuItem("Acceleration Vectors")
    val trajectories = new CheckMenuItem("Trajectories")
    viewMenu.items = List(directionVectors, new SeparatorMenuItem, accelerationVectors, new SeparatorMenuItem, trajectories)



    menuBar.menus = List(viewMenu)








    stage.scene().content = Group(menuBar,playPause, slider, drawBodies())

  //animation timer for the gui, that pauses if variable isPaused is true
    val timer = AnimationTimer(t =>
      if !isPaused then
        domain.timePasses()
        drawBodies()
    )
    timer.start()



end SolarSystemSimulatorApp
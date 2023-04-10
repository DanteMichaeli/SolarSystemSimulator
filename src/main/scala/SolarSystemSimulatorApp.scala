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
import scalafx.Includes._


object SolarSystemSimulatorApp extends JFXApp3 :


  //domain of the simulation
  var domain = new Simulation
  domain.parseData("theSolarSystem.txt")
  var isComplete = false


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


  //method for tracing the trajectory of all bodies using the bodies' trajectory buffer:
  var trajectoriesOn = false
  def drawTrajectories(): Group =
    if trajectoriesOn then
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
    else
      val group = new Group
      group


  //method for drawing the direction vectors of all bodies:
  var directionVectorsOn = false
  def drawDirVectors(): Group =
    val bodies = domain.celestialBodies
    val group = new Group
    if directionVectorsOn then
      for n <- bodies.indices do
        if n > 0 then
          val direction = bodies(n).vel.normalized
          val angle = math.atan2(direction.y, direction.x) * 180 / math.Pi
          val arrowLength = bodies(n).radius + 10

          // endpoint of arrow segment
          val endX = bodies(n).pos.x + direction.x * arrowLength
          val endY = bodies(n).pos.y + direction.y * arrowLength

          // line segment
          val segment = new Line()
          segment.setStartX(bodies(n).pos.x)
          segment.setStartY(bodies(n).pos.y)
          segment.setEndX(endX)
          segment.setEndY(endY)
          segment.setStroke(White)

          // arrowhead, a triangle pointing in the direction of the velocity vector
          val arrowheadSize = 5
          val arrowhead = new Polygon()
          arrowhead.getPoints.addAll(endX, endY,
                                     endX-arrowheadSize*math.cos(angle*math.Pi/180+math.Pi/6),
                                     endY-arrowheadSize*math.sin(angle*math.Pi/180+math.Pi/6),
                                     endX-arrowheadSize*math.cos(angle*math.Pi/180-math.Pi/6),
                                     endY-arrowheadSize*math.sin(angle*math.Pi/180-math.Pi/6)
          )
          arrowhead.setFill(White)

          // add segment and arrowhead to group
          group.getChildren.addAll(segment, arrowhead)
    group



  //method for drawing the acceleration vectors of all bodies, just like the direction vectors
  var accelerationVectorsOn = false
  def drawAccVectors(): Group =
    val bodies = domain.celestialBodies
    val group = new Group
    if accelerationVectorsOn then
      for n <- bodies.indices do
        if n > 0 then
          val acceleration = bodies(n).acc.normalized
          val angle = math.atan2(acceleration.y, acceleration.x) * 180 / math.Pi
          val arrowLength = bodies(n).radius + 10

          // endpoint of arrow segment
          val endX = bodies(n).pos.x + acceleration.x * arrowLength
          val endY = bodies(n).pos.y + acceleration.y * arrowLength

          // line segment
          val segment = new Line()
          segment.setStartX(bodies(n).pos.x)
          segment.setStartY(bodies(n).pos.y)
          segment.setEndX(endX)
          segment.setEndY(endY)
          segment.setStroke(Purple)

          // arrowhead, a triangle pointing in the direction of the velocity vector
          val arrowheadSize = 5
          val arrowhead = new Polygon()
          arrowhead.getPoints.addAll(endX, endY,
                                     endX-arrowheadSize*math.cos(angle*math.Pi/180+math.Pi/6),
                                     endY-arrowheadSize*math.sin(angle*math.Pi/180+math.Pi/6),
                                     endX-arrowheadSize*math.cos(angle*math.Pi/180-math.Pi/6),
                                     endY-arrowheadSize*math.sin(angle*math.Pi/180-math.Pi/6)
          )
          arrowhead.setFill(Purple)

          // add segment and arrowhead to group
          group.getChildren.addAll(segment, arrowhead)
    group



  //super group that combines all drawings of bodies, trajectories, vectors et.c.
  def drawSimulation(): Group =
    val bodiesGroup = drawBodies()
    val trajectoriesGroup = drawTrajectories()
    val dirVectorsGroup = drawDirVectors()
    val accVectorsGroup = drawAccVectors()
    val simulationGroup = new Group(bodiesGroup, trajectoriesGroup, dirVectorsGroup, accVectorsGroup)
    simulationGroup



  override def start(): Unit =
  //ScalaFX stage with all that is needed to display the celestial Bodies in their correct positions
    stage = new JFXApp3.PrimaryStage:
      title = "Solar System Simulator"
      width = GUIwidth
      height = GUIheight
      scene = new Scene:
        fill = Color.Black

   //message displayer that displays messages of failed / successful operations. The text should fade away after a few seconds
    val messageDisplayer = new Label("Launched simulator.")
        messageDisplayer.setLayoutX(420)
        messageDisplayer.setLayoutY(740)
        messageDisplayer.setTextFill(White)

    val fadeTransition = new FadeTransition(jfxDuration2sfx(Duration.seconds(5)), messageDisplayer)
    fadeTransition.setToValue(0.0)
    fadeTransition.play()

    def displayMessage(message: String): Unit =
      messageDisplayer.setOpacity(100)
      messageDisplayer.setText(message)
      fadeTransition.play()
      fadeTransition.setOnFinished( _ => messageDisplayer.setText("") )


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


    //menu bar with menus:
    val menuBar = new MenuBar
    val fileMenu = new Menu("File")
    val viewMenu = new Menu("View")

    //open menu item for opening a new simulation file. When open is pressed, a file chooser is opened, and the simulation is reset and the new file is parsed
    val open = new MenuItem("Open")
    open.onAction = _ =>
      val fileChooser = new FileChooser()
      fileChooser.setTitle("Open Simulation File")
      val file = fileChooser.showOpenDialog(stage)
      try
        if file != null then
          domain = new Simulation
          domain.parseData(file.getAbsolutePath)
          displayMessage("Opened simulation: " + file.getAbsolutePath + ".")
      catch
        case wrongFileType: IOException =>
          val alert = new Alert(AlertType.Error)
          alert.setTitle("Error")
          alert.setHeaderText("Invalid File Type Error")
          alert.setContentText("The file you selected is not a valid simulation file. Make sure you've selected a .txt file.")
          alert.showAndWait()

        case wrongStructure: IllegalArgumentException =>
          val alert = new Alert(AlertType.Error)
          val dialogPane = alert.getDialogPane
          dialogPane.setPrefWidth(800)
          alert.setTitle("Error")
          alert.setHeaderText("File Structure Error")
          alert.setContentText(wrongStructure.getMessage)
          alert.showAndWait()



    //save menu item for saving (overwriting) the current simulation. When save is pressed, simulation is saved into a new file using saveData() method. No user input needed
    val save = new MenuItem("Save")
    save.onAction = _ =>
      domain.saveData(domain.name)
      println("Successfully saved simulation.")

    //save as menu item for saving the current simulation. When save is pressed, simulation is saved into a new file using saveData() method. Should be able to provide a file name
    val saveAs = new MenuItem("Save As")
    saveAs.onAction = _ =>
      val fileChooser = new FileChooser()
      fileChooser.setTitle("Save Simulation File")
      val file = fileChooser.showSaveDialog(stage)
      if file != null then
        domain.saveData(file.getAbsolutePath)
        displayMessage("Successfully saved simulation.")



    //when directionVectors is checked, the directionVectorsOn variable is set to true, and the direction vectors are drawn
    val directionVectors = new CheckMenuItem("Direction Vectors")
    directionVectors.onAction = _ =>
      if directionVectors.selected.value then
        domain.celestialBodies.foreach( (body: CelestialBody) => body.trajectory = mutable.Buffer(body.trajectory.last))
        directionVectorsOn = true
        displayMessage("Direction vectors turned on.")
      else
        directionVectorsOn = false
        displayMessage("Direction vectors turned off.")

    //when accelerationVectors is checked, the accelerationVectorsOn variable is set to true, and the acceleration vectors are drawn
    val accelerationVectors = new CheckMenuItem("Acceleration Vectors")
    accelerationVectors.onAction = _ =>
      if accelerationVectors.selected.value then
        accelerationVectorsOn = true
        displayMessage("Acceleration vectors turned on.")
      else
        accelerationVectorsOn = false
        displayMessage("Acceleration vectors turned off.")

    //when trajectories is checked, the trajectoriesOn variable is set to true, and the trajectories are drawn, and trajectory buffers are cleared
    val trajectories = new CheckMenuItem("Trajectories")
    trajectories.onAction = _ =>
      if trajectories.selected.value then
        domain.celestialBodies.foreach( (body: CelestialBody) => body.trajectory = mutable.Buffer(body.trajectory.last))
        trajectoriesOn = true
        displayMessage("Trajectories turned on.")
      else
        trajectoriesOn = false
        displayMessage("Trajectories turned off.")

    fileMenu.items = List(open, new SeparatorMenuItem, save, new SeparatorMenuItem, saveAs)
    viewMenu.items = List(directionVectors, new SeparatorMenuItem, accelerationVectors, new SeparatorMenuItem, trajectories)

    menuBar.menus = List(fileMenu, viewMenu)

    //time label that displays domain.time. Should also update when domain.time updates:
    val timeProperty = new SimpleDoubleProperty(domain.time)
    val timeLabel = new Label(s"Time: ${domain.time}")
        timeLabel.setLayoutX(110)
        timeLabel.setLayoutY(742.5)
        timeLabel.setTextFill(White)
    timeLabel.textProperty().bind(timeProperty.asString("Time: %.1f"))
    timeProperty.addListener((observable, oldValue, newValue) => timeLabel.setText(s"Time: ${newValue.intValue}"))


  //animation timer for the gui, that pauses if variable isPaused is true. Updates the GUI at ≈ 60 fps
    val timer = AnimationTimer(t =>
      if !isPaused && domain.time > 0 && !domain.collision then
        domain.timePasses()
        stage.scene().content = Group(menuBar, playPause, reset, slider, timeLabel, messageDisplayer, drawSimulation())
        domain.time -= 1.0/60.0   //to account for refresh rate of 60 fps
        timeProperty.set(domain.time)

      else if domain.time <= 0 && !isComplete then
        displayMessage("Simulation complete.")
        isComplete = true
    )
    timer.start()



end SolarSystemSimulatorApp
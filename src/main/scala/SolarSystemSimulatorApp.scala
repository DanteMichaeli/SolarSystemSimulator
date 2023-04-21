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



    //info displayer for a celestial body when clicked with mouse
    val infoDisplayer = new Label("")
        infoDisplayer.setLayoutX(1250)
        infoDisplayer.setLayoutY(10)
        infoDisplayer.setTextFill(White)


    def displayInfo(): Unit =
      if domain.bodyOnDisplay.isDefined then
        val body = domain.bodyOnDisplay.get
        infoDisplayer.setText(s"Name: ${body.name}\nType: ${if body.sort == "sat" then "satellite" else if body.sort == "pla" then "planet" else body.sort}\nMass: ${body.mass} kg\nSimulation radius: ${body.radius} px\nPosition:  X: ${body.pos.x.round}, Y: ${body.pos.y.round}\nOrbital velocity: ${body.vel.magnitude.round} m/s\nDistance from (largest) sun: ${(body.distanceTo(domain.celestialBodies.filter(_.sort == "sun").maxBy(_.mass))*(scalingFactor/1000.0/149597871.0)).round} AU")
      else
        infoDisplayer.setText("")




    //method for drawing the direction vectors of all bodies:
    var directionVectorsOn = false
    def drawDirVectors(): Group =
      val bodies = domain.celestialBodies
      val group = new Group
      if directionVectorsOn then
        for n <- bodies.indices do
          if bodies(n).sort != "sun" then
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
          if bodies(n).sort != "sun" then
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


    //method for drawing "Lagrange lines" between all the bodies, used for testing the Lagrange points. So from body 0 to all bodies, from body 1 to all bodies, etc.
    var lagrangeLinesOn = false
    def drawLagrangeLines(): Group =
      val bodies = domain.celestialBodies
      val group = new Group
      if lagrangeLinesOn then
        for n <- bodies.indices do
          for m <- bodies.indices do
            if n != m then
              val line = new Line()
              line.setStartX(bodies(n).pos.x)
              line.setStartY(bodies(n).pos.y)
              line.setEndX(bodies(m).pos.x)
              line.setEndY(bodies(m).pos.y)
              line.setStroke(White)
              line.setStrokeWidth(1)
              group.getChildren.add(line)
      group


    var trajectoriesOn = false

    //super group that combines all drawings of bodies, trajectories, vectors et.c.
    def drawSimulation(): Group =
      val bodiesGroup = drawBodies(domain)
      val trajectoriesGroup = drawTrajectories(domain, trajectoriesOn)
      val dirVectorsGroup = drawDirVectors()
      val accVectorsGroup = drawAccVectors()
      val lagrangeLinesGroup = drawLagrangeLines()
      val simulationGroup = new Group(bodiesGroup, trajectoriesGroup, dirVectorsGroup, accVectorsGroup, lagrangeLinesGroup)


      val circlesWithBodies = bodiesGroup.getChildren.zip(domain.celestialBodies)
      for circleWithBody <- circlesWithBodies do
        if outOfBounds((circleWithBody._1).asInstanceOf[Circle]) then
          zoomOut(simulationGroup)
          displayMessage(s"Planet ${circleWithBody._2.name} has ventured out of local scope. Zooming out.")

      simulationGroup



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
    val editMenu = new Menu("Edit")

    //open menu item for opening a new simulation file. When open is pressed, a file chooser is opened, and the simulation is reset and the new file is parsed
    val open = new MenuItem("Open")
    open.onAction = _ =>
      val fileChooser = new FileChooser()
      fileChooser.setTitle("Open Simulation File")
      fileChooser.getExtensionFilters.add(new ExtensionFilter("Text Files", "*.txt"))
      val file = fileChooser.showOpenDialog(stage)
      try
        if file != null then
          domain = new Simulation
          domain.parseData(file.getAbsolutePath)
          displayMessage("Opened simulation: " + domain.name + ".")
      catch
        case illegalValue: IllegalArgumentException =>
          val alert = new Alert(AlertType.Error)
          val dialogPane = alert.getDialogPane
          alert.setTitle("Error")
          alert.setHeaderText("File Structure Error")
          dialogPane.setPrefWidth(800)
          alert.setContentText(illegalValue.getMessage)
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




    //button for adding a new body mid-simulation
    val addBody = new MenuItem("Add Celestial Body")
    addBody.onAction = _ =>
      val dialog = new TextInputDialog()
      dialog.setTitle("Add Celestial Body")
      dialog.setHeaderText("Add Celestial Body")
      dialog.setContentText("Enter the new body in the format:\nsort, name, radius, mass, x-pos, y-pos, x-vel, y-vel, color code.")
      val result = dialog.showAndWait()
      try
        val cols = result.get.split(",").map(_.trim)
        if cols.length != 9 then
          throw new IllegalArgumentException("Invalid input format. Please enter the new body in the format:\nsort, name, radius, mass, x-pos, y-pos, x-vel, y-vel, color code.")
        else if cols(2).toDouble <= 0 then
          throw new IllegalArgumentException(s"Radius of ${cols(1)} must be positive.")
        else if cols(3).toDouble <= 0 then
          throw new IllegalArgumentException(s"Mass of ${cols(1)} must be positive.")
        else if cols(4).toDouble < 0 || cols(5).toDouble < 0 then
          throw new IllegalArgumentException(s"Initial position of ${cols(1)} must not be negative.")
        else
          if cols(0) == "sun" then
            domain.celestialBodies += Sun(cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)))
          else if cols(0) == "pla" then
            domain.celestialBodies += Planet(cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)))
          else if cols(0) == "sat" then
            domain.celestialBodies += Satellite(cols(1), cols(2).toDouble, cols(3).toDouble, Vector2D(cols(4).toDouble, cols(5).toDouble), Vector2D(cols(6).toDouble, cols(7).toDouble), Color.web(cols(8)))
          else
            throw new IllegalArgumentException(s"Invalid sort: ${cols(0)}. Body must be of sort 'sun', 'pla', or 'sat'.")

          displayMessage(s"Successfully added ${cols(1)}.")


      catch
        case illegalValue: IllegalArgumentException =>
          val alert = new Alert(AlertType.Error)
          val dialogPane = alert.getDialogPane
          alert.setTitle("Error")
          alert.setHeaderText("Invalid Input")
          dialogPane.setPrefWidth(800)
          alert.setContentText(illegalValue.getMessage)
          alert.showAndWait()




    //button for changing suns' radii
    val editSunRadii = new MenuItem("Sun Radii")
    editSunRadii.onAction = _ =>
      val suns = domain.celestialBodies.filter( _.sort == "sun")
      for sun <- suns do
        val dialog = new TextInputDialog(sun.radius.toString())
        dialog.setTitle("Edit Sun Radius")
        dialog.setHeaderText("Edit Sun Radius")
        dialog.setContentText("Enter new radius for " + sun.name + ":")
        val result = dialog.showAndWait()
        try
          val newRadius = result.get.toDouble
          if newRadius <= 0 then
            throw new IllegalArgumentException
          else
            sun.radius = newRadius
          displayMessage(s"Successfully changed $sun radius to ${sun.radius}.")
        catch
          case illegalValue: IllegalArgumentException =>
            val alert = new Alert(AlertType.Error)
            val dialogPane = alert.getDialogPane
            alert.setTitle("Error")
            alert.setHeaderText("Invalid Input")
            dialogPane.setPrefWidth(800)
            alert.setContentText(s"Failed to change radius: ${result.get} is an illegal value. Please enter a number greater than 0.")
            alert.showAndWait()







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

    //lagrange lines menu item for toggling the lines on and off
    val lagrangeLines = new CheckMenuItem("Lagrange Lines")
    lagrangeLines.onAction = _ =>
      if lagrangeLinesOn then
        lagrangeLinesOn = false
        displayMessage("Lagrange lines turned off.")
      else
        lagrangeLinesOn = true
        displayMessage("Lagrange lines turned on.")



    fileMenu.items = List(open, new SeparatorMenuItem, save, new SeparatorMenuItem, saveAs)
    viewMenu.items = List(directionVectors, new SeparatorMenuItem, accelerationVectors, new SeparatorMenuItem, trajectories, new SeparatorMenuItem, lagrangeLines)
    editMenu.items = List(addBody, new SeparatorMenuItem, editSunRadii)

    menuBar.menus = List(fileMenu, viewMenu, editMenu)

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
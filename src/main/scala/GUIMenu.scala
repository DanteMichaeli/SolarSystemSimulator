import SolarSystemSimulatorApp.{domain, stage}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, CheckMenuItem, Menu, MenuBar, MenuItem, SeparatorMenuItem, TextInputDialog}
import scalafx.scene.paint.Color
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scala.collection.mutable

val menuBar =             new MenuBar
val fileMenu =            new Menu("File")
val viewMenu =            new Menu("View")
val editMenu =            new Menu("Edit")
val open =                new MenuItem("Open")
val save =                new MenuItem("Save")                  //overwrites current file
val saveAs =              new MenuItem("Save As")              //saves as new file
val addBody =             new MenuItem("Add Celestial Body")
val editSunRadii =        new MenuItem("Sun Radii")
val directionVectors =    new CheckMenuItem("Direction Vectors")
val accelerationVectors = new CheckMenuItem("Acceleration Vectors")
val trajectories =        new CheckMenuItem("Trajectories")
val lagrangeLines =       new CheckMenuItem("Lagrange Lines")

//METHODS FOR MENU ITEMS
def openSimulation(): Unit =
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

def saveSimulation(): Unit =
  domain.saveData(domain.name)
  println("Successfully saved simulation.")

def saveAsSimulation(): Unit =
  val fileChooser = new FileChooser()
  fileChooser.setTitle("Save Simulation File")
  val file = fileChooser.showSaveDialog(stage)
  if file != null then
    domain.saveData(file.getAbsolutePath)
    displayMessage("Successfully saved simulation.")

def addBodySimulation(): Unit =
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

def editSunRadiiSimulation(): Unit =
  val suns = domain.celestialBodies.filter( _.sort == "sun")
    for sun <- suns do
      val dialog = new TextInputDialog(sun.radius.toString)
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

def directionVectorsSimulation(): Unit =
  if directionVectors.selected.value then
    domain.celestialBodies.foreach( (body: CelestialBody) => body.trajectory = mutable.Buffer(body.trajectory.last))
    domain.directionVectorsOn = true
    displayMessage("Direction vectors turned on.")
  else
    domain.directionVectorsOn = false
    displayMessage("Direction vectors turned off.")

def accelerationVectorsSimulation(): Unit =
  if accelerationVectors.selected.value then
    domain.accelerationVectorsOn = true
    displayMessage("Acceleration vectors turned on.")
  else
    domain.accelerationVectorsOn = false
    displayMessage("Acceleration vectors turned off.")

def trajectoriesSimulation(): Unit =
  if trajectories.selected.value then
    domain.celestialBodies.foreach( (body: CelestialBody) => body.trajectory = mutable.Buffer(body.trajectory.last))
    domain.trajectoriesOn = true
    displayMessage("Trajectories turned on.")
  else
    domain.trajectoriesOn = false
    displayMessage("Trajectories turned off.")

def lagrangeLinesSimulation(): Unit =
  if lagrangeLines.selected.value then
    domain.lagrangeLinesOn = true
    displayMessage("Lagrange lines turned on.")
  else
    domain.lagrangeLinesOn = false
    displayMessage("Lagrange lines turned off.")

def setupMenu(): Unit =
  fileMenu.items = List(open, new SeparatorMenuItem, save, new SeparatorMenuItem, saveAs)
  editMenu.items = List(addBody, new SeparatorMenuItem, editSunRadii)
  viewMenu.items = List(directionVectors, new SeparatorMenuItem, accelerationVectors, new SeparatorMenuItem, trajectories, new SeparatorMenuItem, lagrangeLines)
  menuBar.menus = List(fileMenu, editMenu, viewMenu)

def setupMenuActions(): Unit = 
  open.onAction = _ => openSimulation()
  save.onAction = _ => saveSimulation()
  saveAs.onAction = _ => saveAsSimulation()
  addBody.onAction = _ => addBodySimulation()
  editSunRadii.onAction = _ => editSunRadiiSimulation()
  directionVectors.onAction = _ => directionVectorsSimulation()
  accelerationVectors.onAction = _ => accelerationVectorsSimulation()
  trajectories.onAction = _ => trajectoriesSimulation()
  lagrangeLines.onAction = _ => lagrangeLinesSimulation()



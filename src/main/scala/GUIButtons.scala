import SolarSystemSimulatorApp.domain
import javafx.beans.value.ChangeListener
import scalafx.scene.control.{Button, Slider}
import scala.language.postfixOps

val playPause = new Button("Pause")
val reset =     new Button("Reset")
val slider =    new Slider(1/10*dayAdjuster, 2*dayAdjuster, dayAdjuster)

//METHODS FOR BUTTON ITEMS
def playPauseSimulation(): Unit =
  if domain.isPaused then
    domain.isPaused = false
    playPause.text = "Pause"
    displayMessage("Simulation resumed.")
  else
    domain.isPaused = true
    playPause.text = "Play"
    displayMessage("Simulation paused.")

def resetSimulation(): Unit =
  val name = domain.name
  domain = new Simulation
  domain.parseData(name)
  directionVectors.selected.value = false
  accelerationVectors.selected.value = false
  trajectories.selected.value = false
  lagrangeLines.selected.value = false
  playPause.text = "Pause"
  displayMessage("Reset simulation.")

def adjustSlider(): Unit =
  slider.valueProperty().addListener(((o: javafx.beans.value.ObservableValue[_ <: Number], oldValue: Number, newValue: Number) =>
    dayAdjuster = newValue.intValue()
      dt = (60 * 60 * 24 * dayAdjuster) / 60
      displayMessage ("Time step adjusted: 1 simulation second = " + dayAdjuster + " days.")
    ))

def setupButtons(): Unit =
  playPause.setLayoutX(1)
  playPause.setLayoutY(740)
  reset.setLayoutX(55)
  reset.setLayoutY(740)
  slider.setLayoutX(200)
  slider.setLayoutY(735)
  slider.setShowTickLabels(true)
  slider.setShowTickMarks(true)
  slider.setMajorTickUnit(10)
  slider.setMinorTickCount(1)
  slider.setBlockIncrement(10)
  slider.setSnapToTicks(false)
  slider.setPrefWidth(200)

def setupButtonActions(): Unit =
  playPause.onAction = _ => playPauseSimulation()
  reset.onAction = _ => resetSimulation()
  adjustSlider()



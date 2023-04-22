import SolarSystemSimulatorApp.domain
import javafx.beans.property.SimpleDoubleProperty
import javafx.util.Duration
import scalafx.Includes.jfxDuration2sfx
import scalafx.animation.FadeTransition
import scalafx.scene.control.Label
import scalafx.scene.paint.Color.White


//MESSAGE DISPLAYER
val messageDisplayer = new Label("Launched simulator.")
val fadeTransition = new FadeTransition(jfxDuration2sfx(Duration.seconds(5)), messageDisplayer)
def displayMessage(message: String): Unit =
  messageDisplayer.setOpacity(100)
  messageDisplayer.setText(message)
  fadeTransition.play()
  fadeTransition.setOnFinished( _ => messageDisplayer.setText("") )

//SELECTED BODY INFO DISPLAYER
val infoDisplayer = new Label("")
def displayInfo(): Unit =
  if domain.bodyOnDisplay.isEmpty then infoDisplayer.setText("") else
    val body = domain.bodyOnDisplay.get
    infoDisplayer.setText(s"Name: ${body.name}\nType: ${if body.sort == "sat" then "satellite" else if body.sort == "pla" then "planet" else body.sort}\nMass: ${body.mass} kg\nSimulation radius: ${body.radius} px\nPosition:  X: ${body.pos.x.round}, Y: ${body.pos.y.round}\nOrbital velocity: ${body.vel.magnitude.round} m/s\nDistance from (largest) sun: ${(body.distanceTo(domain.celestialBodies.filter(_.sort == "sun").maxBy(_.mass))*(scalingFactor/1000.0/149597871.0)).round} AU")

//TIME DISPLAYER
val timeProperty = new SimpleDoubleProperty(domain.time)
val timeLabel = new Label(s"Time: ${domain.time}")

//SET UP METHOD
def setupDisplays(): Unit = 
  messageDisplayer.setLayoutX(420)
  messageDisplayer.setLayoutY(740)
  messageDisplayer.setTextFill(White)
  fadeTransition.setToValue(0.0)
  fadeTransition.play()
  infoDisplayer.setLayoutX(1250)
  infoDisplayer.setLayoutY(10)
  infoDisplayer.setTextFill(White)
  timeLabel.setLayoutX(110)
  timeLabel.setLayoutY(742.5)
  timeLabel.setTextFill(White)
  timeLabel.textProperty().bind(timeProperty.asString("Time: %.1f"))
  timeProperty.addListener((observable, oldValue, newValue) => timeLabel.setText(s"Time: ${newValue.intValue}"))


import scalafx.application.JFXApp3

object SolarSystemSimulatorApp extends JFXApp3 :

  val domain = new Simulation
  domain.parseData()


  override def start(): Unit =
  //create a new ScalaFX stage and set its properties
    stage = new JFXApp3.PrimaryStage :

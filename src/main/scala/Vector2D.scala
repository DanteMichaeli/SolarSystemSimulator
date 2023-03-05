import scala.annotation.targetName
import scala.math.*

class Vector2D(var x: Double, var y: Double):

  def magnitude: Double = sqrt(x*x + y*y)

  def normalized: Vector2D =
    if this.magnitude == 0 then
      Vector2D(0,0)
    else
      Vector2D(x/this.magnitude, y/this.magnitude)

  //THESE METHODS MODIFY THE EXISTING VECTOR...
  @targetName("add")
  def +=(other: Vector2D): Unit =
    this.x += other.x
    this.y += other.y

  @targetName("subtract")
  def -=(other: Vector2D): Unit =
    this.x -= other.x
    this.y -= other.y

  @targetName("multiply")
  def *=(scalar: Double): Unit =
    this.x *= scalar
    this.y *= scalar

  //...WHILE THESE RETURN THE RESULT AS A NEW VECTOR.
  @targetName("addNew")
  def +(other: Vector2D): Vector2D =
    val newX = this.x + other.x
    val newY = this.y + other.y
    Vector2D(newX, newY)


  @targetName("subtractNew")
  def -(other: Vector2D): Vector2D =
    val newX = this.x - other.x
    val newY = this.y - other.y
    Vector2D(newX, newY)

  @targetName("multiplyNew")
  def *(scalar: Double): Vector2D =
    val newX = this.x * scalar
    val newY = this.y * scalar
    Vector2D(newX, newY)

  def dot(other: Vector2D): Double =
    this.x * other.x + this.y * other.y




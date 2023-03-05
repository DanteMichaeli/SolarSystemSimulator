import scala.math.*

class Vector2D(var x: Double, var y: Double):

  def magnitude: Double = sqrt(x*x + y*y)

  def plus(other: Vector2D): Unit =
    this.x += other.x
    this.y += other.y

  def minus(other: Vector2D): Unit =
    this.x -= other.x
    this.y -= other.y

  def mul(scalar: Double): Unit =
    this.x *= scalar
    this.y *= scalar

  def dot(other: Vector2D): Double =
    this.x * other.x + this.y * other.y




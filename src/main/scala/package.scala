package fr.efrei.fp

package object project {

  import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, MINUTES, SECONDS}
  extension (length: Long) {
    def ms: FiniteDuration = FiniteDuration(length, MILLISECONDS)
    def sec: FiniteDuration = FiniteDuration(length, SECONDS)
    def min: FiniteDuration = FiniteDuration(length, MINUTES)
  }
  extension (duration: FiniteDuration) {
    def ++(other: FiniteDuration): FiniteDuration = (duration.unit, other.unit) match {
      case (a, b) if a == b => FiniteDuration(duration.length + other.length, duration.unit)
      case (MILLISECONDS, _) => FiniteDuration(duration.length + other.toMillis, MILLISECONDS)
      case (_, MILLISECONDS) => FiniteDuration(duration.toMillis + other.length, MILLISECONDS)
      case (SECONDS, _) => FiniteDuration(duration.length + other.toSeconds, SECONDS)
      case (_, SECONDS) => FiniteDuration(duration.toSeconds + other.length, SECONDS)
      case (MINUTES, _) => FiniteDuration(duration.length + other.toMinutes, MINUTES)
      case (_, MINUTES) => FiniteDuration(duration.toMinutes + other.length, MINUTES)
    }
  }
}

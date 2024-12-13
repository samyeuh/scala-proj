package fr.efrei.fp.project

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.shouldBe

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}

class FortyTwoSpec extends AnyFlatSpec {

  "42.ms" should "equal the instance class created with extension" in {
    42.ms shouldBe new FiniteDuration(42, MILLISECONDS)
  }

  "42.ms ++ 2 ms" should "equal to 44.ms" in {
    42.ms ++ 2.ms shouldBe 44.ms
  }

  "42.min ++ 120 sec" should "equal to 44min" in {
    val res = 40.min ++ 120.sec
    res shouldBe 2520.sec
  }
}

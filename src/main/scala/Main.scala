package fr.efrei.fp.project

object Main extends App {
  // 42.ms
  val fortyTwoMs = 42.ms
  val fortyTwoS = 42.sec
  val fortyTwoM = 42.min

  val res  = 42.min ++ 42.ms
  println(res)
}

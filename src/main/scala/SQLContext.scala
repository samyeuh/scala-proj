package fr.efrei.fp.project

object SQLContext {
  implicit class CommandString(command: String) {
    def exec(): Unit = {
      val result = CommandProcessor.execute(command)
      println(result)
    }
  }

  def execute(block: => Unit): Unit = {
    block // Évalue le bloc avec les commandes enrichies
  }
}
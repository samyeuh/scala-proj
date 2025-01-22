package fr.efrei.fp.project

import scala.io.StdIn.readLine

object Main extends App {
  println("Bienvenue dans le terminal interactif SQL-like !")
  println("Tapez une commande ou 'exit' pour quitter.\n")

  var continue = true

  while (continue) {
    print(">>> ")
    val command = readLine().trim

    command match {
      case "exit" =>
        continue = false
        println("Fermeture du terminal. À bientôt !")
      case cmd if cmd.nonEmpty =>
        val result = CommandProcessor.execute(cmd)
        println(result)
      case _ =>
        println("Commande vide. Veuillez entrer une commande.")
    }
  }
}
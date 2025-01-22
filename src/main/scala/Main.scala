package fr.efrei.fp.project

import scala.io.StdIn.readLine

/**
 * Entry point for the application.
 */
object Main extends App {
  println("Bienvenue dans le terminal interactif SQL-like ! Tapez une commande ou 'exit' pour quitter.")

  var continue = true

  while (continue) {
    print(">>> ")
    val command = scala.io.StdIn.readLine().trim
    command match {
      case "exit" =>
        continue = false
        println("Fermeture du terminal. À bientôt !")
      case cmd if cmd.nonEmpty =>
        println(CommandProcessor.execute(cmd))
      case _ => println("Commande vide. Veuillez entrer une commande.")
    }
  }
}

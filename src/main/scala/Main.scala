package fr.efrei.fp.project
import csv.Reader

object Main extends App {

  val rows = Reader().readCSV("src/main/resources/users.csv")
  rows.foreach(row => println(row.data))
}

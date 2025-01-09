package fr.efrei.fp.project
import csv.Reader

object Main extends App {
  val rows = Reader().readCSV("src/main/resources/users.csv")
  val table = Table("users", rows)

  val result = table
    .select("id", "name", "age")

  result.print()
}

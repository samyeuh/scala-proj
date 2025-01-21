package fr.efrei.fp.project

import SQLContext._

object Main extends App {
  execute {
    "create table Students (Name, Age, Grade)".exec()
    "Students add (Alice, 22, A)".exec()
    "Students add (Bob, 18, B)".exec()
    "Students select (Name, Grade)".exec()
    "Students filter Age > 20".exec()
  }
}
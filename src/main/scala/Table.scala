package fr.efrei.fp.project

import csv.Row

class Table(name: String, rows: Seq[Row]) {

  def select(columns: String*): Table = {
    val newRows = rows.map(row => {
      Row(row.data.filter { case (key, _) => columns.contains(key) })
    })
    Table(name, newRows)
  }

  def print(): Unit = {
    println(s"Table $name")
    if (rows.isEmpty) {
      println("Table vide")
      return
    }

    val columns = rows.head.data.keys.toSeq

    val columnsWidths = columns.map { col =>
      math.max(col.length, rows.map(_.data.getOrElse(col, "").length).max)
    }

    def formatRows(values: Seq[String]): String = {
      values.zip(columnsWidths).map { case (value, width) =>
        value.padTo(width, ' ')
      }.mkString("| ", " | ", " |")
    }

    // En-tête
    val header = formatRows(columns)
    val separator = columnsWidths.map("-" * _).mkString("+-", "-+-", "-+")
    println(separator)
    println(header)
    println(separator)

    // Données
    rows.foreach { row =>
      val values = columns.map(col => row.data.getOrElse(col, ""))
      println(formatRows(values))
    }
    println(separator)
  }

}

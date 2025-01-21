package fr.efrei.fp.project

import com.github.tototoshi.csv.*
import csv.Row

import java.io.File

case class Table(name: String, rows: Seq[Row] = Seq.empty, columns: Seq[String] = Seq.empty) {

  def select(columns: String*): Table = {
    val newRows = rows.map(row => {
      Row(row.data.filter { case (key, _) => columns.contains(key) })
    })
    Table(name, newRows, this.columns)
  }

  def insert(data: Map[String, String]): Table = {
    if (!data.keys.forall(columns.contains)) {
      throw new IllegalArgumentException("Les données insérées contiennent des colonnes inconnues")
    }
    val newRow = Row(data)
    val updatedTable = Table(name, rows :+ newRow, columns)
    updatedTable.saveToCSV()
    updatedTable
  }

  def filter(condition: Row => Boolean): Table = {
    val filteredRows = rows.filter(condition)
    Table(name, filteredRows, columns)
  }

  def saveToCSV(): Unit = {
    val file = new File(s"src/main/resources/$name.csv")
    val writer = CSVWriter.open(file)
    writer.writeRow(columns)
    rows.foreach { row =>
      writer.writeRow(columns.map(col => row.data.getOrElse(col, "")))
    }
    writer.close()
  }

  def loadFromCSV(): Table = {
    val file = new File(s"src/main/resources/$name.csv")
    if (!file.exists()) {
      throw new IllegalArgumentException(s"Le fichier $name.csv n'existe pas.")
    }
    val reader = CSVReader.open(file)
    val data = reader.allWithHeaders()
    reader.close()
    Table(name, data.map(Row), columns)
  }

  def printTable(): String = {
    val builder = new StringBuilder
    builder.append(s"Table: $name\n")
    if (rows.isEmpty) {
      builder.append("Table vide\n")
      return builder.toString()
    }

    val columnsWidths = columns.map { col =>
      math.max(col.length, rows.map(_.data.getOrElse(col, "").length).max)
    }

    def formatRows(values: Seq[String]): String = {
      values.zip(columnsWidths).map { case (value, width) =>
        value.padTo(width, ' ')
      }.mkString("| ", " | ", " |")
    }

    // Header
    val header = formatRows(columns)
    val separator = columnsWidths.map("-" * _).mkString("+-", "-+-", "-+")
    builder.append(separator).append("\n")
    builder.append(header).append("\n")
    builder.append(separator).append("\n")

    // Data rows
    rows.foreach { row =>
      val values = columns.map(col => row.data.getOrElse(col, ""))
      builder.append(formatRows(values)).append("\n")
      builder.append(separator).append("\n")
    }

    builder.toString()
  }

  override def toString: String = printTable()
}

object Table {
  def create(name: String, columns: Seq[String]): Table = {
    if (columns.isEmpty) {
      throw new IllegalArgumentException("Impossible de créer une table sans colonnes")
    }
    val table = Table(name, Seq.empty, columns)
    table.saveToCSV()
    table
  }
}
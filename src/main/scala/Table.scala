package fr.efrei.fp.project

import com.github.tototoshi.csv.*

import java.io.File
import ColumnType.*

case class Column(name: String, dataType: ColumnType)

case class Row(data: Map[Column, String]) {
  def get(column: Column): Option[String] = data.get(column)
  def get(column: String): Option[String] = data.keys.find(_.name == column).flatMap(data.get)
}

case class Table(name: String, rows: Seq[Row] = Seq.empty, columns: Seq[Column] = Seq.empty) {

    def select(columnNames: String*): TablePrint = {
      if (columnNames == Seq("*")) {
        return TablePrint(name, rows, columns)
      }
      val selectedColumns = columns.filter(col => columnNames.contains(col.name))
      val newRows = rows.map(row => Row(row.data.filter { case (key, _) => selectedColumns.contains(key) }))
      TablePrint(name, newRows, selectedColumns)
    }

  def insert(data: Map[Column, String]): Table = {
    columns.foreach { col =>
      data.get(col) match {
        case Some(value) =>
          col.dataType match {
            case ColumnType.IntType if value.toIntOption.isEmpty =>
              throw new IllegalArgumentException(s"La colonne '${col.name}' attend un entier, mais a reçu : $value")
            case ColumnType.BooleanType if !Set("true", "false").contains(value.toLowerCase) =>
              throw new IllegalArgumentException(s"La colonne '${col.name}' attend un booléen (true/false), mais a reçu : $value")
            case ColumnType.StringType => // OK
            case _ => // OK
          }
        case None =>
          throw new IllegalArgumentException(s"La colonne '${col.name}' est manquante dans les données insérées")
      }
    }
    val newRow = Row(data)
    val updatedTable = Table(name, rows :+ newRow, columns)
    updatedTable.saveToCSV()
    updatedTable
  }

  def filter(condition: Row => Boolean): TablePrint = {
    val filteredRows = rows.filter(condition)
    TablePrint(name, filteredRows, columns)
  }

  def saveToCSV(): Unit = {
    val file = new File(s"src/main/resources/$name.csv")
    val writer = CSVWriter.open(file)
    writer.writeRow(columns.map(col => s"${col.name}:${col.dataType.getClass.getSimpleName.replace("$", "")}"))
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
    val allLines = reader.all()
    reader.close()

    val columnDefinitions = allLines.head.map { colDef =>
      val Array(name, dataType) = colDef.split(":")
      Column(name.trim, ColumnType.fromString(dataType.trim))
    }
    val columnNames = allLines(1)
    val data = allLines.drop(2).map(line => Row(columnNames.zip(line).map { case (name, value) => columnDefinitions.find(_.name == name).get -> value }.toMap))
    Table(name, data, columnDefinitions)
  }
}

object Table {
  def create(name: String, columns: Seq[Column]): Table = {
    if (columns.isEmpty) {
      throw new IllegalArgumentException("Impossible de créer une table sans colonnes")
    }
    val table = Table(name, Seq.empty, columns)
    table.saveToCSV()
    table
  }
}
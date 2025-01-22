package fr.efrei.fp.project

import com.github.tototoshi.csv.*

import java.io.File
import ColumnType.*

import java.util.logging.{Level, Logger}

case class Column(name: String, dataType: ColumnType)

case class Row(data: Map[Column, String]) {
  def get(column: Column): Option[String] = data.get(column)
  def get(column: String): Option[String] = data.keys.find(_.name == column).flatMap(data.get)
}

case class Table(name: String, rows: Seq[Row] = Seq.empty, columns: Seq[Column] = Seq.empty) {
  private val logger: Logger = Logger.getLogger(getClass.getName)
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
              logger.log(Level.SEVERE, s"La colonne '${col.name}' attend un entier, mais a reçu : $value")
              throw new IllegalArgumentException(s"La colonne '${col.name}' attend un entier, mais a reçu : $value")
            case _ => // OK
          }
        case None =>
          logger.log(Level.SEVERE, s"La colonne '${col.name}' est manquante dans les données insérées")
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

  def delete(condition: Row => Boolean): Table = {
    val remainingRows = rows.filterNot(condition)

    if (remainingRows.isEmpty) {
      deleteFile()
    } else {
      val updatedTable = Table(name, remainingRows, columns)
      updatedTable.saveToCSV()
      updatedTable
    }
  }

  def deleteFile(): Table = {
    val file = new File(s"src/main/resources/$name.csv")
    if (file.exists()) {
      file.delete()
      println(s"Le fichier associé à la table '$name' a été supprimé car la table est vide.")
    }
    Table(name, Seq.empty, columns)
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
      logger.log(Level.SEVERE, s"Le fichier $name.csv n'existe pas.")
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
  private val logger: Logger = Logger.getLogger(getClass.getName)
  def create(name: String, columns: Seq[Column]): Table = {
    if (columns.isEmpty) {
      logger.log(Level.SEVERE, "Impossible de créer une table sans colonnes")
      throw new IllegalArgumentException("Impossible de créer une table sans colonnes")
    }
    val table = Table(name, Seq.empty, columns)
    table.saveToCSV()
    table
  }
}
package fr.efrei.fp.project

import com.github.tototoshi.csv.
import java.io.File
import scala.util.{Using, Try, Success, Failure}
import java.util.logging.{Logger, Level}

import java.io.File

/**
 * Row represents a single record in a table.
 */
case class Row(data: Map[String, String]) {
  def get(column: String): Option[String] = data.get(column)
}

/**
 * Table represents a database-like table with rows and columns.
 */
case class Table(name: String, rows: Seq[Row] = Seq.empty, columns: Seq[String] = Seq.empty) {

  /**
   * Select specific columns to create a new table.
   */
  def select(columns: String*): Table = {
    val newRows = rows.map(row => Row(row.data.filter { case (key, _) => columns.contains(key) }))
    Table(name, newRows, columns)
  }

  /**
   * Insert a new row into the table.
   */
  def insert(data: Map[String, String]): Table = {
    if (!data.keys.forall(columns.contains)) {
      throw new IllegalArgumentException("Les données insérées contiennent des colonnes inconnues")
    }
    val newRow = Row(data)
    val updatedTable = Table(name, rows :+ newRow, columns)
    updatedTable.saveToCSV()
    updatedTable
  }

  /**
   * Filter rows based on a condition.
   */
  def filter(condition: Row => Boolean): Table = {
    val filteredRows = rows.filter(condition)
    Table(name, filteredRows, columns)
  }

  /**
   * Save the table to a CSV file.
   */
  def saveToCSV(): Unit = {
    val file = new File(s"src/main/resources/$name.csv")
    Using(CSVWriter.open(file)) { writer =>
      writer.writeRow(columns)
      rows.foreach(row => writer.writeRow(columns.map(col => row.data.getOrElse(col, ""))))
    }.recover {
      case e: Exception => throw new RuntimeException(s"Erreur lors de l'écriture du fichier CSV pour $name", e)
    }
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

  /**
   * Print the table in a user-friendly format.
   */
  def printTable(): String = {
    if (rows.isEmpty) return s"Table: $name\nTable vide\n"

    val columnWidths = columns.map(col => math.max(col.length, rows.map(_.data.getOrElse(col, "").length).max))
    val formatRow = (values: Seq[String]) => values.zip(columnWidths).map { case (value, width) => value.padTo(width, ' ') }.mkString("| ", " | ", " |")

    val header = formatRow(columns)
    val separator = columnWidths.map("-" * _).mkString("+-", "-+-", "-+")
    val rowsData = rows.map(row => formatRow(columns.map(col => row.data.getOrElse(col, "")))).mkString("\n")

    s"Table: $name\n$separator\n$header\n$separator\n$rowsData\n$separator\n"
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

package fr.efrei.fp.project

import csv.Row

case class Table(name: String, rows: Seq[Row] = Seq.empty, columns: Seq[String] = Seq.empty) {

  def select(columns: String*): Table = {
    val newRows = rows.map(row => {
      Row(row.data.filter { case (key, _) => columns.contains(key) })
    })
    Table(name, newRows, columns)
  }

  def insert(data: Map[String, String]): Table = {
    if (!data.keys.forall(columns.contains)) {
      throw new IllegalArgumentException("Inserted data contains unknown columns")
    }
    val newRow = Row(data)
    Table(name, rows :+ newRow, columns)
  }

  def filter(condition: Row => Boolean): Table = {
    val filteredRows = rows.filter(condition)
    Table(name, filteredRows, columns)
  }

  def addColumn(columnName: String, transform: Row => String): Table = {
    if (columns.contains(columnName)) {
      throw new IllegalArgumentException(s"Column '$columnName' already exists")
    }
    val updatedRows = rows.map(row => {
      Row(row.data + (columnName -> transform(row)))
    })
    Table(name, updatedRows, columns :+ columnName)
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
      builder.append(separator).append("\n") // Separation between rows
    }

    builder.toString()
  }

  override def toString: String = printTable()
}

object Table {
  def create(name: String, columns: Seq[String]): Table = {
    if (columns.isEmpty) {
      throw new IllegalArgumentException("Cannot create a table with no columns")
    }
    Table(name, Seq.empty, columns)
  }
}
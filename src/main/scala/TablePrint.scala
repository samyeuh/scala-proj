package fr.efrei.fp.project

class TablePrint(name: String, rows: Seq[Row] = Seq.empty, columns: Seq[Column] = Seq.empty) {

  def orderBy(columnName: String, direction: String): TablePrint = {
    val column = columns.find(_.name == columnName).getOrElse(
      throw new IllegalArgumentException(s"La colonne '$columnName' n'existe pas dans la table")
    )

    val sortedRows = rows.sortWith { (row1, row2) =>
      val value1 = row1.get(column).getOrElse("")
      val value2 = row2.get(column).getOrElse("")
      direction match {
        case "ASC" => value1 < value2
        case "DESC" => value1 > value2
        case _ => throw new IllegalArgumentException(s"Direction de tri inconnue : $direction")
      }
    }
    new TablePrint(name, sortedRows, columns)
  }

  def limit(n: Int): TablePrint = {
    new TablePrint(name, rows.take(n), columns)
  }

  def printTable(): String = {
    val builder = new StringBuilder
    builder.append(s"Table: $name\n")
    if (rows.isEmpty) {
      builder.append("Table vide\n")
      return builder.toString()
    }

    val columnsWidths = columns.map { col =>
      rows.map(_.data.getOrElse(col, "").length).max    
    }

    def formatRows(values: Seq[String]): String = {
      values.zip(columnsWidths).map { case (value, width) =>
        value.padTo(width, ' ')
      }.mkString("| ", " | ", " |")
    }

    // Header
    val header = formatRows(columns.map(_.name))
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

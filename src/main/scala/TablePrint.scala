package fr.efrei.fp.project

class TablePrint(name: String, rows: Seq[Row] = Seq.empty, columns: Seq[Column] = Seq.empty) {

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

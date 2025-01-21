package fr.efrei.fp.project

import csv.Row

object CommandProcessor {
  private var tables: Map[String, Table] = Map()

  def execute(command: String): String = {
    val parts = command.trim.split(" ", 3)
    parts match {
      case Array("create", "table", rest) =>
        val tableNameAndColumns = rest.split("\\(", 2)
        if (tableNameAndColumns.length != 2) return "Erreur de syntax lors de la création de table"

        val tableName = tableNameAndColumns(0).trim
        val columns = tableNameAndColumns(1).stripSuffix(")").split(",").map(_.trim).toSeq
        tables += (tableName -> Table.create(tableName, columns))
        s"Table '$tableName' crée avec les colonnes: ${columns.mkString(", ")}"

      case Array(tableName, "add", rest) =>
        val values = rest.stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq
        tables.get(tableName) match {
          case Some(table) =>
            val columns = table.columns
            if (columns.length != values.length) return s"Erreur de nombre de valeurs. Attendu: ${columns.length}, Reçu: ${values.length}"

            val newRow = columns.zip(values).toMap
            tables += (tableName -> table.insert(newRow))
            s"Ligne ajouté à la table '$tableName'"
          case None => s"La table '$tableName' n'existe pas"
        }

      case Array(tableName, "select", rest) =>
        val columns = rest.stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq
        tables.get(tableName) match {
          case Some(table) =>
            val selectedTable = table.select(columns: _*)
            selectedTable.toString
          case None => s"La table '$tableName' n'existe pas"
        }

      case Array(tableName, "filter", condition) =>
        tables.get(tableName) match {
          case Some(table) =>
            val filteredTable = table.filter(row => evaluateCondition(row, condition))
            tables += (tableName -> filteredTable)
            filteredTable.toString
          case None => s"La table '$tableName' n'existe pas"
        }

      case _ => "Commande inconnue"
    }
  }

  private def evaluateCondition(row: Row, condition: String): Boolean = {
    val Array(column, operator, value) = condition.split(" ", 3)
    row.get(column) match {
      case Some(cellValue) =>
        operator match {
          case "=" => cellValue == value
          case ">" => cellValue.toDoubleOption.exists(_ > value.toDouble)
          case "<" => cellValue.toDoubleOption.exists(_ < value.toDouble)
          case _ => false
        }
      case None => false
    }
  }
}

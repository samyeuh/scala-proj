package fr.efrei.fp.project

import com.github.tototoshi.csv.defaultCSVFormat
import csv.Row

object CommandProcessor {
  private var tables: Map[String, Table] = Map()

  def execute(command: String): String = {
    val parts = command.trim.split(" ", 3)
    parts match {
      case Array("create", "table", rest) =>
        val tableNameAndColumns = rest.split("\\(", 2)
        if (tableNameAndColumns.length != 2) return "Erreur de syntaxe lors de la création de table"

        val tableName = tableNameAndColumns(0).trim
        val columns = tableNameAndColumns(1).stripSuffix(")").split(",").map(_.trim).toSeq
        tables += (tableName -> Table.create(tableName, columns))
        s"Table '$tableName' créée avec les colonnes: ${columns.mkString(", ")}"

      case Array(tableName, "add", rest) =>
        val table = getOrLoadTable(tableName)
        val values = rest.stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq
        table match {
          case Some(existingTable) =>
            val columns = existingTable.columns
            if (columns.length != values.length) return s"Erreur de nombre de valeurs. Attendu: ${columns.length}, Reçu: ${values.length}"

            val newRow = columns.zip(values).toMap
            tables += (tableName -> existingTable.insert(newRow))
            s"Ligne ajoutée à la table '$tableName'"
          case None => s"La table '$tableName' n'existe pas et n'a pas pu être chargée"
        }

      case Array(tableName, "select", rest) =>
        val table = getOrLoadTable(tableName)
        val columns = rest.stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq
        table match {
          case Some(existingTable) =>
            val selectedTable = existingTable.select(columns: _*)
            selectedTable.toString
          case None => s"La table '$tableName' n'existe pas et n'a pas pu être chargée"
        }

      case Array(tableName, "filter", condition) =>
        val table = getOrLoadTable(tableName)
        table match {
          case Some(existingTable) =>
            val filteredTable = existingTable.filter(row => evaluateCondition(row, condition))
            tables += (tableName -> filteredTable)
            filteredTable.toString
          case None => s"La table '$tableName' n'existe pas et n'a pas pu être chargée"
        }

      case _ => "Commande inconnue"
    }
  }

  private def getOrLoadTable(tableName: String): Option[Table] = {
    tables.get(tableName).orElse {
      val file = new java.io.File(s"src/main/resources/$tableName.csv")
      if (file.exists()) {
        val reader = com.github.tototoshi.csv.CSVReader.open(file)
        val data = reader.allWithHeaders()
        reader.close()
        val columns = if (data.nonEmpty) data.head.keys.toSeq else Seq.empty
        val loadedTable = Table(tableName, data.map(Row), columns)
        tables += (tableName -> loadedTable)
        Some(loadedTable)
      } else {
        None
      }
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


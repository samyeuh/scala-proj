package fr.efrei.fp.project

import com.github.tototoshi.csv.defaultCSVFormat

import java.util.logging.{Level, Logger}
import scala.util.boundary, boundary.break

object CommandProcessor {
  private val logger: Logger = Logger.getLogger(getClass.getName)
  private var tables: Map[String, Table] = Map()

  def execute(command: String): String = {
    val parts = command.trim.split(" ", 3)
    parts match {
      case Array("create", "table", rest) => handleCreate(rest) match {
        case Left(errorMsg) => s"Erreur : $errorMsg"
        case Right(successMsg) => successMsg
      }

      case Array(tableName, "add", rest) => handleAdd(tableName, rest) match {
        case Left(errorMsg) => s"Erreur : $errorMsg"
        case Right(successMsg) => successMsg
      }

      case Array(tableName, "select", rest) => handleSelect(tableName, rest) match {
        case Left(errorMsg) => s"Erreur : $errorMsg"
        case Right(successMsg) => successMsg
      }

      case Array(tableName, "filter", conditionAvecParanthese) => handleFilter(tableName, conditionAvecParanthese) match {
        case Left(errorMsg) => s"Erreur : $errorMsg"
        case Right(successMsg) => successMsg
      }

      case Array(tableName, "delete") => handleDelete(tableName) match {
        case Left(errorMsg) => s"Erreur : $errorMsg"
        case Right(successMsg) => successMsg
      }

      case Array("help") => handleHelp()

      case _ =>
        logger.log(Level.WARNING, s"Erreur : Commande inconnue : $command")
        s"Erreur : Commande inconnue, tapez help pour connaitre les commandes disponibles"
    }
  }


  private def handleCreate(rest: String): Either[String, String] = {
    val tableNameAndColumns = rest.split("\\(", 2)
    if (tableNameAndColumns.length != 2)
      val errorMsg = "Syntaxe incorrect lors de la création de table"
      logger.log(Level.SEVERE, errorMsg)
      return Left(errorMsg)

    val tableName = tableNameAndColumns(0).trim
    val columnsDefinition = tableNameAndColumns(1).stripSuffix(")").split(",").map(_.trim)
    val columns = columnsDefinition.map { colDef =>
      val parts = colDef.split(" ")
      if (parts.length != 2)
        val errorMsg = s"Syntaxe incorrect dans la définition de colonne '$colDef'"
        logger.log(Level.SEVERE, errorMsg)
        return Left(errorMsg)
      Column(parts(0).trim, ColumnType.fromString(parts(1).trim))
    }
    tables += (tableName -> Table.create(tableName, columns))
    Right(s"Table '$tableName' créée avec les colonnes: ${columns.map(c => s"${c.name} ${c.dataType}").mkString(", ")}")
  }

  private def handleAdd(tableName: String, rest: String): Either[String, String] = {
    val table = getOrLoadTable(tableName)
    val values = rest.stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq
    table match {
      case Some(existingTable) =>
        val columns = existingTable.columns
        if (columns.length != values.length)
          val errorMsg = s"Nombre de valeurs incorrect. Attendu: ${columns.length}, Reçu: ${values.length}"
          logger.log(Level.SEVERE, errorMsg)
          return Left(errorMsg)

        val newRow = columns.zip(values).toMap
        tables += (tableName -> existingTable.insert(newRow))
        Right(s"Ligne ajoutée à la table '$tableName'")
      case None =>
        val errorMsg = s"La table '$tableName' n'existe pas et n'a pas pu être chargée"
        logger.log(Level.SEVERE, errorMsg)
        Left(errorMsg)
    }
  }

  private def handleSelect(tableName: String, rest: String): Either[String, String] = {
    val table = getOrLoadTable(tableName)
    val values = rest.stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq
    val selectParts = rest.split(" order by | limit ", 3)
    val columnsPart = selectParts(0).stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq

    val orderBy = if (rest.contains("order by")) {
      val orderPart = rest.split("order by")(1).split("limit")(0).trim
      val Array(column, direction) = orderPart.split(" ", 2) match {
        case Array(col) => Array(col, "ASC")
        case Array(col, dir) => Array(col, dir)
        case _ =>
          val errorMsg = "Erreur de syntaxe dans la clause 'order by'"
          logger.log(Level.SEVERE, errorMsg)
          return Left(errorMsg)
      }
      Some((column.trim, direction.trim.toUpperCase))
    } else None

    val limit = if (rest.contains("limit")) {
      rest.split("limit")(1).trim.toIntOption
    } else None

    table match {
      case Some(table) =>
        val selectedTable = table.select(columnsPart: _*)
        val orderedTable = orderBy match {
          case Some((col, dir)) => selectedTable.orderBy(col, dir)
          case None => selectedTable
        }
        val limitedTable = limit match {
          case Some(n) => orderedTable.limit(n)
          case None => orderedTable
        }
        Right(limitedTable.toString)
      case None =>
        val errorMsg= s"La table '$tableName' n'existe pas et n'a pas pu être chargée"
        logger.log(Level.SEVERE, errorMsg)
        Left(errorMsg)
    }
  }

  private def handleFilter(tableName: String, rest: String): Either[String, String] = {
    val isDelete = rest.endsWith("delete")
    val condition = if (isDelete) {
      rest.stripSuffix("delete").trim.stripPrefix("(").stripSuffix(")")
    } else {
      rest.stripPrefix("(").stripSuffix(")")
    }

    val table = getOrLoadTable(tableName)

    table match {
      case Some(existingTable) =>
        if (isDelete) {
          val updatedTable = existingTable.delete(row => evaluateCondition(existingTable, row, condition))
          tables += (tableName -> updatedTable)
          val successMessage = s"Lignes correspondant au filtre '$condition' supprimées de la table '$tableName'"
          Right(successMessage)
        } else {
          val filteredTable = existingTable.filter(row => evaluateCondition(existingTable, row, condition))
          Right(filteredTable.toString)
        }
      case None =>
        val errorMsg = s"La table '$tableName' n'existe pas et n'a pas pu être chargée"
        logger.log(Level.SEVERE, errorMsg)
        Left(errorMsg)
    }
  }

  private def handleDelete(tableName: String): Either[String, String] = {
    val table = getOrLoadTable(tableName)
    table match {
      case Some(existingTable) =>
        tables -= tableName
        existingTable.deleteFile()
      case None =>
        return Left(s"La table '$tableName' n'existe pas et n'a pas pu être chargée")
    }
    Right(s"Table '$tableName' supprimée")
  }

  private def handleHelp(): String = {
    """Liste des commandes disponibles :
      |create table nom_table (colonne1 type1, colonne2 type2, ...) => Crée une table avec les colonnes spécifiées
      |add nom_table (valeur1, valeur2, ...) => Ajoute une ligne à la table
      |select nom_table (colonne1, colonne2, ...) [order by colonne [ASC|DESC]] [limit n] => Sélectionne les colonnes spécifiées de la table avec possibilité de tri et de limiter le nombre de lignes
      |filter nom_table (condition) [delete] => Filtre les lignes de la table selon la condition spécifiée. Ajouter 'delete' pour supprimer les lignes correspondantes
      |nom_table delete => Supprime la table
      |help => Affiche cette aide
      |exit => Quitte le terminal
      |""".stripMargin
  }

  private def getOrLoadTable(tableName: String): Option[Table] = {
    tables.get(tableName).orElse {
      val file = new java.io.File(s"src/main/resources/$tableName.csv")
      if (file.exists()) {
        val reader = com.github.tototoshi.csv.CSVReader.open(file)
        val data = reader.allWithHeaders()
        reader.close()
        val columns = if (data.nonEmpty) data.head.keys.map(key => Column(key.split(":")(0), ColumnType.fromString(key.split(":")(1)))).toSeq else Seq.empty
        val loadedTable = Table(tableName, data.map(row => Row(columns.zip(row.values).toMap)), columns)
        tables += (tableName -> loadedTable)
        Some(loadedTable)
      } else {
        None
      }
    }
  }

  private def evaluateCondition(table: Table, row: Row, condition: String): Boolean = {
    if (condition == "*") {
      return true
    }

    val parts = condition.split(" ", 3)
    if (parts.length != 3) {
      throw new IllegalArgumentException(s"Condition invalide : '$condition'. Format attendu : 'colonne opérateur valeur'")
    }

    val Array(column, operator, value) = parts
    val col = table.columns.find(_.name == column).getOrElse(
      throw new IllegalArgumentException(s"La colonne '$column' n'existe pas dans la table")
    )

    col.dataType match {
      case ColumnType.StringType => evaluateString(row, condition)
      case ColumnType.IntType => evaluateInt(row, condition)
      case _ => false
    }
  }

  private def evaluateInt(row: Row, condition: String): Boolean = {
    val Array(column, operator, value) = condition.split(" ", 3)
    row.get(column) match {
      case Some(cellValue) =>
        operator match {
          case "=" => cellValue == value
          case ">" => cellValue.toDoubleOption.exists(_ > value.toDouble)
          case "<" => cellValue.toDoubleOption.exists(_ < value.toDouble)
          case ">=" => cellValue.toDoubleOption.exists(_ >= value.toDouble)
          case "<=" => cellValue.toDoubleOption.exists(_ <= value.toDouble)
          case _ => false
        }
      case None => false
    }
  }

  private def evaluateString(row: Row, str: String): Boolean = {
    val Array(column, operator, value) = str.split(" ", 3)
    row.get(column) match {
      case Some(cellValue) =>
        operator match {
          case "=" => cellValue == value
          case "!=" => cellValue != value
          case "[" => cellValue.startsWith(value)
          case "]" => cellValue.endsWith(value)
          case "%" => cellValue.contains(value)
          case _ => false
        }
      case None => false
    }
  }
}


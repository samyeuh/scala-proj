package fr.efrei.fp.project

import com.github.tototoshi.csv.defaultCSVFormat
import java.io.File
import scala.util.{Using, Try, Success, Failure}
import java.util.logging.{Logger, Level}


/**
 * CommandProcessor handles SQL-like commands.
 */
object CommandProcessor {
  private val logger: Logger = Logger.getLogger(getClass.getName)
  private var tables: Map[String, Table] = Map()

  def execute(command: String): String = {
    Try {
      val parts = command.trim.split(" ", 3)
      parts match {
        case Array("create", "table", rest) => handleCreate(rest)
        case Array(tableName, "add", rest) => handleAdd(tableName, rest)
        case Array(tableName, "select", rest) => handleSelect(tableName, rest)
        case Array(tableName, "filter", rest) => handleFilter(tableName, rest)
        case _ =>
          logger.log(Level.WARNING, s"Commande non reconnue: $command")
          "Commande inconnue"
      }
    } match {
      case Success(result) => result
      case Failure(exception) => 
        logger.log(Level.SEVERE, "Erreur lors de l'exécution de la commande", exception)
        s"Erreur: ${exception.getMessage}"
    }
  }

  private def handleCreate(rest: String): String = {
    val Array(tableName, columnsPart) = rest.split("\(", 2).map(_.trim)
    val columns = columnsPart.stripSuffix(")").split(",").map(_.trim).toSeq
    tables += (tableName -> Table.create(tableName, columns))
    s"Table '$tableName' créée avec les colonnes: ${columns.mkString(", " )}"
  }

  private def handleAdd(tableName: String, rest: String): String = {
    val table = getOrLoadTable(tableName)
    val values = rest.stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq
    table match {
      case Some(existingTable) =>
        if (existingTable.columns.length != values.length) {
          return s"Erreur de nombre de valeurs. Attendu: ${existingTable.columns.length}, Reçu: ${values.length}"
        }
        val newRow = existingTable.columns.zip(values).toMap
        tables += (tableName -> existingTable.insert(newRow))
        s"Ligne ajoutée à la table '$tableName'"
      case None => s"La table '$tableName' n'existe pas."
    }
  }

  private def handleSelect(tableName: String, rest: String): String = {
    val table = getOrLoadTable(tableName)
    val columns = rest.stripPrefix("(").stripSuffix(")").split(",").map(_.trim).toSeq
    table match {
      case Some(existingTable) => existingTable.select(columns: _*).toString
      case None => s"La table '$tableName' n'existe pas."
    }
  }

  private def handleFilter(tableName: String, rest: String): String = {
    val table = getOrLoadTable(tableName)
    val condition = rest.stripPrefix("(").stripSuffix(")")
    table match {
      case Some(existingTable) =>
        val filteredTable = existingTable.filter(row => evaluateCondition(row, condition))
        tables += (tableName -> filteredTable)
        filteredTable.toString
      case None => s"La table '$tableName' n'existe pas."
    }
  }

  private def getOrLoadTable(tableName: String): Option[Table] = {
    tables.get(tableName).orElse {
      val file = new File(s"src/main/resources/$tableName.csv")
      if (file.exists()) {
        Using(CSVReader.open(file)) { reader =>
          val data = reader.allWithHeaders()
          val columns = if (data.nonEmpty) data.head.keys.toSeq else Seq.empty
          val loadedTable = Table(tableName, data.map(Row), columns)
          tables += (tableName -> loadedTable)
          Some(loadedTable)
        }.getOrElse(None)
      } else {
        None
      }
    }
  }

  private def evaluateCondition(row: Row, condition: String): Boolean = {
    val Array(column, operator, value) = condition.split(" ", 3).map(_.trim)
    row.get(column) match {
      case Some(cellValue) => operator match {
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
}


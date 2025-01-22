package fr.efrei.fp.project

sealed trait ColumnType
object ColumnType {
  case object IntType extends ColumnType
  case object StringType extends ColumnType
  case object BooleanType extends ColumnType

  def fromString(typeStr: String): ColumnType = typeStr.toUpperCase match {
    case "INT" | "INTTYPE" => IntType
    case "STRING" | "STRINGTYPE" => StringType
    case _ => throw new IllegalArgumentException(s"Type de colonne inconnu : $typeStr")
  }
}

package fr.efrei.fp.project

enum ColumnType {
  case IntType, StringType
}

object ColumnType {
  def fromString(typeStr: String): ColumnType = typeStr.toUpperCase match {
    case "INT" | "INTTYPE" => IntType
    case "STRING" | "STRINGTYPE" => StringType
    case _ => throw new IllegalArgumentException(s"Type de colonne inconnu : $typeStr")
  }
}
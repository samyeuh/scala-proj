package fr.efrei.fp.project
package csv

import com.github.tototoshi.csv._
import java.io.File

case class Row(data: Map[String, String]) {
  def get(column: String): Option[String] = data.get(column)
}

class Reader {
  def readCSV(filePath: String): Seq[Row] = {
    val reader = CSVReader.open(new File(filePath))
    val rows = reader.allWithHeaders().map(Row)
    reader.close()
    rows
  }
}

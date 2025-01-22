package fr.efrei.fp.project

import org.scalatest.funsuite.AnyFunSuite

class CommandProcessorTests extends AnyFunSuite {

  test("Create table command") {
    val result = CommandProcessor.execute("create table TestTable (id, name, age)")
    assert(result.contains("Table 'TestTable' créée avec les colonnes"))
  }

  test("Add row to existing table") {
    CommandProcessor.execute("create table TestTable (id, name, age)")
    val result = CommandProcessor.execute("TestTable add (1, Alice, 25)")
    assert(result.contains("Ligne ajoutée à la table 'TestTable'"))
  }

  test("Add row to non-existent table returns error") {
    val result = CommandProcessor.execute("NonExistentTable add (1, Alice, 25)")
    assert(result.contains("La table 'NonExistentTable' n'existe pas."))
  }

  test("Select command on existing table") {
    CommandProcessor.execute("create table TestTable (id, name, age)")
    CommandProcessor.execute("TestTable add (1, Alice, 25)")
    CommandProcessor.execute("TestTable add (2, Bob, 30)")
    val result = CommandProcessor.execute("TestTable select (id, name)")
    assert(result.contains("Table: TestTable"))
    assert(result.contains("id"))
    assert(result.contains("name"))
    assert(!result.contains("age"))
  }

  test("Filter command on table") {
    CommandProcessor.execute("create table TestTable (id, name, age)")
    CommandProcessor.execute("TestTable add (1, Alice, 25)")
    CommandProcessor.execute("TestTable add (2, Bob, 30)")
    val result = CommandProcessor.execute("TestTable filter (age = 25)")
    assert(result.contains("Table: TestTable"))
    assert(result.contains("Alice"))
    assert(!result.contains("Bob"))
  }

  test("Unknown command returns error") {
    val result = CommandProcessor.execute("unknown command")
    assert(result.contains("Commande inconnue"))
  }
}

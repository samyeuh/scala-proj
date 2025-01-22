package fr.efrei.fp.project

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommandProcessorTests extends AnyFlatSpec with Matchers {

  "CommandProcessor" should "create a table correctly" in {
    val result = CommandProcessor.execute("create table Students (Name STRING, Age INT)")
    result shouldBe "Table 'Students' créée avec les colonnes: Name StringType, Age IntType"
  }

  it should "add rows to a table" in {
    CommandProcessor.execute("create table Students (Name STRING, Age INT)")
    val addResult1 = CommandProcessor.execute("Students add (Alice, 22)")
    val addResult2 = CommandProcessor.execute("Students add (Bob, 18)")

    addResult1 shouldBe "Ligne ajoutée à la table 'Students'"
    addResult2 shouldBe "Ligne ajoutée à la table 'Students'"
  }

  it should "select rows from a table" in {
    CommandProcessor.execute("create table Students (Name STRING, Age INT)")
    CommandProcessor.execute("Students add (Alice, 22)")
    CommandProcessor.execute("Students add (Bob, 18)")

    val selectResult = CommandProcessor.execute("Students select (Name)")
    selectResult should include("Name")
    selectResult should include("Alice")
    selectResult should include("Bob")
  }

  it should "filter rows from a table" in {
    CommandProcessor.execute("create table Students (Name STRING, Age INT)")
    CommandProcessor.execute("Students add (Alice, 22)")
    CommandProcessor.execute("Students add (Bob, 18)")

    val filterResult = CommandProcessor.execute("Students filter (Age > 20)")
    filterResult should include("Alice")
    filterResult should not include "Bob"
  }

  it should "delete rows based on a filter" in {
    CommandProcessor.execute("create table Students (Name STRING, Age INT)")
    CommandProcessor.execute("Students add (Alice, 22)")
    CommandProcessor.execute("Students add (Bob, 18)")

    val deleteResult = CommandProcessor.execute("Students filter (Age > 20) delete")
    deleteResult shouldBe "Lignes correspondant au filtre 'Age > 20' supprimées de la table 'Students'"

    val selectResult = CommandProcessor.execute("Students select (Name, Age)")
    selectResult should include("Bob")
    selectResult should not include "Alice"
  }

  it should "delete an entire table" in {
    CommandProcessor.execute("create table Students (Name STRING, Age INT)")
    val deleteResult = CommandProcessor.execute("Students delete")

    deleteResult shouldBe "Table 'Students' supprimée"
    val selectResult = CommandProcessor.execute("Students select (Name, Age)")
    selectResult shouldBe "Erreur : La table 'Students' n'existe pas et n'a pas pu être chargée"
  }

  it should "handle invalid commands" in {
    val invalidResult = CommandProcessor.execute("invalid command")
    invalidResult shouldBe "Erreur : Commande inconnue, tapez help pour connaitre les commandes disponibles"
  }

  it should "return help message" in {
    val helpResult = CommandProcessor.execute("help")
    helpResult should include("Liste des commandes disponibles")
    helpResult should include("create table nom_table")
    helpResult should include("add nom_table")
    helpResult should include("filter nom_table")
  }
}

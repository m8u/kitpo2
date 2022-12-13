package src.main.dev.m8u.kitpo.builders

trait MyHashableBuilder {
  def createRandom: Any

  @throws[Exception]
  def parse(s: String): Any
}

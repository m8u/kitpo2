package src.main.dev.m8u.kitpo.builders

import src.main.dev.m8u.kitpo.types.MyDouble

import java.util.concurrent.ThreadLocalRandom


class MyDoubleBuilder extends MyHashableBuilder {
  override def createRandom = new MyDouble(ThreadLocalRandom.current.nextDouble)

  override def parse(s: String) = new MyDouble(s.toDouble)
}

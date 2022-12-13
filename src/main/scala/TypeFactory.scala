package src.main.dev.m8u.kitpo

import src.main.dev.m8u.kitpo.builders.MyDatetimeBuilder
import src.main.dev.m8u.kitpo.builders.MyDoubleBuilder
import src.main.dev.m8u.kitpo.builders.MyHashableBuilder

import java.util


object MyHashmapStorable extends Enumeration {
  type MyHashmapStorable = Value
  val MyDouble, MyDatetime = Value
}

object TypeFactory {
  def getTypeNames: util.ArrayList[String] = {
    val list = new util.ArrayList[String]
    for (t <- MyHashmapStorable.values) {
      list.add(String.valueOf(t))
    }
    list
  }

  def getBuilderByName(name: String): MyHashableBuilder = {
    if ("MyDouble" == name) return new MyDoubleBuilder
    else if ("MyDatetime" == name) return new MyDatetimeBuilder
    null
  }
}

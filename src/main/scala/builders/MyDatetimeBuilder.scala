package src.main.dev.m8u.kitpo.builders

import src.main.dev.m8u.kitpo.types.MyDatetime

import java.util.concurrent.ThreadLocalRandom


class MyDatetimeBuilder extends MyHashableBuilder {
  override def createRandom: Any = {
    var datetime : MyDatetime = null
    while (datetime == null)
      try {
        datetime = new MyDatetime(ThreadLocalRandom.current.nextInt(1900, 2030),
          ThreadLocalRandom.current.nextInt(1, 12 + 1),
          ThreadLocalRandom.current.nextInt(1, 31 + 1),
          ThreadLocalRandom.current.nextInt(0, 23 + 1),
          ThreadLocalRandom.current.nextInt(0, 59 + 1),
          ThreadLocalRandom.current.nextInt(0, 59 + 1))
      } catch {
        case _: Exception =>
      }
    datetime
  }

  @throws[Exception]
  override def parse(s: String): Any = {
    val minusSplit = s.split("-")
    val year = minusSplit(0).toInt
    val month = minusSplit(1).toInt
    val whitespaceSplit = minusSplit(2).split(" ")
    val day = whitespaceSplit(0).toInt
    val colonSplit = whitespaceSplit(1).split(":")
    val hour = colonSplit(0).toInt
    val minute = colonSplit(1).toInt
    val second = colonSplit(2).toInt
    new MyDatetime(year, month, day, hour, minute, second)
  }
}

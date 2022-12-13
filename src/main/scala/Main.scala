package src.main.dev.m8u.kitpo

import com.m8u.kitpo2.GUI
import src.main.dev.m8u.kitpo.builders.MyHashableBuilder

import java.io.FileOutputStream
import java.io.IOException
import java.util


object Main {
  @throws[Exception]
  def main(args: Array[String]): Unit = {
    test("MyDouble", 8000)
    test("MyDouble", 10000)
    test("MyDatetime", 8000)
    test("MyDatetime", 10000)

    val gui: GUI = new GUI
    gui.setVisible(true)
  }

  @throws[IOException]
  private def test(typeName: String, items: Int): Unit = {
    val builder: MyHashableBuilder = TypeFactory.getBuilderByName(typeName)
    val hashtable: ChainedHashtable = new ChainedHashtable(typeName)
    val timeMA: util.ArrayList[Long] = new util.ArrayList[Long]
    val timeMAWindow: Int = 100
    var chainLengthSum: Double = 0
    var occupancySum: Double = 0
    for (_ <- 1 to items) {
      val key: Any = builder.createRandom
      val start: Long = System.currentTimeMillis
      hashtable.set(key, null)
      val stop: Long = System.currentTimeMillis
      timeMA.add(stop - start)
      if (timeMA.size > timeMAWindow) {
        timeMA.remove(0)
      }
      chainLengthSum += hashtable.getAverageChainLength
      occupancySum += hashtable.getOccupancy
    }
    System.out.printf(
      """
      ============ %s (%d) test complete ============
      Final capacity: %d
      Final avg. chain length: %.2f
      Final occupancy: %.2f%%
      All-time avg. chain length: %.2f
      All-time avg. occupancy: %.2f%%

      """, typeName, items, hashtable.getCapacity, hashtable.getAverageChainLength, hashtable.getOccupancy * 100, chainLengthSum / items, occupancySum / items * 100)
  }
}

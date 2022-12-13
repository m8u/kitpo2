package src.main.dev.m8u.kitpo

import org.json.JSONArray
import org.json.JSONObject

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import scala.collection.mutable


object ChainedHashtable {
  private val INITIAL_SIZE = 4

  @throws[Exception]
  def loadFromJSON(fis: FileInputStream) = {
    val bytes = fis.readAllBytes
    val json = new String(bytes, StandardCharsets.UTF_8)
    val jsonObject = new JSONObject(json)
    val keyTypeName = jsonObject.get("keyType").toString
    val keyBuilder = TypeFactory.getBuilderByName(keyTypeName)
    val hashtable = new ChainedHashtable(keyTypeName)
    val jsonHashtable = jsonObject.getJSONObject("hashtable")
    val size = jsonHashtable.getInt("size")
    val jsonHashtableData = jsonHashtable.getJSONArray("data")
    for (i <- 0 until size) {
      val jsonChain = jsonHashtableData.getJSONObject(i)
      val nodesCount = jsonChain.getInt("size")
      val jsonChainData = jsonChain.getJSONArray("data")
      for (j <- 0 until nodesCount) {
        val jsonNode = jsonChainData.getJSONObject(j)
        hashtable.set(keyBuilder.parse(jsonNode.get("key").asInstanceOf[String]), jsonNode.get("value"))
      }
    }
    hashtable
  }
}

class ChainedHashtable(val keyTypeName: String) extends Iterable[chain] {
  var mapSize: Int = ChainedHashtable.INITIAL_SIZE
  var _map : Array[chain] = new Array[chain](mapSize)

  for (i <- 0 until ChainedHashtable.INITIAL_SIZE) {
    _map(i) = new chain
  }
  private var map = null

  def set(key: Any, value: Any): Unit = {
    val index = Math.abs(key.hashCode) % this.mapSize
    this._map(index).set(key, value, () => this.expand())
  }

  def get(key: Any): Any = {
    val index = key.hashCode % this.mapSize
    this._map(index).get(key)
  }

  def remove(key: Any): Any = {
    val index = key.hashCode % this.mapSize
    this._map(index).remove(key)
  }

  private[kitpo] def expand(): Unit = {
    val old = this._map
    this.mapSize *= 2
    this._map = new Array[chain](this.mapSize)
    for (i <- 0 until this.mapSize) {
      _map(i) = new chain
    }
    old.foreach(chain => chain.foreach(chainNode => this.set(chainNode.key, chainNode.value)))
  }

  def getCapacity: Int = this.mapSize

  override def toString: String = map.toString

  override def iterator: Iterator[chain] = new Iterator[chain]() {
    private[kitpo] var i: Int = -1

    override def hasNext: Boolean = i < mapSize-1

    override def next: chain = {
      i += 1
      _map(i)
    }
  }

  override def foreach[U](action: chain => U): Unit = super.foreach(action)

  @throws[IOException]
  private[kitpo] def saveAsJSON(fos: FileOutputStream): Unit = {
    var json = "{\"keyType\": \"%s\",\"hashtable\": %s}"
    val jsonHashtable = new JSONObject
    jsonHashtable.put("size", this.getCapacity)
    val jsonHashtableData = new JSONArray
    for (chain <- this) {
      val jsonChain = new JSONObject
      val jsonChainData = new JSONArray
      var nodeCount = 0
      for (node <- chain) {
        val jsonNode = new JSONObject
        jsonNode.put("key", node.key.toString)
        jsonNode.put("value", node.value.toString)
        jsonChainData.put(jsonNode)
        nodeCount += 1
      }
      jsonChain.put("size", nodeCount)
      jsonChain.put("data", jsonChainData)
      jsonHashtableData.put(jsonChain)
    }
    jsonHashtable.put("data", jsonHashtableData)
    json = String.format(json, this.keyTypeName, jsonHashtable)
    fos.write(json.getBytes)
  }

  def getKeyTypeName: String = this.keyTypeName

  def getAverageChainLength: Double = {
    var avgChainLength:Double = 0
    var length = 0
    for (c <- this) {
      length = 0
      for (_ <- c) {
        length += 1
      }
      avgChainLength += length
    }
    avgChainLength /= this.getCapacity
    avgChainLength
  }

  def getOccupancy: Double = {
    var nonEmptyCount: Double = 0
    for (c <- this) {
      if (c._head != null) {
        nonEmptyCount += 1
      }
    }
    nonEmptyCount / this.getCapacity
  }
}

object chain {
  val CHAIN_MAX_LENGTH = 5
}

class chain private[kitpo]() extends Iterable[chainNode] {
  private[kitpo] var _head : chainNode = _

  private[kitpo] def set(key: Any, value: Any, hashmap: expandable): Unit = {
    if (this._head == null) {
      this._head = new chainNode(key, value)
      return
    }
    if (this._head.key == key) {
      this._head.value = value
      return
    }
    var node = this._head
    var len = 2
    while ( {
      node.next != null
    }) {
      if (node.next.key == key) {
        node.next.value = value
        return
      }
      node = node.next
      len += 1
    }
    node.next = new chainNode(key, value)
    if (len > chain.CHAIN_MAX_LENGTH) {
      hashmap.expand()
    }
  }

  private[kitpo] def get(key: Any): Any = {
    var node = this.head
    while ( {
      node != null
    }) {
      if (node.key == key) return node.value
      node = node.next
    }
    null
  }

  private[kitpo] def remove(key: Any): Any = {
    var node : chainNode = this._head
    var prev : chainNode = null
    while ( {
      node != null && node.key != key
    }) {
      prev = node
      node = node.next
    }
    if (node == null) return null
    if (prev == null) this._head = this._head.next
    else prev.next = node.next
    node.value
  }

  override def toString: String = {
    val str = new mutable.StringBuilder
    var node = this._head
    while ( {
      node != null && node.key != null
    }) {
      str.append("{" + node.key + ":" + node.value + "}")
      node = node.next
    }
    str.toString
  }



  override def foreach[U](action: chainNode => U): Unit = super.foreach(action)

  override def iterator: Iterator[chainNode] = new Iterator[chainNode]() {
    private[kitpo] var current = _head

    override def hasNext: Boolean = current != null

    override def next: chainNode = {
      val currentRef = current
      current = current.next
      currentRef
    }
  }
}

class chainNode private[kitpo](var _key: Any, var _value: Any) {
  val key : Any = _key
  var value: Any = _value
  var next: chainNode = null
}

trait expandable {
  def expand(): Unit
}

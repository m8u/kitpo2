package src.main.dev.m8u.kitpo.types

class MyDouble(var value: Double) {
  override def toString: String = String.valueOf(this.value)

  override def equals(other: Any): Boolean = {
    val otherClass = other.getClass
    if (otherClass == classOf[MyDouble]) return this.toString == other.toString
    throw new RuntimeException("MyDouble.equals() is not defined for class " + otherClass.getName)
  }

  override def hashCode: Int = this.toString.hashCode
}

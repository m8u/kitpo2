package src.main.dev.m8u.kitpo.types

object MyDatetime {
  private val GREGORIAN_CALENDAR_SWITCH_YEAR = 1582

  private def isLeapYear(year: Int): Boolean = {
    if (year > GREGORIAN_CALENDAR_SWITCH_YEAR) return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    year % 4 == 0
  }
}

class MyDatetime
(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int, val second: Int) {
  _year = year
  _month = month
  _day = day
  _hour = hour
  _minute = minute
  _second = second

  private[this] var __year: Int = year
  private def _year: Int = __year
  @throws[Exception]
  private def _year_=(value: Int): Unit = {
    if (year <= 0) throw new Exception("Invalid year value")
    __year = value
  }

  private[this] var __month: Int = month
  private def _month: Int = __month
  @throws[Exception]
  private def _month_=(value: Int): Unit = {
    if (month < 1 || month > 12) throw new Exception("Invalid month value")
    __month = value
  }

  private[this] var __day: Int = day
  private def _day: Int = __day
  @throws[Exception]
  private def _day_=(value: Int): Unit = {
    __day = value
    if (!isDayValid) throw new Exception("Invalid day value")
  }

  private[this] var __hour: Int = hour
  private def _hour: Int = __hour
  @throws[Exception]
  private def _hour_=(value: Int): Unit = {
    if (hour < 0 || hour > 23) throw new Exception("Invalid hour value")
    __hour = value
  }

  private[this] var __minute: Int = minute
  private def _minute: Int = __minute
  @throws[Exception]
  private def _minute_=(value: Int): Unit = {
    if (minute < 0 || minute > 59) throw new Exception("Invalid minute value")
    __minute = value
  }

  private[this] var __second: Int = second
  private def _second: Int = __second
  @throws[Exception]
  private def _second_=(value: Int): Unit = {
    if (second < 0 || second > 59) throw new Exception("Invalid second value")
    __second = value
  }

  private def isDayValid : Boolean = if ((_month == 4 || _month == 6 || _month == 9 || _month == 11) && _day == 31) false
  else if (_month == 2 && MyDatetime.isLeapYear(_year) && _day > 29) false
  else _month != 2 || _day <= 28

  override def toString: String = String.format("%04d-%02d-%02d %02d:%02d:%02d", _year, _month, _day, _hour, _minute, _second)

  override def equals(other: Any): Boolean = {
    val otherClass = other.getClass
    if (otherClass == classOf[MyDatetime]) return this.toString == other.toString
    throw new RuntimeException("MyDatetime.equals() is not defined for class " + otherClass.getName)
  }

  override def hashCode: Int = this.toString.hashCode
}

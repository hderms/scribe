package com.outr.scribe.formatter

import com.outr.scribe.{Platform, LogRecord}

case class FormatterBuilder(formatters: List[LogRecord => String] = Nil) extends Formatter {
  def string(s: String): FormatterBuilder = add(_ => s)

  def message: FormatterBuilder = add(record => String.valueOf(record.message))

  def date(format: String = "%1$tY.%1$tm.%1$td %1$tT:%1$tL"): FormatterBuilder =
    add(record => Platform.formatDate(format, record.timestamp))

  def threadName: FormatterBuilder = add(_.threadName)

  def level: FormatterBuilder = add(_.level.name)
  def levelPaddedRight: FormatterBuilder = add(_.level.namePaddedRight)

  def className: FormatterBuilder = add(_.name)
  def classNameAbbreviated: FormatterBuilder = add(record => FormatterBuilder.abbreviate(record.name))

  def methodName: FormatterBuilder = add(_.methodName.getOrElse("Unknown method"))
  def lineNumber: FormatterBuilder = add(_.lineNumber.toString)

  def newLine: FormatterBuilder = add(_ => Platform.LineSeparator)

  def add(item: LogRecord => String): FormatterBuilder = copy(formatters = formatters ++ Seq(item))

  def format(record: LogRecord): String =
    formatters.foldLeft("") { case (str, f) => str + f(record) }
}

object FormatterBuilder {
  final def abbreviate(className: String): String = {
    val parts = className.split('.')
    val last = parts.length - 1
    parts.zipWithIndex.map {
      case (cur, i) if i == last => cur
      case (cur, _)              => cur.head
    }.mkString(".")
  }
}
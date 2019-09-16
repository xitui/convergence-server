package com.convergencelabs.server.api.rest

import org.json4s.JsonAST.JBool
import org.json4s.JsonAST.JDouble
import org.json4s.JsonAST.JNull
import org.json4s.JsonAST.JString
import org.json4s.JsonAST.JValue

import com.convergencelabs.server.domain.model.data.ArrayValue
import com.convergencelabs.server.domain.model.data.BooleanValue
import com.convergencelabs.server.domain.model.data.DataValue
import com.convergencelabs.server.domain.model.data.DoubleValue
import com.convergencelabs.server.domain.model.data.NullValue
import com.convergencelabs.server.domain.model.data.ObjectValue
import com.convergencelabs.server.domain.model.data.StringValue
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JArray
import com.convergencelabs.server.domain.model.data.DateValue
import org.json4s.JsonAST.JLong

object DataValueToJValue {
  val ConvergenceTypeFlag = "$$convergence_type"

  val DateTypeValue = "date";

  def toJOject(objectValue: ObjectValue): JObject = {
    toJson(objectValue).asInstanceOf[JObject]
  }

  def toJson(dataValue: DataValue): JValue = {
    dataValue match {
      case NullValue(_) =>
        JNull
      case DoubleValue(_, v) =>
        JDouble(v)
      case BooleanValue(_, v) =>
        JBool(v)
      case StringValue(_, v) =>
        JString(v)
      case ObjectValue(_, v) =>
        val fields = v map { case (k, v) => (k, toJson(v)) }
        JObject(fields.toList)
      case ArrayValue(_, v) =>
        val values = v map (v => toJson(v))
        JArray(values)
      case DateValue(_, v) =>
        JObject(
          (ConvergenceTypeFlag -> JString(DateTypeValue)),
          (DateTypeValue -> JDouble(v.toEpochMilli())))
    }
  }
}
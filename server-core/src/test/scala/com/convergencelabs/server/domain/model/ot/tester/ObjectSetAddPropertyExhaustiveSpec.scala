package com.convergencelabs.server.domain.model.ot

import org.json4s.JsonDSL.int2jvalue

import ObjectOperationExhaustiveSpec.NewProperties
import ObjectOperationExhaustiveSpec.NewValues
import ObjectOperationExhaustiveSpec.SetObjects

class ObjectSetAddPropertyExhaustiveSpec extends ObjectOperationExhaustiveSpec[ObjectSetOperation, ObjectAddPropertyOperation] {

  val serverOperationType: String = "ObjectSetOperation"
  val clientOperationType: String = "ObjectAddPropertyOperation"

  def generateCases(): List[TransformationCase[ObjectSetOperation, ObjectAddPropertyOperation]] = {
    for {
      prop1 <- NewProperties
      value1 <- NewValues
      newObject <- SetObjects
    } yield TransformationCase(
      ObjectSetOperation(List(), false, newObject),
      ObjectAddPropertyOperation(List(), false, prop1, value1))
  }

  def transform(s: ObjectSetOperation, c: ObjectAddPropertyOperation): (DiscreteOperation, DiscreteOperation) = {
    ObjectSetAddPropertyTF.transform(s, c)
  }
}

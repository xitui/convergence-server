package com.convergencelabs.server.domain.model.ot

import org.json4s.JsonAST.JObject
import org.scalatest.Finders
import org.scalatest.Matchers
import org.scalatest.WordSpec

class ObjectSetRemovePropertyTFSpec extends WordSpec with Matchers {

  "A ObjectSetRemovePropertyTF" when {

    "tranforming a set and a remove property operation " must {
      "noOp the remove property and not transform the set" in {
        val s = ObjectSetOperation(List(), false, JObject())
        val c = ObjectRemovePropertyOperation(List(), false, "prop")

        val (s1, c1) = ObjectSetRemovePropertyTF.transform(s, c)

        s1 shouldBe s
        c1 shouldBe ObjectRemovePropertyOperation(List(), true, "prop")
      }
    }
  }
}

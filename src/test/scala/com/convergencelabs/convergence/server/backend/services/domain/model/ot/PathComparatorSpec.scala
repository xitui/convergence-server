/*
 * Copyright (c) 2019 - Convergence Labs, Inc.
 *
 * This file is part of the Convergence Server, which is released under
 * the terms of the GNU General Public License version 3 (GPLv3). A copy
 * of the GPLv3 should have been provided along with this file, typically
 * located in the "LICENSE" file, which is part of this source code package.
 * Alternatively, see <https://www.gnu.org/licenses/gpl-3.0.html> for the
 * full text of the GPLv3 license, if it was not provided.
 */

package com.convergencelabs.convergence.server.backend.services.domain.model.ot

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PathComparatorSpec extends AnyWordSpec with Matchers {

  private val parent = List("level1")

  private val child = parent :+ "level2"
  private val grandChild = child :+ "level3"

  private val childSibling = parent :+ "level2a"
  private val grandChildSibling = childSibling :+ "level3a"
  private val deeperCousin = grandChildSibling :+ "level4a"

  "A PathComparator" when {

    "evaluating a descendant path relationship" must {
      "return true for a grand child being a descendant of its grand parent" in {
        val foo = PathComparator.isDescendantOf(grandChild, parent)
        foo shouldBe true
      }

      "return true for a child being a descendant of its direct parent" in {
        PathComparator.isDescendantOf(child, parent) shouldBe true
      }

      "return false for a sibling paths" in {
        PathComparator.isDescendantOf(childSibling, child) shouldBe false
      }

      "return false for a child testing a parent path" in {
        PathComparator.isDescendantOf(parent, child) shouldBe false
      }

      "return false for a grandchild testing a deeper cousin" in {
        PathComparator.isDescendantOf(deeperCousin, grandChild) shouldBe false
      }
    }

    "evaluating a child path relationship" must {

      "return true for a child under a parent" in {
        PathComparator.isChildOf(child, parent) shouldBe true
      }

      "return false for a parent being a child of its child" in {
        PathComparator.isChildOf(parent, child) shouldBe false
      }

      "return false for a indirect descendant being a child of its ancestor" in {
        PathComparator.isChildOf(grandChild, parent) shouldBe false
      }

      "return false for a sibling being a child of a sibling" in {
        PathComparator.isChildOf(grandChildSibling, child) shouldBe false
      }
    }

    "evaluating an ancestor path relationship" must {
      "return true for a parent being an ancestor of a direct child" in {
        PathComparator.isAncestorOf(parent, child) shouldBe true
      }

      "return true for a parent being an ancestor of an indirec direct descendant" in {
        PathComparator.isAncestorOf(parent, grandChild) shouldBe true
      }

      "return false for a child being an ancestor of its parent" in {
        PathComparator.isAncestorOf(child, parent) shouldBe false
      }

      "return false for a higher order sibling being an ancestor of a siblings descendant" in {
        PathComparator.isAncestorOf(child, grandChildSibling) shouldBe false
      }
    }

    "evaluating a parent path relationship" must {
      "return true for a parent being an ancestor of a direct child" in {
        PathComparator.isParentOf(parent, child) shouldBe true
      }

      "return false for a parent being an ancestor of an indirect descendant" in {
        PathComparator.isParentOf(parent, grandChild) shouldBe false
      }

      "return false for a child being an ancestor of its parent" in {
        PathComparator.isParentOf(child, parent) shouldBe false
      }

      "return false for an path being an ancestor of a siblings child" in {
        PathComparator.isParentOf(child, grandChildSibling) shouldBe false
      }
    }

    "evaluating a sibling path relationship" must {
      "return true for an path being a sibling of a sibling" in {
        PathComparator.areSiblings(child, childSibling) shouldBe true
      }

      "return false for a path being a sibling of itself" in {
        PathComparator.areSiblings(child, child) shouldBe false
      }

      "return false for a parent being a sibling of a child" in {
        PathComparator.areSiblings(parent, child) shouldBe false
      }

      "return false for a child being a sibling of its parent" in {
        PathComparator.areSiblings(child, parent) shouldBe false
      }

      "return false for a child being a sibling of its cousin" in {
        PathComparator.areSiblings(grandChildSibling, grandChild) shouldBe false
      }

      "return false for two empty paths being siblings" in {
        PathComparator.areSiblings(List(), List()) shouldBe false
      }
    }

    "evaluating the equality of paths" must {
      "return true paths that are equal" in {
        PathComparator.areEqual(List("1", 2, "3"), List("1", 2, "3")) shouldBe true
      }

      "return false for paths that are unequal" in {
        PathComparator.areEqual(List("1", 2, "3"), List(1, "2", 3)) shouldBe false
      }
    }
  }
}

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

package com.convergencelabs.convergence.server.backend.services.domain.model.ot.xform.obj

import com.convergencelabs.convergence.server.backend.services.domain.model.ot._
import com.convergencelabs.convergence.server.backend.services.domain.model.ot.xform.OperationTransformationFunction

private[ot] object ObjectRemovePropertyRemovePropertyTF extends OperationTransformationFunction[ObjectRemovePropertyOperation, ObjectRemovePropertyOperation] {
  def transform(s: ObjectRemovePropertyOperation, c: ObjectRemovePropertyOperation): (ObjectRemovePropertyOperation, ObjectRemovePropertyOperation) = {
    if (s.property != c.property) {
      // O-RR-1
      (s, c)
    } else {
      // O-RR-2
      (s.copy(noOp = true), c.copy(noOp = true))
    }
  }
}

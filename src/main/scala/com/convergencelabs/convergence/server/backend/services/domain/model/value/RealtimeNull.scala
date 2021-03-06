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

package com.convergencelabs.convergence.server.backend.services.domain.model.value

import com.convergencelabs.convergence.server.backend.services.domain.model.ot.{AppliedDiscreteOperation, DiscreteOperation}
import com.convergencelabs.convergence.server.model.domain.model.NullValue

import scala.util.{Failure, Try}

private[model] final class RealtimeNull(value: NullValue,
                                        parent: Option[RealtimeContainerValue],
                                        parentField: Option[Any])
  extends RealtimeValue(value.id, parent, parentField, List()) {

  def data(): Null = {
    null // scalastyle:ignore null
  }

  def dataValue(): NullValue = {
    value
  }

  protected def processValidatedOperation(op: DiscreteOperation): Try[AppliedDiscreteOperation] = {
    Failure(new IllegalArgumentException("Invalid operation type for RealTimeNull: " + op));
  }
}

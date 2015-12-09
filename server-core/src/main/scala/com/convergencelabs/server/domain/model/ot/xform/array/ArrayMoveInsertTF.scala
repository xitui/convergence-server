package com.convergencelabs.server.domain.model.ot

private[ot] object ArrayMoveInsertTF extends OperationTransformationFunction[ArrayMoveOperation, ArrayInsertOperation] {
  def transform(s: ArrayMoveOperation, c: ArrayInsertOperation): (ArrayMoveOperation, ArrayInsertOperation) = {
    if (ArrayMoveRangeHelper.isIdentityMove(s)) {
      (s, c)
    } else if (ArrayMoveRangeHelper.indexAfterRange(s, c.index)) {
      (s, c)
    } else if (ArrayMoveRangeHelper.indexBeforeRange(s, c.index)) {
      (s.copy(fromIndex = s.fromIndex + 1, toIndex = s.toIndex + 1), c)
    } else if (ArrayMoveRangeHelper.isForwardMove(s)) {
      transformAgainstForwardMove(s, c)
    } else if (ArrayMoveRangeHelper.isBackwardMoveMove(s)) {
      transformAgainstBackwardMove(s, c)
    } else {
      throw new UnsupportedOperationException(s"An unanticipated Move-Insert case was detected ($s, $c)")
    }
  }

  def transformAgainstForwardMove(s: ArrayMoveOperation, c: ArrayInsertOperation): (ArrayMoveOperation, ArrayInsertOperation) = {
    if (s.fromIndex == c.index) {
      (s.copy(fromIndex = s.fromIndex + 1, toIndex = s.toIndex + 1), c)
    } else if (ArrayMoveRangeHelper.indexWithinRange(s, c.index) || s.toIndex == c.index) {
      (s.copy(toIndex = s.toIndex + 1), c.copy(index = c.index - 1))
    } else {
      (s, c)
    }
  }

  def transformAgainstBackwardMove(s: ArrayMoveOperation, c: ArrayInsertOperation): (ArrayMoveOperation, ArrayInsertOperation) = {
    if (s.fromIndex == c.index
      || ArrayMoveRangeHelper.indexWithinRange(s, c.index)
      || s.toIndex == c.index) {
      (s.copy(fromIndex = s.fromIndex + 1), c.copy(index = c.index + 1))
    } else {
      (s, c)
    }
  }
}

package com.convergencelabs.server.domain.model.ot

private[ot] object ArrayReplaceMoveTF extends OperationTransformationFunction[ArrayReplaceOperation, ArrayMoveOperation] {
  def transform(s: ArrayReplaceOperation, c: ArrayMoveOperation): (ArrayReplaceOperation, ArrayMoveOperation) = {
    if (ArrayMoveRangeHelper.isIdentityMove(c)) {
      (s, c)
    } else if (ArrayMoveRangeHelper.indexBeforeRange(c, s.index)
      || ArrayMoveRangeHelper.indexAfterRange(c, s.index)) {
      (s, c)
    } else if (ArrayMoveRangeHelper.isForwardMove(c)) {
      transformAgainstForwardMove(s, c)
    } else if (ArrayMoveRangeHelper.isBackwardMoveMove(c)) {
      transformAgainstBackwardMove(s, c)
    } else {
      throw new UnsupportedOperationException(s"An unanticipated Replace-Move case was detected ($s, $c)")
    }
  }

  def transformAgainstForwardMove(s: ArrayReplaceOperation, c: ArrayMoveOperation): (ArrayReplaceOperation, ArrayMoveOperation) = {
    if (c.fromIndex == s.index) {
      (s.copy(index = c.toIndex), c)
    } else if (ArrayMoveRangeHelper.indexWithinRange(c, s.index) || c.toIndex == s.index) {
      (s.copy(index = s.index - 1), c)
    } else {
      (s, c)
    }
  }

  def transformAgainstBackwardMove(s: ArrayReplaceOperation, c: ArrayMoveOperation): (ArrayReplaceOperation, ArrayMoveOperation) = {
    if (c.fromIndex == s.index) {
      (s.copy(index = c.toIndex), c)
    } else if (ArrayMoveRangeHelper.indexWithinRange(c, s.index) || c.toIndex == s.index) {
      (s.copy(index = s.index + 1), c)
    } else {
      (s, c)
    }
  }
}

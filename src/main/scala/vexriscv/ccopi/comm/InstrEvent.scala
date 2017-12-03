package vexriscv.ccopi.comm

import spinal.core._

/**
  * Created by jens on 28.11.17.
  */
class InstrEvent extends Area {

  def outsideCondScope[T](that : => T) : T = {
    val body = Component.current.dslBody
    body.push()
    val swapContext = body.swap()
    val ret = that
    body.pop()
    swapContext.appendBack()
    ret
  }
}

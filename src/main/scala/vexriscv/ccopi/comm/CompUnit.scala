package vexriscv.ccopi.comm


import scala.collection.mutable.ArrayBuffer
import vexriscv.ccopi.utils._
import spinal.core._
/**
  * Created by jens on 28.11.17.
  */
abstract trait CompUnit {

  val functions = new ArrayBuffer[InstrBaseFunction]

  def setup() : Unit

  def build() : Unit = {
    setup

    assert(
      assertion = functions.length != 0,
      message = s"Computation unit '${this.getClass.getSimpleName}' has no activated functions"
    )

    functions.foreach { f =>
      f.build()
    }
  }

  def activate(func: InstrBaseFunction*) : Unit = {
    functions ++= func
  }

  Component.current.addPrePopTask(() => build())
}

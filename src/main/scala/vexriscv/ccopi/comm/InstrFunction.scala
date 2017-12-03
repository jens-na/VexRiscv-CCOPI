package vexriscv.ccopi.comm
import spinal.core._
import spinal.lib._
import vexriscv.VexRiscv

import scala.collection.mutable.ArrayBuffer

abstract class InstrBaseFunction extends Area {
  val name : String = "[unnamed]"
  val description : String = "[no description]"
  val pattern : MaskedLiteral

  def build() : Unit

  override def toString(): String = {
    s"[ ${name}, ${description}, ${pattern.toString()}"
  }
}

/**
  * Created by jens on 28.11.17.
  */
abstract class InstrFunction[A <: CCOPICmd, B <: CCOPIRsp](dtCmd : A, dtRsp : B) extends InstrBaseFunction {
  val cmd = slave Stream (dtCmd)
  val rsp = master Stream (dtRsp)
  var events = ArrayBuffer[InstrEvent]()

  events ++= List.fill(2)(new InstrEvent())
  val incoming :: working :: Nil = events.toList

  def build() : Unit

  /**
    * Implicit class to create the areas for each
    * instruction event
    * @param ev the event to define
    */
  implicit class implicitsEvent(ev: InstrEvent){
    def event[T <: Area](area : T) : T = {area.setCompositeName(ev,getName()).reflectNames();area}
  }

  Component.current.addPrePopTask(() => build())
}

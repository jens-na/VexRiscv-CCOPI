package vexriscv.ccopi.comm

import spinal.core._
import spinal.lib._

import scala.collection.mutable.ArrayBuffer



abstract class BaseFunction extends Component

abstract class RequestAndResponseFunction[A <: CCOPICmd, B <: CCOPIRsp](dataTypeCmd : A, dataTypeRsp : B) extends BaseFunction with CCOPIEventHandler {
  val cmd = slave Stream (dataTypeCmd)
  val rsp = master Stream (dataTypeRsp)
  val isBusy = false

  events ++= List.fill(2)(new CCOPIEvent())
  val newInstr :: idle :: Nil = events.toList

  private def internalIdle : Unit = {

  }

  /**
    *
    * @param cmd
    * @param rsp
    * @return
    */
  def build(cmd : A, rsp : B) : Unit

  implicit class implicitsEvent(ev: CCOPIEvent){
    def event[T <: Area](area : T) : T = {area.setCompositeName(ev,getName()).reflectNames();area}
  }
}


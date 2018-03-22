/*
 * The MIT License (MIT)
 * Copyright (c) 2017, Jens Nazarenus, Dominik Swierzy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package vexriscv.ccopi

import spinal.core._
import spinal.lib._
import Utilities._

trait FunctionDef extends Nameable {
  val pattern : String // Required
  val name : String // default => [unnamed]
  val description : String // default => [no description]

  override def getName() = name
}

abstract class InstructionFunction[+A <: InputBundle, +B <: OutputBundle](dtCmd : A, dtRsp : B) extends FunctionDef {
  val io = new Bundle {
    val cmd = slave Stream (dtCmd.asInstanceOf[InputBundle])
    val rsp = master Stream (dtRsp.asInstanceOf[OutputBundle])
    val interrupt = out Bool
  }

  val interruptCtl = dtRsp.isInstanceOf[InterruptBundle]

  // Payload of the command, prepared as specific bundle
  private[this] val cmdPayloadReg = Reg(Bits(io.cmd.payload.getBitsWidth bits))
  val command = cloneOf(dtCmd)
  command.assignFromBits(cmdPayloadReg.asBits)

  // Payload of the response, prepared as specific bundle
  val response = cloneOf(dtRsp)
  io.rsp.payload.assignFromBits(response.asBits)

  val flush = RegInit(False)
  val done = RegInit(True)

  if(interruptCtl) {
    io.interrupt := flush
  } else {
    io.interrupt := False
  }

  // Communication of the Ready/Valid-Interface
  io.cmd.ready := False
  io.rsp.valid := flush // Send response when user sets flush

  when(io.rsp.ready){
    flush := False
  }

  when(done) {
    when(!flush || io.rsp.ready) { // New command
      done := !io.cmd.valid
      flush := False
      io.cmd.ready := True
      io.rsp.payload.assignFromBits(B(io.rsp.payload.getBitsWidth bits, default -> false))
      cmdPayloadReg := io.cmd.payload.asBits
    }
  }.otherwise{
    when(flush){
      done := True
      io.cmd.ready := True
    }
  }

  def setup(): Unit = {
    io.cmd.setWeakName(s"${name}_communication_cmd")
    io.rsp.setWeakName(s"${name}_communication_rsp")
    io.interrupt.setWeakName(s"${name}_communication_interrupt")
    flush.setWeakName(s"${name}_communication_flush")
    done.setWeakName(s"${name}_communication_done")
    cmdPayloadReg.setWeakName(s"${name}_communication_cmdPayloadReg")
  }


  def build(controller : EventController) : Unit

    /**
    * Implicit class to create the event areas
    * @param ev the event to define
    */
  implicit class implicitsEvent(ev: CoProcessorEvent){
    def event[T <: Area](area : T) : T = {area.setCompositeNameRv(getName(), ev);area}
  }
}
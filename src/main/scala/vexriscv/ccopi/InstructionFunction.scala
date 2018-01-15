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

trait FunctionDef extends Nameable {
  val pattern : String // Required
  val name : String // default => [unnamed]
  val description : String // default => [no description]

  override def getName() = name
}

abstract class InstructionFunction[+A <: Bundle, +B <: Bundle](dtCmd : A, dtRsp : B) extends FunctionDef {
  val io = new Bundle {
    val cmd = slave Stream (dtCmd.asInstanceOf[Bundle])
    val rsp = master Stream (dtRsp.asInstanceOf[Bundle])
  }
  io.cmd.setWeakName("cmd")
  io.rsp.setWeakName("rsp")

  val cmdPayloadReg = Reg(Bits(io.cmd.payload.getBitsWidth bits))
  val rspPayloadReg = Reg(Bits(io.rsp.payload.getBitsWidth bits))
  val flush = RegInit(False)
  val waitRsp = RegInit(False)
  val done = RegInit(True)
  io.cmd.ready := False
  io.rsp.valid := waitRsp

  when(io.rsp.ready){
    waitRsp := False
  }

  when(done) {
    when(!waitRsp || io.rsp.ready) { // New command
      done := !io.cmd.valid
      io.cmd.ready := True
      io.rsp.payload.assignFromBits(B(io.rsp.payload.getBitsWidth bits, default -> false))
      cmdPayloadReg := io.cmd.payload.asBits
    }
  }.otherwise{
    io.rsp.payload.assignFromBits(rspPayloadReg.asBits)

    when(flush){
      done := True
      waitRsp := True
      io.cmd.ready := True
    }
  }


  def build(controller : EventController) : Unit

    /**
    * Implicit class to create the event areas
    * @param ev the event to define
    */
  implicit class implicitsEvent(ev: CoProcessorEvent){
    def event[T <: Area](area : T) : T = {area.setCompositeName(ev,getName()).reflectNames();area}
  }
}
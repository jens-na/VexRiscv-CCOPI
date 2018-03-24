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
import spinal.lib.Counter
import vexriscv.ccopi.CustomOpcodes._

/**
  * Created by jens on 28.11.17.
  */
class CoProcessorEx0 extends CoProcessor {

  case class Ex0Cmd() extends InputBundle {
    val opcode = Bits(7 bits)
    val rd = Bits(5 bits)
    val tags = Bits(3 bits)
    val rs1 = Bits(5 bits)
    val rs2 = Bits(5 bits)
    val funct = Bits(7 bits)
  }

  case class Ex0Rsp() extends OutputBundle {
    val data = Bits(32 bits)
  }

  def aes = new InstructionFunction[Ex0Cmd, Ex0Rsp](new Ex0Cmd(), new Ex0Rsp()) {
    val pattern: String = s"0000000----------000-----${custom0}"
    val name: String = "ex0"
    val description: String = "Example Coprocessor"

    def build(controller: EventController): Unit = {

      val regs = controller.prepare event new Area {
      }

      val exec = controller.exec event new Area {
        response.data := False.asBits(32 bits)
        flush := True
      }

    }
  }

  def setup(): Unit = {
    activate(aes)
  }
}
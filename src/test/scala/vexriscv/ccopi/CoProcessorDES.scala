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

class CoProcessorDES extends CoProcessor {

  case class DESCmd() extends InputBundle {
    val opcode = Bits(7 bits)
    val rd = Bits(5 bits)
    val tags = Bits(3 bits)
    val rs1 = Bits(5 bits)
    val rs2 = Bits(5 bits)
    val funct = Bits(7 bits)
  }

  case class DESRsp() extends OutputBundle {
    val data = Bits(32 bits)
  }

  def des = new InstructionFunction[DESCmd, DESRsp](new DESCmd(), new DESRsp()) {
    val pattern: String = s"-----------------000-----${custom1}"
    val name: String = "des"
    val description: String = "DES implementation"

    def build(controller: EventController): Unit = {

      val exec = controller.exec event new Area {
        val funct = command.funct
        val internalAddr = command.rs1.asUInt.resize(2)
        val defaultResponse = False.asBits(32 bits)
        val counter = Counter(12)

        when(counter.willOverflowIfInc) {
          flush := True
        }

        when(!done) {
          counter.increment()
        }.otherwise {
          counter.clear()
        }

        val desResponse = new DESRsp()
        desResponse.data := False.asBits(32 bits)
        response := desResponse
      }
    }
  }


  def setup(): Unit = {
    activate(des)
  }

}
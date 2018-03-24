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
class CoProcessorEx1 extends CoProcessor {

  case class Cmd() extends InputBundle {
    val opcode = Bits(7 bits)
    val rd = Bits(5 bits)
    val funct3 = Bits(3 bits)
    val rs1 = Bits(5 bits)
    val rs2 = Bits(5 bits)
    val intRomAddr = Bits(5 bits) // Internal RAM Addr
    val funct2 = Bits(2 bits)
  }

  case class Rsp() extends OutputBundle {
    val data = Bits(32 bits)
  }

  def ex1 = new InstructionFunction[Cmd, Rsp](new Cmd(), new Rsp()) {
    val pattern: String = s"00---------------001-----${custom0}"
    val name: String = "ex1"
    val description: String = "Example Coprocessor"

    def build(controller: EventController): Unit = {

      val regs = controller.prepare event new Area {
        def init = List (0x20, 0x40, 0x60)
        val internalRom = Mem(Bits(8 bits), init.map(B(_ , 8 bits)))
      }

      val exec = controller.exec event new Area {
        val counter = Counter(50)

        val romReadAddr = command.intRomAddr
          .asUInt.resize(2)
        val romRead = regs.internalRom
          .readAsync(romReadAddr)
        response.data := (command.cpuRS1.asUInt + command.cpuRS2.asUInt + romRead.asUInt).asBits

        when(counter.willOverflowIfInc) {
          flush := True
        }

        when(!done) {
          counter.increment()
        }.otherwise {
          counter.clear()
        }
      }
    }
  }

  def setup(): Unit = {
    activate(ex1)
  }
}
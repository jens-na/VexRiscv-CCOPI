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

import spinal.core.{B, _}
import vexriscv.ccopi.CustomOpcodes._

/**
  * Created by jens on 28.11.17.
  */
class TestCompUnit extends CoProcessor {

  case class AESCmd() extends Bundle {
    val opcode = Bits(7 bits)
    val rd = Bits(5 bits)
    val tags = Bits(3 bits)
    val rs1 = Bits(5 bits)
    val rs2 = Bits(5 bits)
    val funct = Bits(7 bits)
  }

  case class AESRsp() extends Bundle {
    val data = Bits(32 bits)
  }

  def f0 = new InstructionFunction[AESCmd, AESRsp](new AESCmd(), new AESRsp()) {
    val pattern: String = s"-----------------000-----${custom0}"
    val name: String = "f0"
    val description: String = "f0 description"

    def build(controller: EventController): Unit = {

      val regs = controller.prepare event new Area {
        val ramKey = Mem(Bits(32 bits), 4)
        val ramData = Mem(Bits(32 bits), 4)
        val ramResult = Mem(Bits(32 bits), 4)
      }


      val exec = controller.idle event new Area {
        val tCmd = new AESCmd()
        tCmd.assignFromBits(cmdPayloadReg.asBits)

        val funct = tCmd.funct

        val tRsp = new AESRsp()
        rspPayloadReg := tRsp.asBits

        when(!done) {
          switch(funct) {
            is(B"0000001") {
              tRsp.data := B(32 bits, (31 downto 29) -> B"001", default -> False)
              flush := True
            }
            is(B"0000010") {
              tRsp.data := B(32 bits, (31 downto 29) -> B"010", default -> False)
              flush := True
            }
            is(B"0000011") {
              tRsp.data := B(32 bits, (31 downto 29) -> B"011", default -> False)
            }
            is(B"0000100") {
              tRsp.data := B(32 bits, (31 downto 29) -> B"100", default -> False)
            }
            default {
              tRsp.data := B(32 bits, (31 downto 29) -> B"111", default -> False)
            }
          }
        }.otherwise {
          tRsp.data := B(32 bits, (31 downto 29) -> B"111", default -> False)
        }
      }
    }
  }

  def setup(): Unit = {
    activate(f0)
  }

}
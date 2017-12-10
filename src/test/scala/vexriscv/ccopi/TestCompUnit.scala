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
import vexriscv.ccopi.comm.CustomOpcodes._
import vexriscv.ccopi.comm._

/**
  * Created by jens on 28.11.17.
  */
class TestCompUnit extends Component with CompUnit {

  def f0 = new InstrFunction[CCOPICmd, CCOPIRsp](new CCOPICmd(), new CCOPIRsp()) {
    val pattern = s"0000001----------000-----${custom0}"

    def build(): Unit = {

      val x = prepare event new Area {
        val internalReg = Mem(Bits(32 bits), 4) // internal 128-bits
      }

      incoming event new Area {

      }

    }
  }

  def f1 = new InstrFunction[CCOPICmd, CCOPIRsp](new CCOPICmd(), new CCOPIRsp()) {
    val pattern = s"0000010----------000-----${custom0}"

    def build(): Unit = {
    }
  }

  def setup(): Unit = {
    activate(f0, f1)
  }

  val io = new Bundle {
    val a = out Bool
  }
}
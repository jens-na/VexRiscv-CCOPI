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
import vexriscv.ccopi.CustomOpcodes._

/**
  * Created by jens on 28.11.17.
  */
class TestCompUnit extends ComputationUnit {

  def f0 = new InstructionFunction[Transferable, Transferable](new Transferable(), new Transferable()) {
    val pattern: String = "00001-----"
    val name: String = "f0"
    val description: String = "f0 description"

    def build(controller: EventController): Unit = {

      controller.prepare event new Area {
        val iointernal = new Bundle {
          val a = out Bool
        }
        iointernal.a := io.cmd.valid
      }

    }
  }

  def f1 = new InstructionFunction[Transferable, Transferable](new Transferable(), new Transferable()) {
    val pattern: String = "00010-----"
    val name: String = "f1"
    val description: String = "f1 description"

    def build(controller: EventController): Unit = {

      controller.prepare event new Area {
        val xx = new Bundle {
          val b = in Bool
        }
      }

    }
  }

  def setup(): Unit = {
    activate(f0, f1)
  }

}
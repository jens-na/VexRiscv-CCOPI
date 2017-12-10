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
package vexriscv.ccopi.comm


import scala.collection.mutable.ArrayBuffer
import spinal.core._
import vexriscv.VexRiscv

abstract trait CompUnit {

  val functions = new ArrayBuffer[InstrFunction[CCOPICmd, CCOPIRsp]]

  def setup() : Unit

  def build() : Unit = {
    assert(
      assertion = functions.length != 0,
      message = s"Computation unit '${this.getClass.getSimpleName}' has no activated functions"
    )

    functions.foreach { f =>
      f.build()
    }
  }

  def activate(func: InstrFunction[CCOPICmd, CCOPIRsp]*) : Unit = {
    functions ++= func
  }

//  Component.current.addPrePopTask(() => build())
}

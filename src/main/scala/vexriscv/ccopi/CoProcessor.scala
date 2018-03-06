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

import spinal.core.{Bundle, Data}

import scala.collection.mutable.ArrayBuffer

/**
  * A computation unit which can be managed by the co processor.
  */
trait CoProcessor {

  var eventController : EventController = null
  val functions = new ArrayBuffer[InstructionFunction[InputBundle, OutputBundle]]

  /**
    * Activate a list of `InstructionFunction` to the computation unit.
    * @param funcs
    * @return
    */
  def activate(funcs : InstructionFunction[InputBundle, OutputBundle]*) = {
    functions ++= funcs
  }

  /**
    * The abstract setup function for the computation unit.
    */
  def setup() : Unit

}

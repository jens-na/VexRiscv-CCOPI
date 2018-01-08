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
import spinal.core.Component

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
trait  EventController {
  type T <: EventController

  val compUnits = new ArrayBuffer[ComputationUnit]

  val events = new ArrayBuffer[CoProcessorEvent]
  events ++= List.fill(2)(new CoProcessorEvent())

  // The different events a co-processor can handle
  val prepare :: incoming :: Nil = events.toList


  def build() : Unit = {
    compUnits.foreach(u => u.eventController = this)
    compUnits.foreach(u => u.setup())

    // All functions are registered at this point
    compUnits.foreach(u => u.build())
  }

  Component.current.addPrePopTask(() => build())
}
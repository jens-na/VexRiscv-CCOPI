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

import spinal.core._
import spinal.lib._
import vexriscv.VexRiscv

import scala.collection.mutable.ArrayBuffer

abstract class InstrBaseFunction extends Area {
  val name : String = "[unnamed]"
  val description : String = "[no description]"
  val pattern : MaskedLiteral

  def build() : Unit

  override def toString(): String = {
    s"[ ${name}, ${description}, ${pattern.toString()}"
  }
}

/**
  * Created by jens on 28.11.17.
  */
abstract class InstrFunction[A <: CCOPICmd, B <: CCOPIRsp](dtCmd : A, dtRsp : B) extends InstrBaseFunction {
  val cmd = slave Stream (dtCmd)
  val rsp = master Stream (dtRsp)
  var events = ArrayBuffer[InstrEvent]()

  events ++= List.fill(2)(new InstrEvent())
  val incoming :: working :: Nil = events.toList

  def build() : Unit

  /**
    * Implicit class to create the areas for each
    * instruction event
    * @param ev the event to define
    */
  implicit class implicitsEvent(ev: InstrEvent){
    def event[T <: Area](area : T) : T = {area.setCompositeName(ev,getName()).reflectNames();area}
  }

  Component.current.addPrePopTask(() => build())
}

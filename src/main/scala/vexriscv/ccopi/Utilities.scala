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

  import vexriscv.Stageable
import spinal.core._

/**
  * The custom RISC-V opcodes as described in
  * the user level ISA v2.2 opcode mapping
  */
object CustomOpcodes {
  val custom0 = "0001011"
  val custom1 = "0101011"
  val custom2 = "1011011"
  val custom3 = "1111011"
}

object Utilities {

  def XLEN = 32

    /**
    * Converts a `String` into a Spinal `MaskedLiteral` object. Can
    * be used to on any String instance.
    *
    * Example:
    * {{{
    * "---010".asMaskedLiteral
    * }}}
    * @param s the string to convert
    */
  implicit class MaskedLiteralConversion(val s : String) {
    def asMaskedLiteral : MaskedLiteral = MaskedLiteral.apply(s)
  }

  implicit class CompositeNameReverse(val a : Area) {
    def setCompositeNameRv(prefix : String, nameable: Nameable) : this.type = {
      a.setWeakName(s"${prefix}_event_${nameable.getDisplayName()}").reflectNames()
      this
    }
  }
}

class CoCpuData[T <: Data](val dataType : T) extends HardType[T](dataType) with Nameable{

}

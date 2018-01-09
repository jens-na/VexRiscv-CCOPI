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
import spinal.lib._
import vexriscv.plugin._
import vexriscv._
import Utilities._
import scala.collection.mutable.ArrayBuffer

/**
  * The co processor plugin for VexRiscv. Registers the computation units and
  * creates a `Stageable` for each function.
  *
  * @param compUnits
  */
class CoProcessorPlugin(compUnits : Seq[ComputationUnit]) extends Plugin[VexRiscv] {

  var cocpu : CoProcessor = null

  // Collect functions to create
  var functions : ArrayBuffer[InstructionFunction[Transferable, Transferable]] = null

  // Create singelton stageables
  var stageables : ArrayBuffer[(InstructionFunction[Transferable, Transferable], Stageable[Bool])] = null


  override def setup(pipeline: VexRiscv): Unit = {
    import pipeline.config._

    cocpu = new CoProcessor(compUnits)

    functions = cocpu.compUnits.map(u => u.functions.toList).flatten

    stageables = functions.map { f =>
      object stageable extends Stageable(Bool)
      stageable.setWeakName(f.name)
      f -> stageable
    }
    //val functions = compUnits.map(u => u.functions.toList).flatten

    // Register at decoder service
    val decoder = pipeline.service(classOf[DecoderService])
    for((func, stageable) <- stageables) {
      decoder.addDefault(stageable, False)
      decoder.add(
        key = func.pattern.asMaskedLiteral,
        List(
          stageable -> True,
          REGFILE_WRITE_VALID -> True,
          BYPASSABLE_EXECUTE_STAGE -> False,
          BYPASSABLE_MEMORY_STAGE -> False,
          RS1_USE -> True,
          RS2_USE -> True
        )
      )
    }
  }

  def build(pipeline: VexRiscv): Unit = {
    import pipeline._
    import pipeline.config._

    execute plug new Area {
      import execute._

      //functions(0).io.cmd.valid := False

      for((func, stageable) <- stageables) {
       func.io.cmd.valid := True
      }
    }

    memory plug new Area {
      import memory._

    }
  }
}
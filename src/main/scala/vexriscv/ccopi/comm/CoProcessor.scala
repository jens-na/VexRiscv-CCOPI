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
import vexriscv.{DecoderService, Stage, Stageable, VexRiscv}
import vexriscv.plugin.Plugin
import vexriscv.ccopi.comm._
import vexriscv.ccopi.comm.MaskedLiteralUtils._

/**
  * The VexRiscv co-processor plugin for a list of computation
  * units.
  *
  * @param compUnits the computation units
  */
class CoProcessor(compUnits : Seq[CompUnit]) extends Plugin[VexRiscv] {

  // Collect functions to create
  val functions = compUnits.map(u => u.functions.toList).flatten

  // Create singelton stageables
  val stageables = functions.map { f =>
    object stageable extends Stageable(Bool)
    f -> stageable
  }

  override def setup(pipeline: VexRiscv): Unit = {
    import pipeline.config._

    compUnits.foreach(u => u.setup())
    checkOverlappingPattern(functions)

    // Register at decoder service
    val decoder = pipeline.service(classOf[DecoderService])
    for((f, s) <- stageables) {
      decoder.addDefault(s, False)
      decoder.add(
        key = f.pattern.asMaskedLiteral,
        List(
          s -> True,
          REGFILE_WRITE_VALID -> True,
          BYPASSABLE_EXECUTE_STAGE -> False,
          BYPASSABLE_MEMORY_STAGE -> False,
          RS1_USE -> True,
          RS2_USE -> True
        )
      )
    }
  }

  override def build(pipeline: VexRiscv): Unit = {
    import pipeline._
    import pipeline.config._

    execute plug new Area {
      import execute._

      for( (func, stageable) <- stageables) {

        // When pattern matches, fire gets True
        val fire = arbitration.isValid && input(stageable)

        func.io.cmd.valid := False
        func.io.cmd.payload.assignFromBits(execute.input(INSTRUCTION))

        when(fire) {
          func.io.cmd.valid := !arbitration.isStuckByOthers && !arbitration.removeIt
          arbitration.haltItself := memory.arbitration.isValid && memory.input(stageable)
        }
      }
    }

    memory plug new Area {
      import memory._

      for( (func, stageable) <- stageables) {
        func.io.rsp.ready := !arbitration.isStuckByOthers

        when(arbitration.isValid && input(stageable)) {
          arbitration.haltItself := !func.io.rsp.valid

          input(REGFILE_WRITE_DATA) := func.io.rsp.payload.asBits
        }
      }
    }
  }

  /**
    *
    * @param functions
    */
  private def checkOverlappingPattern(functions : Seq[InstrBaseFunction]) : Unit = {

  }

}

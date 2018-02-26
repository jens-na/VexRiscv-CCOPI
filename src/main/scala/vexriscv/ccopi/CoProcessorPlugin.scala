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
import org.apache.commons.io.filefilter.FalseFileFilter

import scala.collection.mutable.ArrayBuffer

/**
  * The co processor plugin for VexRiscv. Registers the computation units and
  * creates a `Stageable` for each function.
  *
  * @param c
  */
class CoProcessorPlugin(c : CoProcessor) extends Plugin[VexRiscv] {

  var cocpu : CoProcessorComp = null
  var functions : ArrayBuffer[InstructionFunction[InputBundle, OutputBundle]] = null
  var stageables : ArrayBuffer[(InstructionFunction[InputBundle, OutputBundle], Stageable[Bool])] = null

  override def setup(pipeline: VexRiscv): Unit = {
    import pipeline.config._

    cocpu = new CoProcessorComp(c)

    // Collect functions
    functions = cocpu.coprocessor.functions

    stageables = functions.map { f =>
      // Create a singleton stageable and set the name to the provided
      // function name
      object stageable extends Stageable(Bool) {
        override def setWeakName(name: String): this.type = super.setPartialName(cocpu, f.name)
      }
      f -> stageable
    }

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

    val csr = pipeline.service(classOf[CsrPlugin])
    //csr.externalInterrupt := False
    /*for((func, stageable) <- stageables) {

      if(func.response.isInstanceOf[InterruptBundle]) {
        val extInterrupt = RegInit(False)
        extInterrupt := func.io.interrupt
        csr.externalInterrupt := extInterrupt
      }
    }*/
  }

  def build(pipeline: VexRiscv): Unit = {
    import pipeline._
    import pipeline.config._

    execute plug new Area {
      import execute._

      for((func, stageable) <- stageables) {
        func.io.cmd.valid := False

        // Transfer RS1 and RS2
        func.io.cmd.cpuRS1 := input(RS1)
        func.io.cmd.cpuRS2 := input(RS2)

        // Transfer instruction data
        val hi = func.io.cmd.payload.getBitsWidth
        val lo = (func.io.cmd.payload.getBitsWidth - 32)
        func.io.cmd.payload.assignFromBits(input(INSTRUCTION), hi, lo)

        SpinalInfo(s"Co-Processor function ${func.name} -> cmd payload width ${func.io.cmd.payload.getBitsWidth} Bits")
        SpinalInfo(s"Co-Processor function ${func.name} <- rsp payload width ${func.io.rsp.payload.getBitsWidth} Bits")

        when(arbitration.isValid && input(stageable)) {
          func.io.cmd.valid := !arbitration.isStuckByOthers && !arbitration.removeIt
          arbitration.haltItself := memory.arbitration.isValid && memory.input(stageable)
        }
      }
    }

    memory plug new Area {
      import memory._

      for((func, stageable) <- stageables) {

        //func.io.flush := memory.arbitration.removeIt
        func.io.rsp.ready := !arbitration.isStuckByOthers

        when(arbitration.isValid && input(stageable)) {

          // Only stall the pipeline when there is a response to wait for
          if(!func.response.isInstanceOf[InterruptBundle]) {
            arbitration.haltItself := !func.io.rsp.valid
            input(REGFILE_WRITE_DATA) := func.io.rsp.payload.asBits
          }
        }
      }

    }
  }
}
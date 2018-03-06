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

import spinal.core.SpinalVerilog
import spinal.core._
import spinal.lib._
import vexriscv._
import vexriscv.plugin._
import vexriscv.ccopi._

import scala.collection.mutable.ArrayBuffer

case class CoprocessorConfig(cpuPlugins : ArrayBuffer[Plugin[VexRiscv]],
                             cocpuPlugin : Plugin[VexRiscv]) {

}

object CoprocessorConfig {
  def default = CoprocessorConfig(
    cpuPlugins = ArrayBuffer( // Define the processor
      new PcManagerSimplePlugin(
        resetVector = 0x00000000l,
        relaxedPcCalculation = false
      ),
      new IBusSimplePlugin(
        interfaceKeepData = false,
        catchAccessFault = false
      ),
      new DBusSimplePlugin(
        catchAddressMisaligned = false,
        catchAccessFault = false
      ),
      new CsrPlugin(CsrPluginConfig.smallest),
      new DecoderSimplePlugin(
        catchIllegalInstruction = false
      ),
      new RegFilePlugin(
        regFileReadyKind = plugin.SYNC,
        zeroBoot = true
      ),
      new IntAluPlugin,
      new SrcPlugin(
        separatedAddSub = false,
        executeInsertion = false
      ),
      new LightShifterPlugin,
      new HazardSimplePlugin(
        bypassExecute           = false,
        bypassMemory            = false,
        bypassWriteBack         = false,
        bypassWriteBackBuffer   = false,
        pessimisticUseSrc       = false,
        pessimisticWriteRegFile = false,
        pessimisticAddressMatch = false
      ),
      new BranchPlugin(
        earlyBranch = false,
        catchAddressMisaligned = false,
        prediction = NONE
      ),
      new YamlPlugin("cpu0.yaml")
    ),
    cocpuPlugin = new CoProcessorPlugin(new TestCompUnit())) // Define the Coprocessor
}

case class VexRiscvWithCocpu(config: CoprocessorConfig) {
  import config._

  def cpu = new VexRiscv(
    config = VexRiscvConfig(
      plugins = cpuPlugins += cocpuPlugin
    )
  )
}

object GenVexRiscvWithCocpu {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(VexRiscvWithCocpu(CoprocessorConfig.default).cpu)
  }
}
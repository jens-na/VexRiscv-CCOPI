package vexriscv.ccopi

import spinal.core.SpinalVerilog
import vexriscv.plugin._
import vexriscv.{VexRiscv, VexRiscvConfig, _}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by jens on 27.03.18.
  */
case class CoprocessorConfig(cpuPlugins : ArrayBuffer[Plugin[VexRiscv]],
                             cocpuPlugins : ArrayBuffer[Plugin[VexRiscv]]) {

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

    cocpuPlugins = ArrayBuffer(
      new CoProcessorPlugin(new CoProcessorEx1())
    )
  )
}

case class VexRiscvWithCCOPI(config: CoprocessorConfig) {
  import config._

  def cpu = new VexRiscv(
    config = VexRiscvConfig(
      plugins = cpuPlugins ++ cocpuPlugins
    )
  )
}

object GenVexRiscvWithCCOPI {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(VexRiscvWithCCOPI(CoprocessorConfig.default).cpu)
  }
}
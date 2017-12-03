package vexriscv.ccopi.comm

import spinal.core._
import vexriscv.{DecoderService, Stage, Stageable, VexRiscv}
import vexriscv.plugin.Plugin

/**
  * The VexRiscv co-processor plugin for a list of computation
  * units.
  *
  * @param compUnits the computation units
  */
class CoProcessor(compUnits : Seq[CompUnit]) extends Plugin[VexRiscv] {

  override def setup(pipeline: VexRiscv): Unit = {
    import pipeline.config._

    compUnits.foreach(u => u.setup())
    val functions = compUnits.map(u => u.functions.toList).flatten

    checkOverlappingPattern(functions)

    // Create singelton stageables
    val stageables = functions.map { f =>
      object stageable extends Stageable(Bool)
      f -> stageable
    }

    // Register at decoder service
    val decoder = pipeline.service(classOf[DecoderService])
    for( (f, s) <- stageables) {
      decoder.addDefault(s, False)
      decoder.add(
        key = f.pattern,
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

    compUnits.foreach { u =>
      u.build()
    }

    // Interconnect with vexrisv
  }

  /**
    *
    * @param functions
    */
  private def checkOverlappingPattern(functions : Seq[InstrBaseFunction]) : Unit = {

  }
}

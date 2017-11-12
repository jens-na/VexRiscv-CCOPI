package vexriscv.accel

import spinal.core._

/**
  *
  */
class VexRiscvCCOPIHandshake {

  class CCOPICmd extends Bundle {
      val funct = Bits(7 bits)
      val opcode = Bits(7 bits)
  }

  class CCOPICmdRType extends CCOPICmd {
    val rs2 = Bits(5 bits)
    val rs1 = Bits(5 bits)
    val tags = Bits(3 bits)
    val rd = Bits(5 bits)
  }

  class CCOPICmdIType extends CCOPICmd {

  }
}

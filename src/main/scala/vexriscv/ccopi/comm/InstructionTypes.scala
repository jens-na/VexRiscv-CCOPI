package vexriscv.ccopi.comm

import spinal.core._

/**
  * The Base class for a CCOPI command.
  */
class CCOPICmd extends Bundle with Data

/**
  * The Base class for a CCOPI response.
  */
class CCOPIRsp extends Bundle with Data

class CCOPICmdRType extends CCOPICmd {
  val funct = Bits(7 bits)
  val rs2 = Bits(5 bits)
  val rs1 = Bits(5 bits)
  val tags = Bits(3 bits)
  val rd = Bits(5 bits)
  val opcode = Bits(7 bits)
}


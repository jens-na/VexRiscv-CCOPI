package vexriscv.ccopi

import spinal.core._
import vexriscv.ccopi.comm._
import vexriscv.ccopi.utils.CCOPIRsp

/**
  * Created by jens on 28.11.17.
  */
class TestCompUnit extends Component with CompUnit {

  def f0 = new InstrFunction[CCOPICmd, CCOPIRsp](new CCOPICmd(), new CCOPIRsp()) {
    val pattern = M"----00000"
    override val name = "f0"

    def build(): Unit = {
    }
  }

  def f1 = new InstrFunction[CCOPICmd, CCOPIRsp](new CCOPICmd(), new CCOPIRsp()) {
    val pattern = M"----00000"
    override val name = "f1"

    def build(): Unit = {
    }
  }

  def setup(): Unit = {
    activate(f0, f1)
  }
}
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

import spinal.core.{B, _}
import spinal.lib.Counter
import vexriscv.ccopi.CustomOpcodes._
import spinalcrypto.symmetric._
import spinalcrypto.symmetric.aes._

/**
  * Created by jens on 28.11.17.
  */
class CoProcessorAES extends CoProcessor {

  /**
    * An AES command which is used for load/store
    * operations or to trigger the AES calculation
    */
  case class AESCmd() extends InputBundle {
    val opcode = Bits(7 bits)
    val rd = Bits(5 bits)
    val tags = Bits(3 bits)
    val rs1 = Bits(5 bits)
    val rs2 = Bits(5 bits)
    val funct = Bits(7 bits)
  }

  /**
    * A basic AES response of 32 bits
    */
  case class AESRsp() extends OutputBundle {
    val data = Bits(32 bits)
  }

  def aes = new InstructionFunction[AESCmd, AESRsp](new AESCmd(), new AESRsp()) {
    val pattern: String = s"-----------------000-----${custom0}"
    val name: String = "aes"
    val description: String = "AES implementation"

    def build(controller: EventController): Unit = {

      val regs = controller.prepare event new Area {
          val ramKey = Mem(Bits(32 bits), 4)
          val ramData = Mem(Bits(32 bits), 4)
          val ramResult = Mem(Bits(32 bits), 4)
      }

      val exec = controller.exec event new Area {
        val funct = command.funct
        val flushAesResp = RegInit(False)
        val aesKey = Reg(Bits(128 bits))
        val aesData = Reg(Bits(128 bits))
        val internalAddr = command.rd.asUInt.resize(2)
        val internalWriteData = command.cpuRS1
        val aesResponse = new AESRsp()
        val counter = Counter(20)
        val aesCalc = RegInit(False)

        // #define aes_store_key(_rdi, _rs1 ) \
        //     r_type_insn(0b0000001, 0b000, _rs1, 0b000, _rdi, 0b0001011)
        val functAesStoreKey = (funct === B"0000001")

        // #define aes_store_data(_rdi, _rs1 ) \
        //     r_type_insn(0b0000010, 0b000, _rs1, 0b001, _rdi, 0b0001011)
        val functAesStoreData = (funct === B"0000010")

        // #define aes_load_res(_rd, _rs1 ) \
        //     r_type_insn(0b0000011, 0b000, _rs1, 0b000, _rd, 0b0001011)
        val functAesLoadRes = (funct === B"0000011")

        // #define aes_enc \
        //     r_type_insn(0b0000100, 0b00000, 0b00000, 0b000, 0b00000, 0b0001011)
        val functAesEnc = (funct === B"0000001")


        aesResponse.data := False.asBits(32 bits)
        response := aesResponse

        when(!done) {
          when(functAesStoreKey) {
            regs.ramKey.write(internalAddr, internalWriteData)
            flush := True
          }

          when(functAesStoreData) {
            regs.ramData.write(internalAddr, internalWriteData)
            flush := True
          }

          when(functAesLoadRes) {
            response.data := regs.ramResult.readAsync(internalAddr)
            flush := True
          }

          when(functAesEnc) {
            counter.clear()
            aesCalc := True
          }

          when(aesCalc) {
            counter.increment()
          }

          when(counter.willOverflowIfInc) {
            regs.ramResult.write(0, True.asBits)
            regs.ramResult.write(1, True.asBits)
            regs.ramResult.write(2, True.asBits)
            regs.ramResult.write(3, True.asBits)
            aesCalc := False
            flush := True
          }
        }
      }
    }
  }

  def setup(): Unit = {
    activate(aes)
  }
}
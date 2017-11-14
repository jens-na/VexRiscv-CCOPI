package vexriscv.ccopi.comm

import spinal.core._
import spinal.lib._
import scala.reflect._


abstract class BaseFunction extends Component

abstract class FireAndForgetFunction[A <: CCOPICmd](implicit ca: ClassTag[A]) extends BaseFunction {
  val cmd = slave Stream (ca.runtimeClass.asInstanceOf[A])

  /**
    * Abstract function definition. The implementation of this function
    * is called in each clock cycle where no instruction is executed.
    */
  def idle()

  /**
    * Abstract function definition. The implementation is called when this
    * class is responsible for the instruction which should be executed.
    *
    * @param cmd the command
    */
  def newRequest(cmd: CCOPICmd)


  def recvCmd(): A = {
    cmd.payload
  }

  private def idleInternal() = {

  }

}

abstract class RequestAndResponseFunction[A <: CCOPICmd, B <: CCOPIRsp](implicit ca: ClassTag[A], implicit val cb: ClassTag[B]) extends FireAndForgetFunction[A] {
  val rsp = master Stream (cb.runtimeClass.asInstanceOf[B])

  /**
    * Not used in Request and Response communication
    */
  def newRequest(cmd: CCOPICmd) = { }

  /**
    * Abstract function definition. The implementation is called when this
    * class is responsible for the instruction which should be executed.
    *
    * @param cmd the command
    * @param rsp the response
    */
  def newRequest(cmd: CCOPICmd, rsp : CCOPIRsp)


  def sendRsp(response: B): Unit = {

  }
}
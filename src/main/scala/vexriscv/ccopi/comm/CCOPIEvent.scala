package vexriscv.ccopi.comm

import spinal.core.Area

import scala.collection.mutable.ArrayBuffer

trait CCOPIEventHandler {
  var events = ArrayBuffer[CCOPIEvent]()
}

class CCOPIEvent extends Area {

}
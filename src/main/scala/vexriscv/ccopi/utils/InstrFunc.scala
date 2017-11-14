package vexriscv.ccopi.utils

import spinal.core.MaskedLiteral

import scala.annotation.StaticAnnotation

import vexriscv.ccopi.comm._

/**
  * The annotation which describes a function of an
  * instruction to execute. The function is bound to a fixed
  * pattern.
  *
  * InstrFunc can be bound to a [[spinal.core.Component]] or an
  * [[spinal.core.Area]].
  *
  * @param name The name of the function
  * @param description The description of the function
  * @param pattern The instruction pattern to  which the area is tied
  */
case class InstrFunc(name : String,
                     description : String,
                     pattern : MaskedLiteral) extends StaticAnnotation



lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.spinalhdl",
      scalaVersion := "2.11.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "VexRiscv-CCOPI",
    version := "1.0"
  ).dependsOn(vexRiscv)

lazy val vexRiscv = RootProject(uri("git://github.com/SpinalHDL/VexRiscv.git"))
//lazy val vexRiscv = RootProject(uri("git://github.com/SpinalHDL/VexRiscv.git#commitHash"))

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
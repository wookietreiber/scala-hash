package scalax.hash

import sbt._
import Keys._

package object build {
  val commonSettings = Seq (
    organization := "com.github.wookietreiber",
    scalaVersion := "2.10.2",
    sourceDirectory <<= baseDirectory(identity),
    initialCommands in (Compile, consoleQuick) <<= initialCommands in Compile,
    initialCommands in Compile in console += """
      import scalax.hash._
    """
  )
}

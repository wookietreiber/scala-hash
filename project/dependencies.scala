package scalax.hash
package build

import sbt._

object Dependencies {
  val scalacheck = "org.scalacheck" %% "scalacheck"  % "1.10.1"
  val scalaz     = "org.scalaz"     %% "scalaz-core" % "7.0.3"
  val specs2     = "org.specs2"     %% "specs2"      % "2.1.1"
  val spire      = "org.spire-math" %% "spire"       % "0.6.0"
}

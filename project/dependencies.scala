package scalax.hash
package build

import sbt._

object Versions {
  val scalaz = "7.0.6"
  val spire  = "0.7.4"
}

object Dependencies {
  val scalameter = "com.github.axel22" %% "scalameter" % "0.5-M2"

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.11.4"

  val specs2 = "org.specs2" %% "specs2-scalacheck" % "2.3.12"

  val scodecBits = "org.typelevel" %% "scodec-bits" % "1.0.1"
  val scodecCore = "org.typelevel" %% "scodec-core" % "1.0.0"

  val scalaz    = "org.scalaz" %% "scalaz-core"               % Versions.scalaz
  val scalazscb = "org.scalaz" %% "scalaz-scalacheck-binding" % Versions.scalaz

  val stream = "org.scalaz.stream" %% "scalaz-stream" % "0.4.1"

  val spire    = "org.spire-math" %% "spire"                    % Versions.spire
  val spirescb = "org.spire-math" %% "spire-scalacheck-binding" % Versions.spire
}

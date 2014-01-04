package scalax.hash
package build

import sbt._

object Versions {
  val spire = "0.7.3"
}

object Dependencies {
  val scalameter = "com.github.axel22" %% "scalameter"        % "0.4"
  val scalaz     = "org.scalaz"        %% "scalaz-core"       % "7.0.5"
  val specs2     = "org.specs2"        %% "specs2-scalacheck" % "2.3.7"

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.10+"

  val spire    = "org.spire-math" %% "spire"                    % Versions.spire
  val spirescb = "org.spire-math" %% "spire-scalacheck-binding" % Versions.spire
}

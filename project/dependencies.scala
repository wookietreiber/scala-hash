package scalax.hash
package build

import sbt._

object Versions {
  val scalaz = "7.0.6"
  val spire  = "0.7.3"
}

object Dependencies {
  val scalameter = "com.github.axel22" %% "scalameter"        % "0.4"
  val specs2     = "org.specs2"        %% "specs2-scalacheck" % "2.3.10"

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.10.1"

  val scalaz    = "org.scalaz" %% "scalaz-core"               % Versions.scalaz
  val scalazscb = "org.scalaz" %% "scalaz-scalacheck-binding" % Versions.scalaz

  val spire    = "org.spire-math" %% "spire"                    % Versions.spire
  val spirescb = "org.spire-math" %% "spire-scalacheck-binding" % Versions.spire
}

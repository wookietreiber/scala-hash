import scalax.hash.build._
import Dependencies._

import UnidocKeys._

lazy val root = (
  HashProject("scala-hash", ".")
  settings(sbtunidoc.Plugin.unidocSettings: _*)
  settings (
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(benchmarks),
    scalacOptions in (Compile, doc) ++=
      Seq("-sourcepath", baseDirectory.value.getAbsolutePath, "-doc-source-url",
        "https://github.com/wookietreiber/scala-hash/tree/master€{FILE_PATH}.scala")
  )
  aggregate(hash, scalacheckBinding, scalazContrib, spireContrib, tests, benchmarks)
)

lazy val hash = (
  HashProject("scala-hash-core", "core")
  settings (
    libraryDependencies += scodec
  )
)

lazy val scalacheckBinding = (
  HashProject("scala-hash-scalacheck-binding", "scalacheck")
  dependsOn(hash)
  settings (
    libraryDependencies += scalacheck % Provided
  )
)

lazy val scalazContrib = (
  HashProject("scala-hash-scalaz-contrib", "contrib/scalaz")
  dependsOn(hash, scalacheckBinding % "test")
  settings (
    libraryDependencies ++= Seq(scalaz, scalazscb % "test")
  )
)

lazy val spireContrib = (
  HashProject("scala-hash-spire-contrib", "contrib/spire")
  dependsOn(hash, scalacheckBinding % "test")
  settings (
    libraryDependencies ++= Seq(spire, spirescb % "test")
  )
)

lazy val benchmarks = (
  HashProject("scala-hash-benchmarks", "benchmarks")
  dependsOn(hash, scalazContrib, spireContrib)
  settings (
    libraryDependencies += scalameter,
    logBuffered := false,
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
  )
)

lazy val tests = (
  HashProject("scala-hash-tests", "tests")
  dependsOn(hash, scalazContrib, spireContrib)
  settings (
    libraryDependencies ++= Seq(specs2 % "test")
  )
)

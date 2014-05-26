import scalax.hash.build._
import Dependencies._

import UnidocKeys._

lazy val root = (
  HashProject("scala-hash", ".")
  settings(sbtunidoc.Plugin.unidocSettings: _*)
  settings (
    autoAPIMappings in (ScalaUnidoc, unidoc) := true,
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(benchmarks),
    scalacOptions in (Compile, doc) ++=
      Seq("-sourcepath", baseDirectory.value.getAbsolutePath, "-doc-source-url",
        "https://github.com/wookietreiber/scala-hash/tree/master€{FILE_PATH}.scala")
  )
  aggregate(core, comb, streams, scalazContrib, tests, benchmarks)
)

lazy val core = (
  HashProject("scala-hash-core", "core")
  configs(IntegrationTest)
  settings(Defaults.itSettings: _*)
  settings (
    libraryDependencies += scodecBits,
    libraryDependencies ++= Seq(specs2, scodecCore).map(_ % "test"),
    libraryDependencies += scalameter % "it",
    logBuffered in IntegrationTest := false,
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
  )
)

lazy val comb = (
  HashProject("scala-hash-combination", "comb")
  dependsOn(core)
)

lazy val streams = (
  HashProject("scala-hash-stream", "stream")
  dependsOn(core, scalazContrib)
  settings (
    libraryDependencies += stream
  )
)

lazy val scalazContrib = (
  HashProject("scala-hash-scalaz-contrib", "contrib/scalaz")
  dependsOn(comb)
  configs(IntegrationTest)
  settings(Defaults.itSettings: _*)
  settings (
    libraryDependencies += scalaz,
    libraryDependencies += scalazscb % "it"
  )
)

lazy val benchmarks = (
  HashProject("scala-hash-benchmarks", "benchmarks")
  dependsOn(core, comb, scalazContrib)
  settings (
    libraryDependencies += scalameter,
    logBuffered := false,
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
  )
)

lazy val tests = (
  HashProject("scala-hash-tests", "tests")
  dependsOn(core, scalazContrib)
  settings (
    libraryDependencies ++= Seq(scodecCore % "test", specs2 % "test")
  )
)

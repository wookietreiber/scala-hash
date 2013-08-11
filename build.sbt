import scalax.hash.build._
import Dependencies._

lazy val root = (
  HashProject("scala-hash", ".")
  settings(sbtunidoc.Plugin.unidocSettings: _*)
  settings(
    scalacOptions in (Compile, doc) ++=
      Seq("-sourcepath", baseDirectory.value.getAbsolutePath, "-doc-source-url",
        "https://github.com/wookietreiber/scala-hash/tree/master€{FILE_PATH}.scala")
  )
  aggregate(hash, scalazContrib, spireContrib, tests)
)

lazy val hash = (
  HashProject("scala-hash-core", "core")
)

lazy val scalazContrib = (
  HashProject("scala-hash-scalaz-contrib", "contrib/scalaz")
  dependsOn(hash)
  settings(
    libraryDependencies += scalaz
  )
)

lazy val spireContrib = (
  HashProject("scala-hash-spire-contrib", "contrib/spire")
  dependsOn(hash)
  settings(
    libraryDependencies += spire
  )
)

lazy val tests = (
  HashProject("scala-hash-tests", "tests")
  dependsOn(hash, scalazContrib, spireContrib)
  settings(
    libraryDependencies ++= Seq(scalacheck % "test", specs2 % "test")
  )
)

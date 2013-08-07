import scalax.hash.build._
import Dependencies._

lazy val hash = (
  HashProject("scala-hash", ".")
  settings(
    libraryDependencies ++= Seq(spire, scalacheck % "test", specs2 % "test"),
    scalacOptions in (Compile, doc) ++=
      Seq("-sourcepath", baseDirectory.value.getAbsolutePath, "-doc-source-url",
        "https://github.com/wookietreiber/scala-hash/tree/master€{FILE_PATH}.scala")
  )
)

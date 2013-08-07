package scalax.hash
package build

import sbt._
import Keys._

object HashProject {
  def apply(name: String, path: String): Project = (
    Project(name, file(path))
    settings(commonSettings: _*)
  )
}

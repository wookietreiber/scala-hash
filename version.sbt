val release   = settingKey[Boolean]("whether this is a regular (non-snapshot) release")
val gitCommit = settingKey[String]("current git commit")

release := sys.props("scalax.hash.release") == "true"

gitCommit in ThisBuild := Process("git rev-parse HEAD").lines.head

version in ThisBuild := {
  val v = "0.1.0"
  if (release.value) v else s"$v-${gitCommit.value}"
}

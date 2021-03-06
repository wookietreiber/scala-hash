organization in ThisBuild := "com.github.wookietreiber"

scalaVersion in ThisBuild := "2.11.1"

crossScalaVersions in ThisBuild := Seq("2.11.1", "2.10.4")

autoAPIMappings in ThisBuild := true

resolvers in ThisBuild += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

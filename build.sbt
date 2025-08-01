ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "pekko-course",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % "1.1.5",
      "org.apache.pekko" %% "pekko-slf4j" % "1.1.5",
      "ch.qos.logback" % "logback-classic" % "1.2.11"
    ),
    dependencyOverrides += "org.slf4j" % "slf4j-api" % "1.7.36"
  )

import com.typesafe.sbt.GitVersioning

name := "pokequizz-api"
normalizedName := "PokequizzAPI"
organization := "dev.agnesm"

version := "1.0.0"
scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, BuildInfoPlugin, SbtTwirl, GitVersioning)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, normalizedName, version, scalaVersion, sbtVersion, git.gitHeadCommit)
  )

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

libraryDependencies ++= {
  Seq(
    guice,
    ws,
    "org.clapper" %% "grizzled-slf4j" % "1.3.4",
    "commons-net" % "commons-net" % "3.6",
    "org.typelevel" %% "cats-core" % "2.0.0",
    "io.swagger" %% "swagger-scala-module" % "1.0.6",
    "org.mockito" % "mockito-core" % "3.3.0" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
  )
}
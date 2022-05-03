val Http4sVersion = "0.23.6"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.6"
val MunitCatsEffectVersion = "1.0.6"
val TapirVersion = "1.0.0-M8"
val CirceVersion = "0.14.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.cannondev",
    name := "auth-scala3",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.1.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
      "io.circe" %% "circe-generic" % CirceVersion
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )

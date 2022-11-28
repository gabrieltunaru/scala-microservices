val Http4sVersion = "0.23.6"
val LogbackVersion = "1.2.6"
val TapirVersion = "1.0.0-M8"
val CirceVersion = "0.14.3"
val SkunkVersion = "0.2.3"
val cirisVersion = "2.3.2"
val tsecV = "0.4.0-M1"
val catsEffectV = "3.4.1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.cannondev",
    name := "auth-scala3",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.2.1",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectV,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "org.tpolecat" %% "skunk-core" % SkunkVersion,
      "is.cir" %% "ciris" % cirisVersion,
      "is.cir" %% "ciris-refined" % cirisVersion,
      "org.flywaydb" % "flyway-core" % "7.2.0",
      "org.postgresql" % "postgresql" % "42.2.5",
      "io.github.jmcardon" %% "tsec-password" % tsecV,
      "com.github.jwt-scala" %% "jwt-circe" % "9.0.5"
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
scalacOptions ++= Seq("-new-syntax", "-rewrite")
mainClass in (Compile, run) := Some("com.cannondev.authscala3.Main")

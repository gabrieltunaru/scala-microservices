val Http4sVersion = "0.23.16"
val Http4sBlazeVersion = "0.23.12"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.6"
val MunitCatsEffectVersion = "1.0.6"
val TapirVersion = "1.0.0-M8"
val CirceVersion = "0.14.3"
val SkunkVersion = "0.2.3"
val cirisVersion = "2.3.2"
val tsecV = "0.4.0-M1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.cannondev",
    name := "business",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.2.1",
    semanticdbEnabled := true,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sBlazeVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sBlazeVersion,
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "org.tpolecat" %% "skunk-core" % SkunkVersion,
      "is.cir" %% "ciris" % cirisVersion,
      "is.cir" %% "ciris-refined" % cirisVersion,
      "org.flywaydb" % "flyway-core" % "7.2.0",
      "org.postgresql" % "postgresql" % "42.2.5",
      "io.github.jmcardon" %% "tsec-password" % tsecV,
      "com.github.jwt-scala" %% "jwt-circe" % "9.0.5"
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    scalacOptions += "-Ywarn-unused-import"
  )

Compile / mainClass := Some("com.cannondev.business.Main")
run / mainClass := Some("com.cannondev.business.Main")

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

organization  := "net.node3.demoprocessor"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  val specsV = "3.6.6"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-json" % "1.3.2",
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.specs2" %% "specs2-core" % specsV % "test",
    "org.specs2" %% "specs2-mock" % specsV % "test",
    "com.typesafe.play" %% "anorm" % "2.5.0",
    "com.typesafe.play" %% "play-jdbc" % "2.4.6",
    "com.typesafe.play" %% "play-jdbc-api" % "2.4.6"
  )
}

Revolver.settings

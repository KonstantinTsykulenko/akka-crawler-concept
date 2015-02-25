name := "akka-crawler-concept"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "io.spray" %% "spray-client" % "1.3.2",
  "org.jsoup" % "jsoup" % "1.8.1"
)

    
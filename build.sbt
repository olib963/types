name := "impossible-states"

version := "0.1"

scalaVersion := "2.13.6"

val testDependencies = List(
  "org.scalatest"     %% "scalatest"       % "3.2.9",
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.5.0",
  "org.scalacheck"    %% "scalacheck"      % "1.14.1",
)

libraryDependencies ++= testDependencies.map(_ % Test)

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xlint:infer-any",
  "-Xlint:unused",
  // Fail compilation on warnings
  "-Xfatal-warnings",
)

name := "zio-studies"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= {
  val zioVersion = "2.0.0-M3"
  Seq(
    "dev.zio" %% "zio" % zioVersion,
    "dev.zio" %% "zio-streams" % zioVersion
  )
}

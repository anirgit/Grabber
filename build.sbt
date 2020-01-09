lazy val root = (project in file("."))
  .settings(name := "grabber", version := "1.0", scalaVersion := "2.12.10")

lazy val solution = (project in file("Solution"))
                    .settings(
                      name := "solution",
                      version := "1.0",
                      assemblyJarName in assembly := "grabber-1.0.jar",
                      libraryDependencies ++= commonDependency ++ akkaHttpDependencies ++ dbDependency
                    )

lazy val mockService = (project in file("MockService"))
                          .settings(
                            name := "mockService",
                            version := "1.0",
                            libraryDependencies ++= commonDependency ++ akkaHttpDependencies)

lazy val akkaV            = "2.5.26"
lazy val akkaHttpV        = "10.1.11"

lazy val akkaHttpDependencies = Seq(
  "com.typesafe.akka"            %% "akka-actor"            % akkaV,
  "com.typesafe.akka"            %% "akka-stream"           % akkaV,
  "com.typesafe.akka"            %% "akka-http"             % akkaHttpV,
)

lazy val commonDependency = Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.typelevel" %% "cats-core"  % "2.0.0",
  "org.slf4j" % "slf4j-api" % "1.7.28")

lazy val dbDependency = Seq(
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "org.xerial"          % "sqlite-jdbc"     % "3.7.2",
  "com.typesafe.slick" %% "slick-hikaricp"  % "3.2.2",
  "com.h2database"      % "h2"              % "1.4.197"
)

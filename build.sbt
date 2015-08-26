name := "arallon"

// If the CI supplies a "build.version" environment variable, inject it as the rev part of the version number:
version := s"${sys.props.getOrElse("build.majorMinor", "0.1")}.${sys.props.getOrElse("build.version", "SNAPSHOT")}"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7", "2.10.5")

organization := "com.themillhousegroup"

libraryDependencies  <++= (scalaVersion) { (v) =>
    Seq(
		"joda-time" 									% 	"joda-time" 						% "2.8.1",
		"org.joda" 									  % 	"joda-convert" 					% "1.7" 			  % "compile",
		"org.scala-lang"              %   "scala-reflect"         % v,
    "com.typesafe.scala-logging"  %%  "scala-logging-slf4j"   % "2.1.2"			  % "test",
		"ch.qos.logback"        			%   "logback-classic"       % "1.1.3"				% "test",
    "org.mockito"                 %   "mockito-all"           % "1.9.5"       % "test",
    "org.specs2"                  %%  "specs2"                % "2.3.13"      % "test"
)}

resolvers ++= Seq(  "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
                    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

jacoco.settings

publishArtifact in (Compile, packageDoc) := false

seq(bintraySettings:_*)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalariformSettings

net.virtualvoid.sbt.graph.Plugin.graphSettings


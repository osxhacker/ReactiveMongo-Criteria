import sbt._
import sbt.Keys._


object BuildSettings
{
	val buildVersion = "0.10.0"

	val filter = { (ms: Seq[(File, String)]) =>
	ms filter {
		case (file, path) =>
		path != "logback.xml" && !path.startsWith("toignore") && !path.startsWith("samples")
		}
	}

	val buildSettings = Defaults.defaultSettings ++ Seq(
		organization := "org.reactivemongo",
		version := buildVersion,
		scalaVersion := "2.10.2",
		crossScalaVersions := Seq("2.10.2"),
		crossVersion := CrossVersion.binary,
		javaOptions in test ++= Seq("-Xmx512m", "-XX:MaxPermSize=512m"),
		scalacOptions ++= Seq("-unchecked", "-deprecation"),
		scalacOptions in (Compile, doc) ++= Seq("-unchecked", "-deprecation", "-diagrams", "-implicits"),
		scalacOptions in (Compile, doc) ++= Opts.doc.title("ReactiveMongo Criteria"),
		scalacOptions in (Compile, doc) ++= Opts.doc.version(buildVersion),
		mappings in (Compile, packageBin) ~= filter,
		mappings in (Compile, packageSrc) ~= filter,
		mappings in (Compile, packageDoc) ~= filter) ++ Publish.settings
}

object Publish
{
	def targetRepository: Project.Initialize[Option[sbt.Resolver]] = version {
		(version: String) =>
		val nexus = "https://oss.sonatype.org/"
		if (version.trim.endsWith("SNAPSHOT"))
			Some("snapshots" at nexus + "content/repositories/snapshots")
		else
			Some("releases" at nexus + "service/local/staging/deploy/maven2")
	}

	lazy val settings = Seq(
		publishMavenStyle := true,
		publishTo <<= targetRepository,
		publishArtifact in Test := false,
		pomIncludeRepository := { _ => false },
		licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
	homepage := Some(url("http://github.com/osxhacker/ReactiveMongo-Criteria")),
	pomExtra := (
		<scm>
			<url>git://github.com/osxhacker/ReactiveMongo-Criteria.git</url>
			<connection>scm:git://github.com/osxhacker/ReactiveMongo-Criteria.git</connection>
		</scm>
		<developers>
			<developer>
				<id>osxhacker</id>
				<name>Steve Vickers</name>
				<url>http://www.iteamsolutions.com</url>
			</developer>
		</developers>))
}

object Resolvers
{
	val typesafe = Seq(
		"Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
		"Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/")
	val resolversList = typesafe
}

object Dependencies
{
	val rmongoVersion = "0.10.0"

	val bson = "org.reactivemongo" %% "reactivemongo-bson" % rmongoVersion

	val bsonmacros = "org.reactivemongo" %% "reactivemongo-bson-macros" % rmongoVersion

	val specs = "org.specs2" %% "specs2" % "2.2.1" % "test"
}

object ReactiveMongoCriteriaBuild extends Build
{
	import BuildSettings._
	import Resolvers._
	import Dependencies._

	lazy val dsl = Project(
		"ReactiveMongo-Criteria-DSL",
        file("."),
		settings = buildSettings ++ Seq(
			resolvers := resolversList,
			libraryDependencies <++= (scalaVersion)(sv => Seq(
				specs, bson, bsonmacros)
			)
		)
	)
}


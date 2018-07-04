import sbt._
import sbt.Keys._

import com.typesafe.sbt.osgi.{
	OsgiKeys,
	SbtOsgi
	}

import SbtOsgi.autoImport._


object BuildSettings
{
	val buildVersion = Dependencies.reactiveMongoVersion

	val filter = { (ms: Seq[(File, String)]) =>
	ms filter {
		case (file, path) =>
		path != "logback.xml" && !path.startsWith("toignore") && !path.startsWith("samples")
		}
	}

	val buildSettings = Defaults.defaultSettings ++ Seq(
		organization := "org.reactivemongo",
		version := buildVersion,
		scalaVersion := "2.11.12",
		crossScalaVersions := Seq("2.11.12", "2.12.4"),
		javaOptions in test ++= Seq("-Xmx512m", "-XX:MaxPermSize=512m"),
		scalacOptions ++= Seq("-unchecked", "-deprecation"),
		scalacOptions in (Compile, doc) ++= Seq (
			"-unchecked",
			"-deprecation",
			"-diagrams",
			"-implicits",
			"-feature"
			),
		scalacOptions in (Compile, doc) ++= Opts.doc.title (
			"ReactiveMongo Criteria"
			),
		scalacOptions in (Compile, doc) ++= Opts.doc.version (buildVersion),
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
				<url>https://github.com/osxhacker</url>
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
	val reactiveMongoVersion = "0.12.7"

	val bson = "org.reactivemongo" %% "reactivemongo-bson" % reactiveMongoVersion

	val bsonmacros = "org.reactivemongo" %% "reactivemongo-bson-macros" % reactiveMongoVersion

    val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"
}

object ReactiveMongoCriteriaBuild extends Build
{
	import BuildSettings._
	import Resolvers._
	import Dependencies._

	lazy val dsl = Project(
		"ReactiveMongo-Criteria-DSL",
        file("."),
		settings = buildSettings ++ osgiSettings ++ Seq(
			resolvers := resolversList,
			OsgiKeys.exportPackage := Seq(
				"reactivemongo.extensions.dsl.criteria"
				),
			libraryDependencies <++= (scalaVersion)(sv => Seq(
				scalaTest, bson, bsonmacros,
                "org.scala-lang" % "scala-reflect" % sv
                )
			)
		)
	)
	.enablePlugins (SbtOsgi);
}


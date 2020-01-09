import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.PathList

object AssemblySettings {
  val commonAssemblySettings = Seq(
    assemblyOutputPath in assembly := (target in LocalRootProject).value / "jarFiles" / (assemblyJarName in assembly).value,
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "annotation", list @ _*) =>
        list map (_.toLowerCase) match {
          case "matchespattern$checker.class" :: Nil => MergeStrategy.first
          case "meta" :: "when.class" :: Nil         => MergeStrategy.first
          case "nonnegative$checker.class" :: Nil    => MergeStrategy.first
          case "nonnull$checker.class" :: Nil        => MergeStrategy.first
          case "regex$checker.class" :: Nil          => MergeStrategy.first
          case "syntax.class" :: Nil                 => MergeStrategy.first
          case _                                     => MergeStrategy.deduplicate
        }
      case PathList("META-INF", list @ _*) =>
        list map (_.toLowerCase) match {
          case (x :: Nil) if Seq("manifest.mf", "index.list", "dependencies") contains x =>
            MergeStrategy.discard
          case (x :: Nil) if Seq("license", "license.txt", "notice", "notice.txt") contains x =>
            MergeStrategy.discard
          case "io.netty.versions.properties" :: Nil =>
            MergeStrategy.discard
          case ps @ (x :: xs)
              if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") || ps.last.endsWith(".rsa") =>
            MergeStrategy.discard
          case "maven" :: xs =>
            MergeStrategy.discard
          case "plexus" :: xs =>
            MergeStrategy.discard
          case "services" :: xs =>
            MergeStrategy.filterDistinctLines
          case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) | ("spring.tooling" :: Nil) =>
            MergeStrategy.discard
          case ("javax.jms" :: Nil) =>
            MergeStrategy.discard
          case _ => MergeStrategy.deduplicate
        }
      case x =>
        val defaultStrategy = (assemblyMergeStrategy in assembly).value
        defaultStrategy(x)
    }
  )
}

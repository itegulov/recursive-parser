logLevel := Level.Warn

resolvers ++= Seq(
  Resolver.mavenLocal,
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)


addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")
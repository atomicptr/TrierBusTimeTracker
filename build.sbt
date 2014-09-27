import android.Keys._

android.Plugin.androidBuild

name := "TrierBusTimeTracker"

scalaVersion := "2.11.2"

proguardCache in Android ++= Seq(
    ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-dontwarn scala.collection.mutable.**")

libraryDependencies ++= Seq(
    "org.scaloid" %% "scaloid" % "3.5-10",
    "org.scalaj" %% "scalaj-http" % "0.3.16",
    "org.json4s" %% "json4s-native" % "3.2.10"
)

scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android

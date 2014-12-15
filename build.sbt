import android.Keys._

android.Plugin.androidBuild

name := "TrierBusTimeTracker"

scalaVersion := "2.11.4"

proguardCache in Android ++= Seq(
    ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize",
    "-keepattributes Signature", "-dontwarn scala.collection.**",
    "-dontwarn scala.collection.mutable.**")

libraryDependencies ++= Seq(
    "org.scaloid" %% "scaloid" % "3.6.1-10",
    "org.scalaj" %% "scalaj-http" % "0.3.16",
    "org.json4s" %% "json4s-native" % "3.2.10",
    "com.android.support" % "support-v4" % "21.0.+"
)

scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android

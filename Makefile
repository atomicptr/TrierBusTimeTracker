run:
	sbt android:package
	adb install -r target/android-bin/TrierBusTimeTracker-debug.apk


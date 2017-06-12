#!/bin/bash

# A small bash script which perfoms the entire jank performance test set up and execution.
# Assumptions: adb is available via system PATH


function main() {
    local -r projectRoot=$(git rev-parse --show-toplevel)
	local -r packageName="example.jank.performance.myapplication"
	local -r instrumentArguments="-w -r -e debug false -e class example.jank.performance.myapplication.BasicTest example.jank.performance.myapplication.test/android.support.test.runner.AndroidJUnitRunner"
	
	#preps the device for ui automation test
	./gradlew assembleDebug assembleDebugAndroidTest
	adb install -r ${projectRoot}/app/build/outputs/apk/debug/app-debug.apk
	adb install -r ${projectRoot}/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

	#executes kotlin script which queries gfxinfo while running the automated tests
	kotlinc main.kt -include-runtime -d main.jar
	java -jar main.jar $packageName $instrumentArguments
}

main
#!/bin/bash

# A small bash script which performs the entire jank performance test set up and execution.
#
# Created by Andre Perkins (akperkins1@gmail.com) on 6/6/17.
#
# Assumptions:
# - adb is available via system PATH



function main() {
    # simply obtains the project root directory
    local -r projectRoot=$(git rev-parse --show-toplevel)

	#preps the device for the UI automation test
	${projectRoot}/gradlew -p ${projectRoot} assembleDebug assembleDebugAndroidTest
	adb install -r ${projectRoot}/app/build/outputs/apk/debug/app-debug.apk
	adb install -r ${projectRoot}/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

	# package under test name
	local -r packageName="example.jank.performance.myapplication"

	# command to start the ui automation test
	local -r instrumentArguments="adb shell am instrument -w -r -e debug false -e class example.jank.performance.myapplication.BasicTest example.jank.performance.myapplication.test/android.support.test.runner.AndroidJUnitRunner"

	#executes kotlin script which queries gfxinfo while running the automated tests
	kotlinc main.kt -include-runtime -d main.jar
	java -jar main.jar "$packageName" "$instrumentArguments"
}

main
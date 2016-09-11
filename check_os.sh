#!/bin/bash

if [[ "$TRAVIS_OS_NAME" == "linux" ]]; then jdk_switcher oraclejdk8; fi

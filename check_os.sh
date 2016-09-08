#!/bin/bash

if [ "$TRAVIS_OS_NAME" -eq "linux"]; then
  jdk_switcher use oraclejdk8
fi

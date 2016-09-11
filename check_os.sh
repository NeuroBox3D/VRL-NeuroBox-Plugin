#!/bin/bash

if [[ "$TRAVIS_OS_NAME" == "linux" ]]; then exit 0; fi
exit 1

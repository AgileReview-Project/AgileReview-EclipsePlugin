#!/bin/bash

# This script was taken from https://github.com/ajdm/travis-build-children

# Get last dependent project build number
BUILD_NUM=$(curl -s 'https://api.travis-ci.org/repos/AgileReview-Project/AgileReview-Findbugs/builds' | grep -o '^\[{"id":[0-9]*,' | grep -o '[0-9]' | tr -d '\n')
# Restart last dependent project build
curl -X POST https://api.travis-ci.org/builds/$BUILD_NUM/restart --header "Authorization: token "$CHILD_AUTHTOKEN

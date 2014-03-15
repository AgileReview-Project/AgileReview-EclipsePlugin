#!/bin/bash

RELEASE_VERSION=$1
NEXT_VERSION=$2

if [[ -z $RELEASE_VERSION ]]; then
    echo -e "\e[00;31mRelease version is empty, please specify.\e[00m"
    exit 1
fi

if [[ -z $NEXT_VERSION ]]; then
    echo -e "\e[00;31mNext development version is empty, please specify.\e[00m"
    exit 1
fi

echo "Updating bundle versions to "$RELEASE_VERSION"..."

mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$RELEASE_VERSION

mvn clean verify

if [[ $? -ne 0 ]]; then
    echo -e "\e[00;31mVerification of release version $RELEASE_VERSION failed\e[00m"
    exit 1
fi

git commit -a -m "prepare for release"

git tag v$RELEASE_VERSION

echo "Building the release version..."

mvn clean install

echo "Updating bundle versions to "$NEXT_VERSION"..."

mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$NEXT_VERSION

mvn clean verify

if [[ $? -ne 0 ]]; then
    echo -e "\e[00;31mVerification of next development version $RELEASE_VERSION failed\e[00m"
    exit 1
fi

git commit -a -m "preparing for next developing version"

git --tags push

echo -e "\e[00;32mRelease of version "$RELEASE_VERSION" was successful!\e[00m"

exit 0;

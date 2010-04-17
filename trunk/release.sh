#!/bin/sh

if [ $# != 2 ]; then
	echo "Usage: $0 (REVISION) (TAG NAME)"
    exit 1;
fi

REVISION=$1
TAG_NAME=$2

svn copy -r ${REVISION} https://maven-replacer-plugin.googlecode.com/svn/trunk \
https://maven-replacer-plugin.googlecode.com/svn/tags/${TAG_NAME} -m "Tagging for release"

svn checkout https://maven-replacer-plugin.googlecode.com/svn/tags/${TAG_NAME} -r head release

cd release
mvn clean package install
if [ $? != 0 ]; then
    echo "Build failed, aborting."
    svn delete https://maven-replacer-plugin.googlecode.com/svn/tags/${TAG_NAME} -m "Cleaning up tag from broken build"
    exit 1;
fi

mvn clean source:jar repository:bundle-create

svn mkdir https://maven-replacer-plugin.googlecode.com/svn/release-repo/com/google/code/maven-replacer-plugin/maven-replacer-plugin/${TAG_NAME} \
-m "Preparing release dir"

svn checkout https://maven-replacer-plugin.googlecode.com/svn/release-repo/com/google/code/maven-replacer-plugin/maven-replacer-plugin/${TAG_NAME} repo

cp pom.xml repo/maven-replacer-plugin-${TAG_NAME}.pom
cp target/*.jar repo
cd repo
svn add *
svn ci -m "Release"

echo "Cleaning up"
cd ..
cd ..
rm -fR release

echo "Done."
echo "Make sure you update central maven repo, and the website documentation" 
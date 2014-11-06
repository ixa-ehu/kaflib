#!/bin/sh

rflag=false
while getopts ":v:" opt; do
  case $opt in
      v) rflag=true; version="$OPTARG";
          ;;
      \?) echo "Invalid option -$OPTARG" >&2
          ;;
  esac
done

if ! $rflag; then
    echo "sh release -v version"
    exit
fi

git checkout master
mvn versions:set -DnewVersion=$version
git commit -m \"Bumped version to $version\"
git merge --no-ff develop
git tag -a v$version
git push origin master
git push --tags
git checkout develop
git merge --no-ff master
git push origin develop

echo "Version $version released"

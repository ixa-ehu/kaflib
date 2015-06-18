#!/bin/bash

rflag=false
branch="naf"
while getopts ":v:b:" opt; do
  case $opt in
      v) rflag=true; version="$OPTARG";
          ;;
      b) branch="$OPTARG";
          ;;
      \?) echo "Invalid option -$OPTARG" >&2
          ;;
  esac
done

if ! $rflag || [ $branch != "naf" -a $branch != "kaf" ]; then
    echo "sh release -v version [-b {naf|kaf}]"
    exit
fi

git checkout $branch
mvn versions:set -DnewVersion=$version
git commit -a -m "Bumped version to $version"
git tag -a v$version
git push origin $branch
git push --tags
mvn clean deploy -P release

echo "Version $version released"

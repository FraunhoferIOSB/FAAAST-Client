#!/usr/bin/env bash
#
VERSION=$1
NEXTVERSION=$2
if [[ -z "$3" ]]; then
  NEXTBRANCH=$(sed -n 's/^\s\+<tag>\([^<]\+\)<\/tag>/\1/p' pom.xml)
else
  NEXTBRANCH=$3
fi
TAG_VERSION="version"
TAG_CHANGELOG_HEADER="changelog-header"
TAG_CHANGELOG_ANCHOR="changelog-anchor"
README_FILE="README.md"

# arguments: tag
function startTag()
{
	echo "<!--start:$1-->\\n"
}

# arguments: tag
function endTag()
{
	echo "<!--end:$1-->"
}

# arguments: file, tag, newValue, originalValue(optional, default: matches anything)
function replaceValue()
{
	local file=$1
	local tag=$2
	local newValue=$3
	local originalValue=${4:-.*}
	local startTag=$(startTag "$tag")
	local endTag=$(endTag "$tag")
	sed -r -z "s/$startTag($originalValue)$endTag/$startTag$newValue$endTag/g" -i "$file"
}

# arguments: file, tag
function removeTag()
{
	local file=$1
	local tag=$2
	local startTag=$(startTag "$tag")
	local endTag=$(endTag "$tag")
	sed -r -z "s/$startTag(.*)$endTag/\1/g" -i "$file"
}

# arguments: file, newVersion
function replaceVersion()
{
	local file=$1
	local new_version=$2
	local startTag=$(startTag "$tag")
	local endTag=$(endTag "$tag")
	replaceValue "$file" "$TAG_VERSION" "$new_version"
	sed -r -z 's/(<artifactId>core<\/artifactId>[\r\n]+\s*<version>)[^<]+(<\/version>)/\1'"${new_version}"'\2/g' -i "$file"
	sed -r -z 's/(\x27de.fraunhofer.iosb.ilt.faaast.client:core:)[^\x27]*\x27/\1'"${new_version}"'\x27/g' -i "$file"
}


echo "Releasing:  ${VERSION},
tagged:    v${VERSION},
next:       ${NEXTVERSION}-SNAPSHOT
nextBranch: ${NEXTBRANCH}"
echo "Press enter to go"
read -s

echo "Replacing version numbers"
mvn -B versions:set -DgenerateBackupPoms=false -DnewVersion="${VERSION}"
sed -i 's/<tag>HEAD<\/tag>/<tag>v'"${VERSION}"'<\/tag>/g' pom.xml
replaceVersion "$README_FILE" "$VERSION"
replaceValue "$README_FILE" "$TAG_CHANGELOG_HEADER" "### ${VERSION}"
removeTag "$README_FILE" "$TAG_CHANGELOG_HEADER"

mvn -B spotless:apply

echo "Git add ."
git add .

echo "Next: git commit & Tag [enter]"
read -s
git commit -m "Release v${VERSION}"
git tag -m "Release v${VERSION}" -a v"${VERSION}"

echo "Next: replacing version nubmers [enter]"
read -s
mvn versions:set -DgenerateBackupPoms=false -DnewVersion="${NEXTVERSION}"-SNAPSHOT
sed -i 's/<tag>v'"${VERSION}"'<\/tag>/<tag>'"${NEXTBRANCH}"'<\/tag>/g' pom.xml
sed -i "/^<!--${TAG_CHANGELOG_ANCHOR}-->$/a <!--start:${TAG_CHANGELOG_HEADER}-->\\n<!--end:${TAG_CHANGELOG_HEADER}-->" "$README_FILE"
replaceValue "$README_FILE" "$TAG_CHANGELOG_HEADER" "### ${NEXTVERSION}-SNAPSHOT (current development version)"
mvn -B spotless:apply

echo "Git add ."
git add .

echo "Next: git commit [enter]"
read -s
git commit -m "Prepare for next development iteration"

echo "Done"

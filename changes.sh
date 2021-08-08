#!/bin/bash
#
# This script writes the changelog in a human-readable format.
#
# You'll probably want to edit manually the result of executing the script.
#
if [[ $# -eq 0 ]] ; then
	echo "No version supplied (e.g. '3.6.1')"
	exit 1
fi
OLDTAG=`git tag -l --merged master --sort=-taggerdate|head -1`
echo "Writing changes from tag $OLDTAG"
TITLE="CSS4J Agent Module changes"
VERHDR="Version ${1}"
OUTFILE="CHANGES.txt"
echo -en "${TITLE}\\r\\n${TITLE//?/=}\\r\\n\\r\\n${VERHDR}\\r\\n${VERHDR//?/-}\\r\\n\\r\\n">${OUTFILE}
git log --reverse --pretty=format:%s ${OLDTAG}..|sed -e 's/^/- /'|fold -s|sed -r 's/^([^-])/  \1/'|sed -e 's/$/\r/'>>${OUTFILE}
echo -en "\\n">>${OUTFILE}

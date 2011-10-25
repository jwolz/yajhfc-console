#!/bin/sh
# Extracts all translatable strings into messages.po

SOURCEDIR=../src
OUTNAME=Messages

find $SOURCEDIR -name '*.java' | xgettext -k_ --from-code=utf-8 -f- -o$OUTNAME.po


#!/bin/sh
# Extracts all translatable strings into messages.po

SOURCEDIR=../src
OUTNAME=Messages

xgettext -k_ --from-code=utf-8 `find $SOURCEDIR -name '*.java'` -o$OUTNAME.po


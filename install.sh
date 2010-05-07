#!/bin/sh

mvn clean install
rm -fR target
echo "Installed to local repository"

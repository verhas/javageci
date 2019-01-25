#!/usr/bin/env bash

mkdir -p release/01api
cp javageci-api/target/*jar release/01api
cp javageci-api/pom.xml release/01api

mkdir -p release/02annotation
cp javageci-annotation/target/*jar release/02annotation
cp javageci-annotation/pom.xml release/02annotation

mkdir -p release/03tools
cp javageci-tools/target/*jar release/03tools
cp javageci-tools/pom.xml release/03tools

mkdir -p release/04core
cp javageci-core/target/*jar release/04core
cp javageci-core/pom.xml release/04core


mkdir -p release/05engine
cp javageci-engine/target/*jar release/05engine
cp javageci-engine/pom.xml release/05engine

mkdir -p release/06jamal
cp javageci-jamal/target/*jar release/06jamal
cp javageci-jamal/pom.xml release/06jamal

cd release
for artifact in *
do
    rm ${artifact}/*.sig
    rm ${artifact}/*.asc
    for file in ${artifact}/*.jar ${artifact}/pom.xml
    do
        gpg -s -b ${file}
        mv ${file}.sig ${file}.asc
    done
    cd ${artifact}
    rm *.zip
    jar -c -M -f ${artifact}_release.zip *.jar pom.xml *asc
    cd ..
done
cd ..

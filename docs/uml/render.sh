#!/usr/bin/env sh
# Render toàn bộ sơ đồ PlantUML trong src/ thành PNG ở thư mục này.
# Cần Java và plantuml.jar (https://plantuml.com/download). Không cần Graphviz (dùng layout smetana).
# Cách dùng:  sh docs/uml/render.sh /duong/dan/plantuml.jar
JAR="${1:-plantuml.jar}"
DIR="$(cd "$(dirname "$0")" && pwd)"
java -jar "$JAR" -charset UTF-8 -tpng -o "$DIR" "$DIR"/src/*.puml

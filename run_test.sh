#!/bin/bash
set -eo pipefail
cd "$(dirname "$0")"
mvn test -q -B

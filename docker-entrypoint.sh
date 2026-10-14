#!/bin/bash
set -e

# Support passing tags via CUCUMBER_FILTER_TAGS env var
EXTRA_ARGS=""
if [ -n "$CUCUMBER_FILTER_TAGS" ]; then
    EXTRA_ARGS="-Dcucumber.filter.tags=$CUCUMBER_FILTER_TAGS"
fi

# If arguments were provided to 'docker compose run tests <args>'
if [ $# -gt 0 ]; then
    # If the user passed a maven lifecycle like 'verify' or 'test'
    if [[ "$1" == "verify" || "$1" == "test" || "$1" == "clean" ]]; then
        exec ./mvnw clean "$@" $EXTRA_ARGS
    else
        # If user passed custom flags or properties
        exec ./mvnw clean verify "$@" $EXTRA_ARGS
    fi
else
    # Default execution: run full suite on verify lifecycle
    exec ./mvnw clean verify $EXTRA_ARGS
fi

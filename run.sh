#!/usr/bin/env bash
# Wrapper to allow calling ./run.sh while the script is stored as .run.sh
exec "./.run.sh" "$@"

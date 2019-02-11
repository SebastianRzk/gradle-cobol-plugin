#! /bin/bash
set -e

git clone https://github.com/RosesTheN00b/gradle-cobol-plugin-example

cd gradle-cobol-plugin-example
sh ci/run_dev_test.sh
cd ..

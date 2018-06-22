set -e

echo ">>>>>>> creating test-repository"
mkdir endToEndTest
cd endToEndTest
git clone https://github.com/RosesTheN00b/gradle-cobol-plugin-example
git clone https://github.com/RosesTheN00b/gradle-cobol-plugin-unittest-extension
echo "<<<<<<<"

echo ">>>>>>> install test-repo"
sh gradle-cobol-plugin-example/ci/install_requirements.sh
echo "<<<<<<< install rest repo"

echo ">>>>>>> create local repo"
cd ..
gradle publish
gradle publish -PgenerateLatest
mv ../repo endToEndTest/repo
cd endToEndTest/gradle-cobol-plugin-unittest-extension
gradle publish -PgenerateLatest
cd ..
cd ..
echo "<<<<<<<"

echo ">>>>>>> prepare test-repository"
gradle switchOffline
echo "<<<<<<<"

echo ">>>>>>> exec test"
cd endToEndTest/gradle-cobol-plugin-example
cd ./project-cobol-fixed-format && gradle check && cd ..
cd ./project-cobol-free-format && gradle check && cd ..
cd ./project-cobol-unit-test && gradle check && cd ..
cd ./project-cobol-unit-test && gradle testUnit computeTestCoverage && cd ..
cat ./project-cobol-unit-test/build/CobolUnit/coverage.xml

echo "<<<<<<<"

set -e

echo ">>>>>>> creating test-repository"
mkdir endToEndTest
cd endToEndTest
git clone https://github.com/RosesTheN00b/gradle-cobol-plugin-example
echo "<<<<<<<"

echo ">>>>>>> create local repo"
cd ..
gradle publish
mv ../repo endToEndTest/repo
echo "<<<<<<<"

echo ">>>>>>> prepare test-repository"
rm endToEndTest/gradle-cobol-plugin-example/settings.gradle
cp ci/local_repo_test_settings.gradle endToEndTest/gradle-cobol-plugin-example/settings.gradle
echo "<<<<<<<"

echo ">>>>>>> exec test"
cd endToEndTest/gradle-cobol-plugin-example
gradle cobolCheck
echo "<<<<<<<"

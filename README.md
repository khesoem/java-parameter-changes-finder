# java-parameter-changes-finder
You can use this project to find the commits of a project which add a parameter to a java class.
## How to use it?
To use it, follow these stpes in ubuntu (It is tested on ubuntu 16.04):<br />
1- Make a new directory (let call it new_dir) and cd into it<br />
2- Copy "find-parameter-changes.sh" and "Parameter_Adder_Extractor-1.0-SNAPSHOT-jar-with-dependencies.jar" from this repo into new_dir<br />
3- Choose a github repo and find its remote-url (for example: https://github.com/apache/commons-math.git)<br />
4- Run find-parameter-changes.sh by a sudo user and give it your chosen remote-url and the name of the folder where you want to clone the project into.<br />
Note 1: you should have git installed.<br />
Note 2: you may need to enter your sudo user password when the script is running.<br />
Note 3: you need to change the script file permissions so that you can run it as a script file.<br />
5- You can find the results in "parameter_adder_commits.csv" in new_dir.<br />

The following commands show you an example of using this project:<br />
```
# step 1
mkdir new_dir
cd new_dir
# step 2
cp ../java-parameter-changes-finder/find-parameter-changes.sh ./
cp ../java-parameter-changes-finder/Parameter_Adder_Extractor-1.0-SNAPSHOT-jar-with-dependencies.jar ./
# step 3 (you should do it via your FireFox!)
# step 4
sudo chmod 777 find-parameter-changes.sh
./find-parameter-changes.sh https://github.com/apache/commons-math.git math
# step 5
vim parameter_adder_commits.csv
```
### Some clarifications
1- All the commits that have added EXACTLY one parameter to a method of a class or interface (or nested class or interface) will be reported in parameter_adder_commits.csv.<br />
2- Each line of parameter_adder_commits.csv has this format: "commit_id, java-file-name, old-function-signature, new-functoin-signature".<br />
3- Since function signatures usually contain ',' character, if you open parameter_adder_commits.csv with Microsoft Excel some lines would have more than 4 columns.<br />
4- We say a commit adds EXACTLY one parameter to a method if the old version of a class (say OldC) contains a method (say M1) and the new version of that class (say NewC) contains a metohd (say M2) while OldC does not contain M2, NewC does not contain M1, and the only difference between signatures of M1 and M2 is that M1 does not have the LAST parameter of M2.<br />
5- If the name of a parameter is changed, the signature of the method will not be changed.<br />
6- We assume that the files containing OldC and NewC in two versions (before and after the commit) have the same name and path.<br />
## How to change and compile it?
First, you should clone this project into your hard-disk.<br />
This project contains a bash script and a java jar file.<br />
To change the bash script, you can simply open it by an editor and change it!<br />

To change the jar file, you can change the src of "parameter-adder-extractor" folder or open this folder by IntelliJ and edit files there.

When you are done with the changes you want to make, you should go to the root of "parameter-adder-extractor" folder and run "mvn package". Note that you should have maven installed to be able to run this command. <br />
The new version of jar file will be accessible in "parameter-adder-extractor/target/Parameter_Adder_Extractor-1.0-SNAPSHOT-jar-with-dependencies.jar".<br />
If you want to run jar file, you can simply run the following command.
```
java -jar Parameter_Adder_Extractor-1.0-SNAPSHOT-jar-with-dependencies.jar inputFilePath outputFilePath
```
You should pass inputFilePath and outputFilePath to it. The output file would have the same format as parameter_adder_commits.csv and each line of the input file has the format of "commit_id, file_name, old_file_path, new_file_path". You can see a sample of jar-input-file in "jar-input-sample.txt" in this repo.<br />
For each line of the input-file, this command will compare "old_file_path" and "new_file_path" and find all the methods to which EXACTLY one parameter is added <br />
## Sample results
This project has been tested on two github repos:<br />
https://github.com/apache/commons-math and https://github.com/jfree/jfreechart<br />
The results can be found in "results" folder of this repo.
# $1=git-repo-address, $2=directory to clone project into

# Initializing...
#rm $2 -rf
#git clone $1 $2
git_project_folder=$2
tmp_folder="$2-tmp"
sudo rm $tmp_folder -rf
mkdir $tmp_folder
sudo chmod 777 $tmp_folder -R
tmp_folder=../$tmp_folder
rm -rf to_be_compared_files.txt 2> error.log
touch to_be_compared_files.txt # format: listOf(commit_id, file_name, old_file_path, new_file_path)
cd $git_project_folder

# Checking commits one by one
all_commits_file="$tmp_folder/all_commits.txt"
git rev-list --remotes > $all_commits_file
number_of_commits=$(wc -l < $all_commits_file)
echo "Checking commits one by one"
echo "$number_of_commits commits should be checked"
checked_commits=$(( 0 ))
mkdir $tmp_folder/to_be_compared_changed_files
while read commit_id; do
	mkdir $tmp_folder/to_be_compared_changed_files/$commit_id

	changed_files=$tmp_folder/changed_files.txt
	parent_commits=$tmp_folder/parent_commits.txt
	git diff-tree --no-commit-id --name-only -r $commit_id > $changed_files
	parent_commits_str=$(git log --pretty=%P -n 1 $commit_id)
	IFS=" " read -r -a parent_commits_arr <<<  "$parent_commits_str"

	while read changed_file_name; do
		if ! [[ $changed_file_name = *"java" ]]
		then
			break
		fi
		
		new_file_uuid=$(uuidgen)
		git show $commit_id:$changed_file_name > $tmp_folder/to_be_compared_changed_files/$commit_id/$new_file_uuid 2> /dev/null
		if ! [ -s $tmp_folder/to_be_compared_changed_files/$commit_id/$new_file_uuid ] # file should not be empty
		then
			continue
		fi
		for parent_commit in "${parent_commits_arr[@]}"
		do
			old_file_uuid=$(uuidgen)
			git show $parent_commit:$changed_file_name > $tmp_folder/to_be_compared_changed_files/$commit_id/$old_file_uuid 2> /dev/null
			if ! [ -s $tmp_folder/to_be_compared_changed_files/$commit_id/$old_file_uuid ] # file should not be empty
			then
				continue
			fi
			echo "$commit_id, $changed_file_name, $git_project_folder-tmp/to_be_compared_changed_files/$commit_id/$old_file_uuid, $git_project_folder-tmp/to_be_compared_changed_files/$commit_id/$new_file_uuid" >> ../to_be_compared_files.txt
		done
	done < $changed_files
	checked_commits=$(( $checked_commits+1 ))
	if (( $checked_commits % 100 == 0 ))
	then
		echo "Checked $checked_commits commits"
	fi
done < $all_commits_file 

# Running java program to find parameter-added methods
echo "running java"
cd ..
java -jar Parameter_Adder_Extractor-1.0-SNAPSHOT-jar-with-dependencies.jar "to_be_compared_files.txt" "parameter_adder_commits.csv"
cd $git_project_folder
echo "running java is finished"
# End of running java program
echo "you can find results in parameter_adder_commits.csv"

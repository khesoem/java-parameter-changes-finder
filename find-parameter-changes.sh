# $1=git-repo-address, $2=directory to clone project into

# Initializing...
#git clone $1 $2
git_project_folder=$2
tmp_folder="$2-tmp"
sudo rm $tmp_folder -rf
mkdir $tmp_folder
sudo chmod 777 $tmp_folder -R
tmp_folder=../$tmp_folder
rm parameter_adder_commits.txt 2> error.log
touch parameter_adder_commits.txt
cd $git_project_folder

# Checking commits one by one
all_commits_file="$tmp_folder/all_commits.txt"
git rev-list --remotes > $all_commits_file
number_of_commits=$(wc -l < $all_commits_file)
echo "Checking commits one by one"
echo "$number_of_commits commits should be checked"
checked_commits=$(( 0 ))
while read commit_id; do
	changed_files=$tmp_folder/changed_files.txt
	parent_commits=$tmp_folder/parent_commits.txt
	git diff-tree --no-commit-id --name-only -r $commit_id > $changed_files
	parent_commits_str=$(git log --pretty=%P -n 1 $commit_id)
	IFS=" " read -r -a parent_commits_arr <<<  "$parent_commits_str"
	
	while read changed_file_name; do
		git show $commit_id:$changed_file_name > $tmp_folder/NewJavaFile.java 2> /dev/null
		for parent_commit in "${parent_commits_arr[@]}"
		do
			git show $parent_commit:$changed_file_name > $tmp_folder/OldJavaFile.java 2> /dev/null
			echo "$commit_id, $changed_file_name" >> ../parameter_adder_commits.txt
		done
	done < $changed_files
	checked_commits=$(( $checked_commits+1 ))
	if (( $checked_commits % 100 == 0 ))
	then
		echo "Checked $checked_commits commits"
	fi
done < $all_commits_file 

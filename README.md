# Gitlet
Gitlet is a version-control system inspired by Git. It provides basic functionalities for tracking changes, creating branches, committing, merging, and restoring previous versions of files. This project is implemented in Java and aims to mimic some of the core features of Git.

## Tools
- Java
- Eclipse (IDE)
- Git
- GitHub

## Features
1. Save backups of directories and files with commits.
2. Restore specific files or entire commits.
3. View commit history using the log.
4. Manage related sequences of commits with branches.
5. Merge changes from one branch into another.

## Getting Started
To use Gitlet, follow these steps:
1. Clone the repository to your local machine.
2. Navigate to the directory containing the repository.
3. Run Gitlet commands in the command line interface.

## Usage
Here are some essential Gitlet commands:

- `java gitlet.Main init`: Creates a new Gitlet repository.
- `java gitlet.Main add [file name]`: Adds a file to the staging area.
- `java gitlet.Main commit [message]`: Saves a snapshot of files in the staging area as a new commit.
- `java gitlet.Main rm [file name]`: Untracks a file.
- `java gitlet.Main log`: Displays commit history.
- `java gitlet.Main global-log`: Displays information about all commits.
- `java gitlet.Main find [commit message]`: Prints commit IDs with the given message.
- `java gitlet.Main status`: Displays branch information and staged/untracked files.
- `java gitlet.Main checkout -- [file name]`: Restores the file from the head commit.
- `java gitlet.Main checkout [commit id] -- [file name]`: Restores the file from the specified commit.
- `java gitlet.Main checkout [branch name]`: Switches to the specified branch.
- `java gitlet.Main branch [branch name]`: Creates a new branch.
- `java gitlet.Main rm-branch [branch name]`: Deletes a branch.
- `java gitlet.Main reset [commit id]`: Restores all files from the given commit.
- `java gitlet.Main merge [branch name]`: Merges changes from the specified branch.

![Alt Text](https://github.com/trxw/Gitlet/blob/master/assets/split_point.png)

## Future Directions
- Objective:-  Going Remote allow the ability to save files from the local version to a remote version. This will try to mimic the popular online repo Github.The new functionalities will be...  
    * add-remote :-  Saves the given login information under the given remote name
    * rm-remote :- Remove information associated with the given remote name.
    * push :- Attempts to append the current branch's commits to the end of the given branch at the given remote
    * fetch :- Brings down commits from the remote gitlet into the local gitlet.
    * pull :- Fetches branch [remote name]/[remote branch name] as for the fetch command, and then merges that fetch into the current branch.

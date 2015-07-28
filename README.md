# Android
This is the repository of the RadMagnet Android App

#Guidelines
1. Do not push directly to master
2. Create a separate branch for yourself and create a pull request
3. All changes should be commented appropriately
4. Before working on a new task
  1. Create a new local branch
    -   git checkout -b branch_name
  2. Pull from the master branch of the origin source
    -   git pull origin master
5. Do not use point-and-click interfaces, use command prompt for all GIT activities

# GIT Help
## Make changes to your local files
## Check which files have been changes since last push
    - git status
## Add files to staging area
    - git add file_name
## Create a commit
    - git commit -m "A Commit Message"
## Push to your branch only
    - git push origin your_branch
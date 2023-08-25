#!/bin/bash

# Determine the operating system
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS="linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    OS="mac"
elif [[ "$OSTYPE" == "cygwin" ]]; then
    OS="windows"
elif [[ "$OSTYPE" == "msys" ]]; then
    OS="windows"
elif [[ "$OSTYPE" == "win32" ]]; then
    OS="windows"
else
    OS="unknown"
fi

# Set the base directory for cloning
base_dir="$HOME/.ois"
mkdir -p "$base_dir"

# Define the tags for cloning
CORE_TAG="master"
DEPLOYER_TAG="master"

# Function to check if directory exists and is not empty
directory_exists_and_not_empty() {
    dir_path=$1

    if [ -d "$dir_path" ] && [ -n "$(ls -A "$dir_path")" ]; then
        return 0  # Directory exists and is not empty
    else
        return 1  # Directory doesn't exist or is empty
    fi
}

# Function to clone and publish a repository to Maven Local
clone_and_publish() {
    repo_url=$1
    tag=$2
    repo_name=$(basename $repo_url .git)
    clone_dir="$base_dir/$repo_name"

    # Check if the directory exist and not empty
    if directory_exists_and_not_empty "$clone_dir"; then
        echo "$clone_dir exists and is not empty. Skipping cloning."
    else
        mkdir -p "$clone_dir"
        echo "cloning $repo_url ${tag}."
        # Clone the repository with the specified tag or master if not specified
        git clone --branch ${tag} $repo_url "$clone_dir"
    fi

    cd "$clone_dir"

    # Publish the repository to Maven Local
    echo "publishing to mavenLocal $repo_url."
    if [[ "$OS" == "windows" ]]; then
        ./gradlew.bat publishToMavenLocal
    else
        ./gradlew publishToMavenLocal
    fi

    cd ..
}

# Clone and publish the core library
clone_and_publish https://github.com/attiasas/open-interactive-simulation-core.git $CORE_TAG

# Clone and publish the deployer repository
clone_and_publish https://github.com/attiasas/open-interactive-simulation-deployer.git $DEPLOYER_TAG

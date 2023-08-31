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
#CORE_TAG="master"
DEPLOYER_TAG="${1:-master}"

# Function to check if directory exists and is not empty
directory_exists_and_not_empty() {
    dir_path=$1

    if [ -d "$dir_path" ] && [ -n "$(ls -A "$dir_path")" ]; then
        return 0  # Directory exists and is not empty
    else
        return 1  # Directory doesn't exist or is empty
    fi
}

# Function to clone a repository
clone_repository() {
    repo_url=$1
    tag=$2
    repo_name=$(basename "$repo_url" .git)
    clone_dir="$base_dir/$repo_name-$tag"

    # Check if the directory exists and is not empty
    if directory_exists_and_not_empty "$clone_dir"; then
        echo "$clone_dir exists and is not empty. Skipping cloning."
    else
        mkdir -p "$clone_dir"
        echo "cloning $repo_url ${tag}."
        # Clone the repository with the specified tag or master if not specified
        git clone --branch "$tag" "$repo_url" "$clone_dir"
    fi
}

publish_repository() {
    repo_dir=$1

    cd "$repo_dir"

    # Publish the repository to Maven Local
    echo "publishing to mavenLocal $repo_dir."
    if [[ "$OS" == "windows" ]]; then
        ./gradlew.bat publishToMavenLocal
    else
        ./gradlew publishToMavenLocal
    fi

    cd ..
}

# Function to clone and publish a repository
clone_and_publish() {
    repo_url=$1
    tag=$2

    # Clone the repository
    clone_repository "$repo_url" "$tag"

    repo_name=$(basename "$repo_url" .git)
    repo_dir="$base_dir/$repo_name"

    # Publish the repository
    publish_repository "$repo_dir"
}

# Clone the deployer repository
clone_repository https://github.com/attiasas/open-interactive-simulation-deployer.git $DEPLOYER_TAG

# Determine the core tag from build.gradle
DEPLOYER_DIR="$base_dir/$(basename https://github.com/attiasas/open-interactive-simulation-deployer.git .git)-$DEPLOYER_TAG"
DEPLOYER_BUILD_GRADLE="$DEPLOYER_DIR/build.gradle"

if [ -f "$DEPLOYER_BUILD_GRADLE" ]; then
    core_version_line=$(grep "coreVersion" "$DEPLOYER_BUILD_GRADLE")
    if [[ "$core_version_line" =~ coreVersion\ *=\ *[\'\"]([0-9]+\.[0-9]+(\.[0-9]+)*(-[a-zA-Z0-9]+)*)[\'\"] ]]; then
        CORE_TAG="${BASH_REMATCH[1]}"
        echo "Found coreVersion: $CORE_TAG"

        # Clone and publish the core repository
        clone_and_publish https://github.com/attiasas/open-interactive-simulation-core.git $CORE_TAG

        # Publish the deployer repository
        publish_repository "$DEPLOYER_DIR"
    else
        echo "coreVersion not found in $DEPLOYER_BUILD_GRADLE. Exiting."
        exit 1
    fi
else
    echo "$DEPLOYER_BUILD_GRADLE not found. Unable to determine coreVersion. Exiting."
    exit 1
fi
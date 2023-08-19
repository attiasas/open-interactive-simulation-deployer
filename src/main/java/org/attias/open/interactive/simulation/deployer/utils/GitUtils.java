package org.attias.open.interactive.simulation.deployer.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class GitUtils {
    // https://github.com/jfrog/jfrog-vscode-extension/releases/latest/download/jfrog-vscode-extension-2.5.0.vsix

//    String repositoryURL = "https://github.com/ianlapham/release-and-build.git"; // Replace with the repository URL
//    String tag = "v1"; // Replace with the desired tag
//    String destinationFolder = "C:\\Users\\Assaf Attias\\.ios\\runners\\1.0-SNAPSHOT";

    public static final String OIS_RUNNER_GIT_REPO = "https://github.com/attiasas/open-interactive-simulation-runner.git";

    // Folder needs to be created before clone
    public static void cloneRepoByTag(String repositoryURL, String tag, String destinationFolder) throws GitAPIException {
        Git.cloneRepository()
                .setURI(repositoryURL)
                .setDirectory(new File(destinationFolder))
                .setBranch(tag) // Checkout the specific tag
                .call();
    }
}

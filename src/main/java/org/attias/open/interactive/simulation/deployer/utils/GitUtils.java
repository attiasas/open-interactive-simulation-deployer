package org.attias.open.interactive.simulation.deployer.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class GitUtils {

    // Folder needs to be created before clone
    public static void cloneRepoByTag(String repositoryURL, String branch, String destinationFolder) throws GitAPIException {
        Git.cloneRepository()
                .setURI(repositoryURL)
                .setDirectory(new File(destinationFolder))
                .setBranch(branch)
                .call();
    }
}

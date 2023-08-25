package org.attias.open.interactive.simulation.deployer.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class GitUtils {

    public static void cloneRepoByTag(String repositoryURL, String branch, String destinationFolder) throws GitAPIException {
        // Folder needs to be created before clone
        Git.cloneRepository()
                .setURI(repositoryURL)
                .setDirectory(new File(destinationFolder))
                .setBranch(branch)
                .call();
    }
}

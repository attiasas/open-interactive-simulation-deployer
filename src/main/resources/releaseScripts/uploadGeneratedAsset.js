const github = require('@actions/github');
const core = require('@actions/core');
const fs = require('fs');

const tag = process.env.GITHUB_REF.split('/')[2];
const octokit = github.getOctokit(process.env.GITHUB_TOKEN);
octokit.repos
    .listReleases({
        owner: process.env.OWNER,
        repo: process.env.REPOSITORY
    })
    .then(releases => {
        const release = releases.data.find(release => release.tag_name === tag);
        const fileName = process.env.ASSET
        const filePath = '../build/libs/' + fileName;
        core.info('Uploading ' + process.env.NAME);
        octokit.repos.uploadReleaseAsset({
            file: fs.createReadStream(filePath),
            headers: {
                'content-length': fs.statSync(filePath).size,
                'content-type': 'application/zip'
            },
            name: process.env.NAME,
            url: release.upload_url
        });
    })
    .catch(error => {
        core.error(error);
    });
#!/usr/bin/env groovy
def config = [
    scriptVersion  : 'v7',
    pipelineScript: 'https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git',
    credentialsId: 'github',
    javaVersion: "11",
    jacoco: true,
    docs: false,
    sonarQube: false,
    openShiftBuild: false,
    manualReleaseEnabled: true,
    versionStrategy: [],
    iqOrganizationName: "Team AOS",
    compilePropertiesIq: "-x test",
    chatRoom: "#aos-notifications"
]

fileLoader.withGit(config.pipelineScript, config.scriptVersion) {
  jenkinsfile = fileLoader.load('templates/leveransepakke')
}
jenkinsfile.gradle(config.scriptVersion, config)
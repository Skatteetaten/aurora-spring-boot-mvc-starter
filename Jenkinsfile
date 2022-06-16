#!/usr/bin/env groovy
env.CI = true

def version = 'v7'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
    jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
    credentialsId: 'github',
    javaVersion: "11",
    docs: false,
    sonarQube: false,
    openShiftBuild: false,
    manualReleaseEnabled: true,
    versionStrategy: [],
    iqOrganizationName: "Team AOS",
    compilePropertiesIq: "-x test",
    chatRoom: "#aos-notifications"
]

jenkinsfile.run(version, overrides)
#!/usr/bin/env groovy

pipeline {

    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    tools {
        jdk 'jdk1.8.0_111'
        maven 'maven-3.3.9'
    }

    parameters {
        booleanParam(
                name: "RELEASE",
                description: "Build a release from current commit.",
                defaultValue: false)
    }

    stages {

        stage('Build and unit test') {
            steps {
                script {
                    def revision = getRevision()

                    try {
                        bat "mvn " +
                                "clean install -Dresume=false " +
                                "-Darguments=\"-Dmaven.javadoc.failOnError=false\" -Drevision=${revision}"
                    } finally {
                        junit 'build/test-results/test/TEST-*.xml'
                        archive 'build/libs/*.jar'
                        //jacoco exclusionPattern: '**/*Test*.class', inclusionPattern: '**/*.class', maximumBranchCoverage: '80', maximumClassCoverage: '95', maximumComplexityCoverage: '80', maximumInstructionCoverage: '5000', maximumLineCoverage: '90', maximumMethodCoverage: '95', minimumBranchCoverage: '100', minimumClassCoverage: '100', minimumComplexityCoverage: '100', minimumInstructionCoverage: '6000', minimumLineCoverage: '100', minimumMethodCoverage: '100'
                    }
                }

            }
        }

        stage('Publish to nexus') {
            steps {
                script {
                    def revision = getRevision()
                    withCredentials([file(credentialsId: 'm2_settings', variable: 'MAVEN_SETTINGS'),usernamePassword(credentialsId: 'github-kizwid', passwordVariable: 'SCM_PASSWORD', usernameVariable: 'SCM_USERNAME')]) {

                            //no need to tag snapshot versions (currently svn does not like tagging - 'scm:tag')
                            def tagDirective = revision.endsWith("SNAPSHOT")?"":"scm:tag"

                            bat "git config --list"

                            bat "mvn -s ${MAVEN_SETTINGS} -Dusername=${SCM_USERNAME} -Dpassword=${SCM_PASSWORD} " +
                            "deploy ${tagDirective} -Darguments=\"-Dmaven.javadoc.failOnError=false\" -Drevision=${revision} -Dmaven.tests.skip=true -DskipTests"
                        }

                }
            }
        }
    }
}

def getRevision() {
    def revisionNumber = env.BUILD_NUMBER;
    String branchName = env.BRANCH_NAME;
    println "BRANCH_NAME is " + BRANCH_NAME
    println "env.BRANCH_NAME is " + env.BRANCH_NAME
    println "branchName is " + branchName
    //all other branches are SNAPSHOTS
    if( branchName.contains("release")) {
        revisionNumber += "-rc"
    } else if( ! branchName.contains("master")) {
        revisionNumber += "-${branchName}"
    }
    if( !params.RELEASE){
        revisionNumber += "-SNAPSHOT"
    }
    return revisionNumber
}

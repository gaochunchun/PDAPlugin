package com.ccn.plugin

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScriptMain implements Plugin<Project> {

    void apply(Project project) {

        /*project.task('a_hello') {
            group = "pda"
            description = "gradle build script demo,shares only in this build.gradle"
            doLast {
                println "Hello from the BuildScriptPlugin"
                println '--------------------------------->' + System.getProperty("user.dir")
                println '--------------------------------->' + project.rootProject.name

                println '--------------------------------->' + project.rootProject.properties
                //def str = project.buildFile.getText()
                //println '--------------------------------->' + str
                //println '--------------------------------->' + project.buildFile.getText(project.buildFile)
                println '--------------------------------->' + project.buildFile
                println '--------------------------------->' + project.buildDir
                println '--------------------------------->' + project.rootDir.path
                println '--------------------------------->' + project.projectDir

                //def str = project.buildFile.text
                //println '--------------------------------->' + str
            }
        }*/


        ScriptParam param = project.getExtensions().create("PDAPlugin", ScriptParam.class)
        // gradle build完后可以获取配置的参数value
        project.afterEvaluate(new Action<Project>() {
            void execute(Project mProject) {

                /*project.android.applicationVariants.all { variant ->
                    println '------------->' + variant.name
                } */
                //println '-------------->' + project.android.defaultConfig.versionName
                //System.out.println(param.name)
                //System.out.println(param.s8_ZipXmlName)

                def projectName = param.name
                def pdaSDCardDirName = param.pdaSDCardDirName
                def s8_ZipXmlFileName = param.s8_ZipXmlName

                if (projectName == null) {
                    println '\n ★ -----------------> 请在当前Module下 build.gradle 中配置 PDAPlugin 参数属性！'
                    return
                }
                new SyncFileTask(projectName, pdaSDCardDirName, project)
                if (mProject.rootProject.name == 'SmartPDA') {
                    SyncFileTask.S8_PushXmlToPDA()
                    SyncFileTask.S8_RemovePDAFile()
                    SyncFileTask.S8_Rename_XML_ZIP(s8_ZipXmlFileName)
                } else {
                    SyncFileTask.PushXmlToPDA()
                    SyncFileTask.PushDBToPDA()
                    SyncFileTask.AllProjectZipToAssets()
                    SyncFileTask.AllProjectBuildFileToTFS()
                }
                SyncFileTask.PullToDeskTop_DB()
                SyncFileTask.PullToDeskTop_Log()
                SyncFileTask.X_uninstallApkDeleteDir()
                //SyncFileTask.X_installReleaseApk()
                SyncFileTask.ADBWiFiConnect()
                SyncFileTask.ADBWiFiDisConnect()
            }
        })
    }
}







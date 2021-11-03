package com.ccn.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class SyncOrPushFile implements Plugin<Project> {

    void apply(Project target) {
        //def rootProjectPath = rootProject.projectDir.path
        println '--------------------------------->' + System.getProperty("user.dir")
        //println '--------------------------------->' +new File(System.getProperty("user.dir")).name
        //println '--------------------------------->' +new File(System.getProperty("user.dir")).parent
        //println '--------------------------------->' + new SystemProperties().properties


        //def debugBuildConfigFieldName = android.properties.get('buildTypes').getByName("debug").getBuildConfigFields().APP_TYPE.getValue().replaceAll("\"", "")
        //println 'buildTypes debug APP_TYPE : --------------------------------->' + debugBuildConfigFieldName
        A_PushXmlToPDA("QiaoPai", target)

    }

    static void A_PushXmlToPDA(def projectName, Project target) {

        def rootProjectPath = System.getProperty("user.dir") + File.separator + 'AllProject' + File.separator + projectName + File.separator
        def PDASDCardPath = ' /sdcard/防伪追溯/'
        println '--------------------------------->' + rootProjectPath
        //注：为了保持开发的xml目录和PDA中保持一致，会删除PDA中Configuration目录，再进行推送，推送时会包含AllProject中具体项目下的CNG.xml
        Task deleteConfigurationDir = target.task("deleteConfigurationDir") {
            doLast {
                //def devicePath = ' /sdcard/防伪追溯/'
                def deleteConfigurationDirCommand = 'adb shell cd /sdcard && rm -r ' + PDASDCardPath + 'Configuration'
                def deleteConfigurationDirCommandProcess = deleteConfigurationDirCommand.execute()
                println "Success execute Command: ${deleteConfigurationDirCommand.toString().readLines()}"
                deleteConfigurationDirCommandProcess.in.eachLine { processing ->
                    println processing
                }
            }
        }

        target.task("A_PushXmlToPDA", dependsOn: deleteConfigurationDir) {
            group = "PDA"
            description = "卸载App并删除PDA本地防伪追溯目录"
            doLast {
                //Thread.start {
                //自定义的XML文件路径（可按具体存放位置更改）
                //def fromPath = project.projectDir.path + File.separator + 'XML' + File.separator + projectName + File.separator + 'Configuration'

                /*通过执行adb命令，将文件push到指定目录*/
                //def adbCommand='adb push D:/0000.txt /sdcard/防伪追溯'
                def fromPath = rootProjectPath + 'Configuration'
                println "fromPath----> " + fromPath
                //只会push project里的文件，并不push project文件夹本身
                //adb push C:\PDA\project\. /sdcard/防伪追溯
                def adbCommand = 'adb push ' + fromPath + PDASDCardPath
                def process = adbCommand.execute()
                //process.waitForOrKill(1000)//等待一段时间
                println "Success execute Command: ${adbCommand.toString().readLines()}"
                process.in.eachLine { processing ->
                    println processing
                }

                //Push CNG file is to Configuration
                def cngPath = rootProjectPath + '_ProjectOtherFile' + File.separator + 'CNG.xml'
                def deviceConfigurationPath = PDASDCardPath + 'Configuration'
                println 'CNGPath ----> ' + cngPath

                //adb push file command
                //def adbCommand='adb push D:/0000.txt /sdcard/防伪追溯'
                def adbPushCNGCommand = 'adb push ' + cngPath + deviceConfigurationPath
                def adbPushCNGCommandProcess = adbPushCNGCommand.execute()
                //adbPushCNGCommandProcess.waitFor();//永久等待，等待进程执行完成之后才能下面操作
                println "Success execute Command: ${adbPushCNGCommand.toString().readLines()}"
                adbPushCNGCommandProcess.in.eachLine { processing ->
                    println processing
                }
            }
        }
        //tasks.A_PushXmlToPDA.dependsOn(deleteConfigurationDir)

    }


}







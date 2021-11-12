package com.ccn.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip

class SyncFileTask {

    public static def groupName = "PDA"
    static def PDASDCardPath = ' /sdcard/防伪追溯/'

    static def rootProjectPath
    static def projectName
    static def project

    SyncFileTask(proName, pdaDirName, Project mProject) {
        rootProjectPath = mProject.rootDir.path + File.separator + 'AllProject' + File.separator + proName + File.separator
        println '--------------------------------->' + rootProjectPath
        projectName = proName
        if (pdaDirName != null) {
            PDASDCardPath = ' /sdcard/' + pdaDirName + '/'
        }
        project = mProject
    }

    /**
     * 将Configuration目录下的XML文件，Push到设备中
     * @param projectName
     * @param project
     */
    static void PushXmlToPDA() {
        //println 'aaaaaaaaaaaaaaaaaaaa  ====== '+project
        //注：为了保持开发的xml目录和PDA中保持一致，会删除PDA中Configuration目录，再进行推送，推送时会包含AllProject中具体项目下的CNG.xml
        Task deleteConfigurationDir = project.task(ScriptTask.deleteConfigurationDir) {
            doLast {
                def deleteConfigurationDirCommand = 'adb shell cd /sdcard && rm -r' + PDASDCardPath + 'Configuration'
                def deleteConfigurationDirCommandProcess = deleteConfigurationDirCommand.execute()
                println "Success execute Command: ${deleteConfigurationDirCommand.toString().readLines()}"
                deleteConfigurationDirCommandProcess.in.eachLine { processing ->
                    println processing
                }
            }
        }

        project.task(ScriptTask.PushXmlToPDA, dependsOn: deleteConfigurationDir) {
            group = groupName
            description = "PUSH XML文件到PDA"
            doLast {
                //自定义的XML文件路径（可按具体存放位置更改）
                //def fromPath = project.projectDir.path + File.separator + 'XML' + File.separator + projectName + File.separator + 'Configuration'

                //通过执行adb命令，将文件push到指定目录
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
    }


    /**
     * 将AllProject项目目录下的 Data 数据库文件Push到设备中
     * @param projectName
     * @param project
     * @return
     */
    static def PushDBToPDA() {
        project.task(ScriptTask.PushDBToPDA) {
            group = groupName
            description = "PUSH DB文件到PDA"
            doLast {
                def fromPath = rootProjectPath + 'Data'
                //只会push project里的文件，并不push project文件夹本身
                //adb push C:\PDA\project\. /sdcard/防伪追溯
                def adbCommand = 'adb push ' + fromPath + PDASDCardPath + 'Data'
                def process = adbCommand.execute()
                println "Success execute Command: ${adbCommand.toString().readLines()}"
                process.in.eachLine { processing ->
                    println processing
                }
            }
        }.dependsOn(project.task("deleteDBDir") {
            doLast {
                def deleteDBDirCommand = 'adb shell cd /sdcard && rm -r' + PDASDCardPath + 'Data'
                def deleteDBDirCommandProcess = deleteDBDirCommand.execute()
                println "Success execute Command: ${deleteDBDirCommand.toString().readLines()}"
                deleteDBDirCommandProcess.in.eachLine { processing ->
                    println processing
                }
            }
        })
    }


    /**
     * 将sdcard下的防伪追溯 Data 数据库目录 pull到桌面 XXX_Data 目录
     * @param projectName
     * @param project
     * @return
     */
    static def PullToDeskTop_DB() {
        project.task(ScriptTask.PullToDeskTop_DB) {
            group = groupName
            description = "PULL DB文件到桌面"
            doLast {
                def dbDirName = projectName + '_Data'  //pull 到 Desktop 的DB文件夹名称
                def desktopPath = System.getenv("USERPROFILE") + File.separator + "Desktop" + File.separator + dbDirName
                def PDADBPath = '/sdcard/防伪追溯/Data '
                def fileDir = project.file(desktopPath)
                if (fileDir.exists()) {
                    fileDir.deleteDir() //delete dir
                }
                def adbPullDBCommand = 'adb pull ' + PDADBPath + desktopPath
                def pullProcess = adbPullDBCommand.execute()
                println "Success execute Command: ${adbPullDBCommand.toString().readLines()}"
                pullProcess.in.eachLine { processing ->
                    println processing
                }
            }
        }
    }


    /**
     * 将sdcard下的防伪追溯 Log 目录 pull到桌面 <项目名>_Data 目录
     * @param projectName
     * @param project
     * @return
     */
    static def PullToDeskTop_Log() {
        project.task(ScriptTask.PullToDeskTop_Log) {
            group = groupName
            description = "PULL Log文件到桌面"
            doLast {
                def dbDirName = projectName + '_Log'  //pull 到 Desktop 的Log文件夹名称
                def desktopPath = System.getenv("USERPROFILE") + File.separator + "Desktop" + File.separator + dbDirName
                def PDALogPath = '/sdcard/防伪追溯/Log '
                def fileDir = project.file(desktopPath)
                if (fileDir.exists()) {
                    fileDir.deleteDir() //delete dir
                }
                def adbPullLogCommand = 'adb pull ' + PDALogPath + desktopPath
                def pullProcess = adbPullLogCommand.execute()
                println "Success execute Command: ${adbPullLogCommand.toString().readLines()}"
                pullProcess.in.eachLine { processing ->
                    println processing
                }
            }
        }
    }


    /**
     * 针对超8项目只Push AllProject下Configuration目录中的XML文件
     * 该命令与 PushXmlToPDA 差别：1.push时不对设备中的Configuration目录进行删除  2.超8不涉及CNG文件，不做处理
     * @param projectName
     * @param project
     */
    static void S8_PushXmlToPDA() {
        project.task(ScriptTask.S8_PushXmlToPDA) {
            group = groupName
            description = "超8 PUSH XML文件到PDA"
            doLast {
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
            }
        }
    }


    /**
     * 移除PDA中 /sdcard/防伪追溯/ 目录下所有文件
     * （若_ProjectOtherFile目录下存在testServiceAddress.txt则Push到根目录）
     * @param projectName
     * @param project
     */
    static void S8_RemovePDAFile() {
        Task S8_deleteAllDir = project.task(ScriptTask.S8_deleteAllDir) {
            doLast {
                def deleteDirCommand = 'adb shell cd /sdcard && rm -r' + PDASDCardPath
                def deleteDirCommandProcess = deleteDirCommand.execute()
                println "Success execute Command: ${deleteDirCommand.toString().readLines()}"
                deleteDirCommandProcess.in.eachLine { processing ->
                    println processing
                }
            }
        }
        project.task(ScriptTask.S8_RemovePDAFile, dependsOn: S8_deleteAllDir) {
            group = groupName
            description = "超8 移除PDA防伪追溯目录下所有文件"
            doLast {
                def rootProjectPath = project.rootProject.name + File.separator + 'AllProject' + File.separator + projectName + File.separator
                //Push testServiceAddress.txt file is to /sdcard/防伪追溯/
                def testServiceAddressPath = rootProjectPath + '_ProjectOtherFile' + File.separator + 'testServiceAddress.txt'
                if (project.file(testServiceAddressPath).exists()) {
                    println 'testServiceAddress.txt ----> ' + testServiceAddressPath
                    //adb push file command
                    def adbPushCommand = 'adb push ' + testServiceAddressPath + PDASDCardPath + 'testServiceAddress.txt'
                    def commandProcess = adbPushCommand.execute()
                    println "Success execute Command: ${adbPushCommand.toString().readLines()}"
                    commandProcess.in.eachLine { processing ->
                        println processing
                    }
                }
            }
        }
    }


    /**
     * 将根目录 AllProject 下的 Configuration目录文件 重命名并压缩zip，便与超8批量上传发布
     * @param projectName
     * @param project
     * @return
     */
    static def S8_Rename_XML_ZIP(xmlzipName) {

        //文件命名：读取build.gradle文件中 PDAPlugin 配置的 S8_ReName_XmlZip属性
        def ZipXmlFileName
        if (xmlzipName == null) {
            ZipXmlFileName = '100000000000_1.0.0'   //set default name
        } else {
            ZipXmlFileName = xmlzipName
        }
        //println 'ZipXmlFileName -------> ' + ZipXmlFileName

        /* 当 Super8_XML_ZIP 目录已创建且存在xml文件时，执行打包zip任务 */
        Task create_Zip = project.task(ScriptTask.create_Zip) {
            def rootPath = project.rootDir.path + File.separator + 'AllProject' + File.separator + projectName + File.separator
            def toPath = rootPath + 'S8_XML_ZIP'
            def zipFileDir = project.file(toPath)
            if (zipFileDir.exists()) {
                project.file(toPath).eachFile { mfile ->
                    int index = mfile.name.lastIndexOf('.')
                    def nameX = mfile.name.substring(0, index)
                    //println ' -------> ' + mfile.name
                    //println ' -------> ' + nameX

                    //if (project.tasks.findByPath("$nameX") == null) {
                    finalizedBy(
                            //project.task("$nameX", type: Zip) {
                            project.task("$nameX", type: Zip, overwrite: true) {
                                from mfile
                                include '*.xml', '*.XML'
                                destinationDir project.file(toPath)
                                baseName nameX
                                appendix ''
                                version ''
                                classifier ''
                            })
                }
            } else {
                //println ' ★ -------> 请确保 S8_XML_ZIP 目录中存在 XML 文件后再次执行该任务！'
            }
        }

        //首次运行Copy并重命名XML文件到S8_XML_ZIP目录中，当文件存在后再次运行该任务会生成对应的Zip压缩包
        project.task(ScriptTask.S8_Rename_XML_ZIP, type: Copy) {
            group = groupName
            description = "超8重命名XML、ZIP文件"
            def rootPath = project.rootDir.path + File.separator + 'AllProject' + File.separator + projectName + File.separator
            def fromPath = rootPath + 'Configuration'
            def toPath = rootPath + 'S8_XML_ZIP'

            from(fromPath)
            into(toPath)
            include('*.xml', '*.XML')
            exclude('LayoutUI.*', 'Check.*')
            rename {
                def fileName ->
                    if (fileName.endsWith('xml') || fileName.endsWith('XML')) {
                        ZipXmlFileName + '_' + fileName
                    }
            }
            finalizedBy(create_Zip)
        }.dependsOn(project.task("deleteExistedFile", type: Delete) {
            delete project.rootDir.path + File.separator + 'AllProject' + File.separator + projectName + File.separator + 'S8_XML_ZIP'
            //println 'toPath--->' + toPath
        })

    }


    /**
     * 将AllProject下的项目打包zip到 asserts 目录下，方便打包
     * @param projectName
     * @param project
     * @return
     */
    static def AllProjectZipToAssets() {
        //Gradle Zip文档  https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Zip.html
        //if (tasks.findByPath(taskName) == null) {tasks.create(taskName)}
        //if (project.tasks.findByPath(ScriptTask.AllProjectZipToAssets) == null) {
        //注：打包xml包时会忽略具体项目下的Data目录和CNG.xml文件
        project.task(ScriptTask.AllProjectZipToAssets, type: Zip) {
            //project.tasks.create(ScriptTask.AllProjectZipToAssets, Zip) {
            group = groupName
            description = "打包zip到asserts目录下"

            def resProjectPath = project.rootDir.path + File.separator + 'AllProject' + File.separator + projectName
            def targetPath = System.getProperty("user.dir") + File.separator + project.name + File.separator + 'src' + File.separator + 'main' + File.separator + 'assets'

            from resProjectPath
            exclude('_ProjectOtherFile', 'Data', '归档文件_*')//忽略指定目录
            include 'OnlineTime', '*/*'   //打包时只包含文件夹内的内容 Configuration  Image  Voice
            destinationDir project.file(targetPath)
            baseName projectName
            appendix '' //The appendix part of the archive name, if any.
            version '' //The version part of the archive name, if any. eg:FeiHe-1.0.0.zip
            //extension 'zip' //The extension part of the archive name. Default is zip
            classifier ''   //The classifier part of the archive name, if any.
            def DataDir = project.file(resProjectPath + File.separator + 'Data')
            if (DataDir.exists()) {
                def DBZipFileName = projectName + '_Data'
                finalizedBy(project.task(ScriptTask.createDBZip, type: Zip) {
                    from resProjectPath + File.separator + 'Data'
                    //exclude('CNG.xml', 'Data', 'Ver.txt')//忽略CNG.xml文件和Data目录
                    //include 'data.db','mf.db','pro.db','set.db'
                    include '*.db'   //打包db文件
                    destinationDir project.file(targetPath)
                    baseName DBZipFileName
                    appendix '' //The appendix part of the archive name, if any.
                    version '' //The version part of the archive name, if any. eg:FeiHe-1.0.0.zip
                    classifier ''   //The classifier part of the archive name, if any.
                }.dependsOn(project.task("deleteExistedDBZip", type: Delete) {
                    delete targetPath + File.separator + DBZipFileName + '.zip'
                }))
            } else {
                //println ' ★★★★★★ AllProject下不存在Data目录，不会构建数据库zip至assets ★★★★★★' + '\n'
            }
        }.dependsOn(project.task(ScriptTask.deleteExistedXMLZip, type: Delete) {
            def targetPath = System.getProperty("user.dir") + File.separator + project.name + File.separator + 'src' + File.separator + 'main' + File.separator + 'assets'
            delete targetPath + File.separator + projectName + '.zip'
        })
        //}
    }


/*******************************>   将AllProject下指定项目资料归档到指定TFS目录，（自动执行TFS命令功能待定）   <*********************************/
/**
 * Frist：使用该Task需要设置TFS文件归档路径（AndroidPDA\GradleConfig\LOCAL_TFS.txt），执行会先删除路径下的文件，再进行归档操作
 * 注意路径名称如：JunLeBao.LOCAL.TFS，其中JunLeBao读取的是build.gradle中配置的buildTypes下debug()打包方法定义的的buildConfigField属性
 *
 * Second：该任务会将AllProject项目目录下的文件归档到指定TFS目录，归档文件-->
 *
 *      _ProjectOtherFile目录下的文件（如CNG.xml  Ver.txt  TestServiceAddress.txt  版本说明.txt  等）
 *      100956022832_A_V1.0.1.2_app_release.apk 注：不管APK是否存在，都会自动进行构建最新的（apk构建成功目录：newUIApp\build\outputs\apk\release\）
 *
 *     配置目录：
 *     JunLeBao 目录：Configuration 、 Image 、 Voice 、 OnlineTime
 *     JunLeBao_Data 目录：data.db 、 mf.db 、 pro.db 、 set.db
 *     JunLeBao.zip 、 JunLeBao_Data.zip
 *
 * 备注：其他需要归档的文件都可以放置在_ProjectOtherFile目录中
 *
 * 重点说明：若执行任务过程中归档路径出现中文乱码，可以设置 Help --> Edit Custom VM Options... 加上 -Dfile.encoding=utf-8 后重启AS即可
 **/


    //clean --> assembleRelease  --> deleteExistedTFSFile   --> AllProjectBuildFileToTFS
    static def AllProjectBuildFileToTFS() {
        //---------------------------------------------这部分有待优化------------------------------------------
        Task AllProjectZipToAssets2 = project.tasks.create('AllProjectZipToAssets2', Zip) {
            def resProjectPath = project.rootDir.path + File.separator + 'AllProject' + File.separator + projectName
            def targetPath = System.getProperty("user.dir") + File.separator + project.name + File.separator + 'src' + File.separator + 'main' + File.separator + 'assets'
            from resProjectPath
            exclude('_ProjectOtherFile', 'Data', '归档文件_*')//忽略指定目录
            include 'OnlineTime', '*/*'   //打包时只包含文件夹内的内容 Configuration  Image  Voice
            destinationDir project.file(targetPath)
            baseName projectName
            appendix '' //The appendix part of the archive name, if any.
            version '' //The version part of the archive name, if any. eg:FeiHe-1.0.0.zip
            //extension 'zip' //The extension part of the archive name. Default is zip
            classifier ''   //The classifier part of the archive name, if any.
            def DataDir = project.file(resProjectPath + File.separator + 'Data')
            if (DataDir.exists()) {
                def DBZipFileName = projectName + '_Data'
                finalizedBy(project.task('createDBZip2', type: Zip) {
                    from resProjectPath + File.separator + 'Data'
                    //exclude('CNG.xml', 'Data', 'Ver.txt')//忽略CNG.xml文件和Data目录
                    //include 'data.db','mf.db','pro.db','set.db'
                    include '*.db'   //打包db文件
                    destinationDir project.file(targetPath)
                    baseName DBZipFileName
                    appendix '' //The appendix part of the archive name, if any.
                    version '' //The version part of the archive name, if any. eg:FeiHe-1.0.0.zip
                    classifier ''   //The classifier part of the archive name, if any.
                }.dependsOn(project.task("deleteExistedDBZip2", type: Delete) {
                    delete targetPath + File.separator + DBZipFileName + '.zip'
                }))
            } else {
                //println ' ★★★★★★ AllProject下不存在Data目录，不会构建数据库zip至assets ★★★★★★' + '\n'
            }
        }.dependsOn(project.task('deleteExistedXMLZip2', type: Delete) {
            def targetPath = System.getProperty("user.dir") + File.separator + project.name + File.separator + 'src' + File.separator + 'main' + File.separator + 'assets'
            delete targetPath + File.separator + projectName + '.zip'
        })
        //---------------------------------------------------------------------------------------
        /* if (project.tasks.findByPath(ScriptTask.AllProjectZipToAssets) == null) {
             dependsOn:
             AllProjectZipToAssets()
         }*/

        //println '=======>' + project.tasks.findByPath(ScriptTask.AllProjectZipToAssets)
        //当有task创建时
        /*project.getTasks().whenTaskAdded { Task task ->
            println "The task ${task.getName()} is added to the TaskContainer"
        }*/
//        Task aa = project.tasks.create("myTask1") /*{
//            println "doLast in myTask1"
//        }*/
//
//        Task bb = project.tasks.create("myTask2") /*{
//            println "doLast in myTask2"
//        }*/
//
//        if (project.tasks.findByPath("myTask1") != null){
//            println  project.tasks.findByPath("myTask1").name
//        }
        /*Task aa = project.tasks.create("myTask5"){
            println "doLast in task5"
        }*/

        //注：任务执行流程
        // --> deleteExistedXMLZip --> deleteExistedDBZip   删除assets目录下xml及db的zip文件
        // -->A_AllProjectZipToAssets--->createDBZip    创建<项目名>.zip 和 <项目名_Data>.zip文件
        // -->clean-->deleteExistedTFSFile    删除本地已经存在的TFS归档目录（注：需要先从TFS迁出进行编辑，若是第一次提交则无需从TFS迁出）
        // -->assembleRelease-->A_AllProjectArchiveToTFS    生成release版本apk并将归档文件提交到指定的TFS目录

        /*clean deleteExistedXMLZip2
        AllProjectZipToAssets2
        deleteExistedDBZip2 createDBZip2
        assembleRelease
        deleteExistedTFSFile
        AllProjectBuildFileToTFS*/

        def localTFSPath = rootProjectPath + '归档文件_v' + project.android.defaultConfig.versionName
        Task deleteExistedTFSFile = project.task(ScriptTask.deleteExistedTFSFile, type: Delete, dependsOn: AllProjectZipToAssets2) {
            delete localTFSPath
            //不管是否存在apk，都会去打包
            //dependsOn 'clean'
            dependsOn 'assembleRelease'
            //mustRunAfter 'assembleRelease'
        }

        project.task(ScriptTask.AllProjectBuildFileToTFS, type: Copy, dependsOn: deleteExistedTFSFile) {
            group = groupName
            description = "归档文件打包到AllProject中具体项目下"
            if (localTFSPath == null) return
            //finalizedBy('commitTFS')
            def configName = 'XML-DB配置文件'
            def assetsPath = project.projectDir.path + File.separator + 'src' + File.separator + 'main' + File.separator + 'assets'
            def assetsZipPath = assetsPath + File.separator + projectName + '.zip'
            def assetsZipDataPath = assetsPath + File.separator + projectName + '_Data.zip'
            def apkFileUrl = project.buildDir.path + File.separator + 'outputs' + File.separator + 'apk' + File.separator + 'release' + File.separator
            //println 'apkFileUrl------> ' + apkFileUrl
            /*def folder = project.file(apkFileUrl)
            if (!folder.exists()) {
                dependsOn 'assembleRelease'
            }*/
            into localTFSPath    //本地TFS归档路径
            from(rootProjectPath + File.separator + '_ProjectOtherFile') //AllProject目录下_ProjectOtherFile项目文件
            from(apkFileUrl) {
                exclude '**/*.json'
            }
            into(configName) {
                from assetsZipPath      //assets目录下对应的项目zip包
                from assetsZipDataPath  //assets目录下对应的项目数据库zip包
            }
            into(configName + File.separator + projectName) {
                from project.zipTree(assetsZipPath)
            }
            into(configName + File.separator + projectName + '_Data') {
                from project.zipTree(assetsZipDataPath)
            }
            //exclude 'CNG.xml' //忽略CNG文件归档（可选）
            //归档时重命名CNG文件（方便维护，灌抢时可直接使用）
            rename {
                def fileName ->
                    if (fileName.startsWith('CNG')) {
                        "测试-CNG.xml"
                    }
            }
        }.dependsOn('clean')


//        def getAllProjectZipToAssets = project.tasks.findByPath(ScriptTask.AllProjectZipToAssets)
//        println "--------- getAllProjectZipToAssets --------------------" + getAllProjectZipToAssets
//
//        if (getAllProjectZipToAssets != null) {
//
//            //getAllProjectZipToAssets
//            /* println "---------taskClean.doLast --------------------"
//             getAllProjectZipToAssets.doLast {
//                 AllProjectBuildFileToTFS.mustRunAfter
//
//                 println "---------taskClean.doLast --------------------"
//             }*/
//            /*getAllProjectZipToAssets.doLast{
//                AllProjectBuildFileToTFS.execute()
//                println "---------taskClean.doLast --------------------"
//            }*/
//        }


        /* task commitTFS{
             doLast {
                 //println 'test'
                //def CommitCommand = 'net use \\192.168.1.5 /user:gaochun gao@000000'
                 //String  tf = 'E:\\Program Files\\Microsoft Visual Studio 14.0\\Common7\\IDE\\TF.exe'
                 String  tfPath = 'cd /E:/Program Files/Microsoft Visual Studio 14.0/Common7/IDE'
                //def CommitCommand = tf + tfPath +' TF delete [/lock:(none|checkin|checkout)] [/recursive] [/login:gaochun,[gao@000000]] $/客户项目/J/君乐宝/君乐宝乳业学生奶追溯项目/发布/安装文件/V1.0.1.3'
                def CommitCommand = tfPath + ' && tf status /server:http://10.20.31.18/DefaultCollection/ /workspace:* /user:gaochun'
                 Process process = CommitCommand.execute()
                 println "Success execute Command: ${CommitCommand.toString().readLines()}"
                 process.in.eachLine { processing ->
                     println processing
                 }
             }
         }*/

    }

/***********************************************************************************
 备注说明：
 ADB使用WiFi连接Android设备（手机与PC需要处于同一个局域网下）
 1.将设备与电脑通过 USB 线连接
 2.通过 adb devices 命令查看是否存在设备
 3.若不存在需要在开发者模式中设置允许usb调试
 4.让设备在 8888 端口监听 TCP/IP 连接：adb tcpip 8888
 5.查找设备IP可以在设备WiFi中扎到到或者使用命令 adb shell netcfg  或者  adb shell ifconfig wlan0
 6.通过命令 adb connect HOST[:PORT] 连接设备
 7.通过命令执行 adb disconnect HOST[:PORT] 即可断开连接
 注：若连接不上可以尝试通过 adb kill-server 关闭adb服务，使用 adb start-server 启动服务
 ***********************************************************************************/

/**
 * adb wifi connect
 * @param project
 * @return
 */
    static def ADBWiFiConnect() {
        project.task(ScriptTask.ADBWiFiConnect) {
            group = groupName
            description = "ADB WiFi 连接"
            doLast {
                /*List of devices attached
                MT90-7WFE-9F05841	device
                10.20.35.13:8888	device*/

                def adbCommand1 = 'adb devices' //查看是否存在设备
                def process = adbCommand1.execute()
                println "Success execute Command: ${adbCommand1.toString().readLines()}"
                process.in.eachLine { processing ->
                    println processing
                    /*if (processing.trim().contains('device')) {
                        String deviceStr = processing.trim().split('device')[0].trim()
                        if (!deviceStr.equals('List of')) {
                            println deviceStr
                        }
                    }*/
                }

                def IP
                def adbCommand2 = 'adb shell ifconfig wlan0'
                def process2 = adbCommand2.execute()
                println "Success execute Command: ${adbCommand2.toString().readLines()}"
                process2.in.eachLine { processing2 ->
                    if (processing2.trim().contains('inet addr:')) {
                        println processing2.trim().trim()
                        //println processing2.trim().indexOf(':') + 1
                        //println processing2.trim().indexOf('Bcast')
                        //println '==>' + processing2.trim().substring(processing2.trim().indexOf(':') + 1, processing2.trim().indexOf('Bcast')).trim()
                        IP = processing2.trim().substring(processing2.trim().indexOf(':') + 1, processing2.trim().indexOf('Bcast')).trim()
                    }
                }

                def adbCommand3 = 'adb tcpip 8888'
                def process3 = adbCommand3.execute()
                println "Success execute Command: ${adbCommand3.toString().readLines()}"
                process3.in.eachLine { processing3 ->
                    println processing3.trim()
                }

                def adbCommand4 = 'adb connect ' + IP + ':8888'
                def process4 = adbCommand4.execute()
                println "Success execute Command: ${adbCommand4.toString().readLines()}"
                process4.in.eachLine { processing ->
                    println processing.trim()
                }
            }
        }
    }


/**
 * adb wifi disconnect
 * @param project
 * @return
 */
    static def ADBWiFiDisConnect() {
        project.task(ScriptTask.ADBWiFiDisConnect) {
            group = groupName
            description = "断开 ADB WiFi连接"
            doLast {
                /*String IP
                def adbCommand1 = 'adb devices' //查看是否存在设备
                Process process = adbCommand1.execute()
                println "Success execute Command: ${adbCommand1.toString().readLines()}"
                process.in.eachLine { processing ->
                    println processing
                    if (processing.trim().contains('device')) {
                        //println processing.trim()
                        println processing.trim().split('device')[0].trim()
                        IP = processing.trim().split('device')[0].trim()
                        println IP
                    }
                }*/

                def adbCommand2 = 'adb disconnect'
                def process2 = adbCommand2.execute()
                println "Success execute Command: ${adbCommand2.toString().readLines()}"
                process2.in.eachLine { processing ->
                    println processing.trim()
                }
            }
        }
    }


/**
 * 快速卸载App （同时会删除sdcard下的防伪追溯目录)
 * @return
 */
    static void X_uninstallApkDeleteDir() {
        project.task(ScriptTask.X_uninstallApkDeleteDir) {
            group = groupName
            description = "快速卸载App"
            doLast {

                def deletePDADirCommand = 'adb shell cd /sdcard && rm -r' + PDASDCardPath
                def deleteDirCommandProcess = deletePDADirCommand.execute()
                println "Success execute Command: ${deletePDADirCommand.toString().readLines()}"
                deleteDirCommandProcess.in.eachLine { processing ->
                    println processing
                }

                def uninstallCommand = 'adb uninstall com.ccn.androidSmart'
                def process = uninstallCommand.execute()
                println "Success execute Command: ${uninstallCommand.toString().readLines()}"
                process.in.eachLine { processing ->
                    println processing
                }

            }
        }
    }


}







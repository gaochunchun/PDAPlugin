# PDAPlugin
ccn pda plugin.
#### Plugin安装教程

**1.Import**

Step 1. Add the JitPack repository to your build file.

Add it in your **root** build.gradle at the end of repositories:

```
buildscript {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.2.2"
        classpath 'com.github.gaochunchun:PDAPlugin:lastVersion'
    }
}

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

Add it in your **module** build.gradle at the top:
```
apply plugin: 'com.ccn.plugin'
PDAPlugin {
    name '指定项目名称，如：QiaoPai'          //(必须) AllProject目录下项目名称
    pdaSDCardDirName '防伪追溯'             //(可选 非必须)Task所操作SDCard下的 防伪追溯 目录
    s8_ZipXmlName '101379039353_1.0.1'     //(可选 非必须)超8平台重命名Xml文件和Zip文件时的文件名
}
```


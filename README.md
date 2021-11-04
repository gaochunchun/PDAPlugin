# PDAPlugin
ccn pda plugin.
#### Plugin安装教程

**1.Import**

Step 1. Add the JitPack repository to your build file.

Add it in your **root** build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the classpath to your build file.

Add it in your **root** build.gradle at the dependency:

```
buildscript {
    dependencies {
        ...
         classpath'com.github.gaochunchun:PDAPlugin:1.2'
    }
}
```

Step 3. Add the dependency

Add it in your **module** build.gradle at the top:
```
apply plugin: 'com.ccn.plugin'
```

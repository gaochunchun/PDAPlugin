# PDAPlugin
ccn pda plugin.
#### 安装教程

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
Step 2. Add the dependency

Add it in your **module** build.gradle at the end of dependencies:
```
dependencies {
   implementation 'com.github.gaochunchun:PDAPlugin:Tag'
 }
```

java-multithread-downloader
===========================

###What?

Java-multithread-downloader is a java download library which supports multithread.

###How?

```java

//Step0: we need a download manager

DownloadManager downloadManager = DownloadManager.getInstance(); 

//Step1: we need construct a mission

String qqApp = "http://dldir1.qq.com/qqfile/qq/QQ2013/QQ2013Beta2.exe";
String saveDirectory = "";
String newName = "qqApp.exe"

DownloadMission mission = new DownloadMission(qQString,saveDirectory, newName);

//Step2: deliver this mission to manager

downloadManager.addMission(mission);

//Step3: Let's start

downloadManager.start();

```

###I'm sure, it's convinient

There are some useful method you can use:

```java

mission.getReadableSize() //get mission target file size (which is readable end with KB/MB/GB…)

mission.getReadableSpeed() //get mission's readable downloading speed

mission.getReadableAverageSpeed() //get avarage download speed

mission.getReadableMaxSpeed() //get max download speed
 
mission.getActiveTheadCount() // get mission's downloading thread count

mission.isFinished() //judge if a mission is finished

mission.getTimePassed() //get download time

mission.pause() //pause this mission ,and it will automatically resume when you start again. 

```
###Notice

This library is still under construction and I need your help. if you get some bugs,please feel free to issue or [Email me](mailto:daimajia@gmail.com).

###Licence

MIT

###About me

I am a common senior student in China 22 years old. Good at Php,Java,Android,NodeJS. I just want to make a good app. If there is any intern opportunity and you think I'm suitable, welcome to email me:  [Email Me](mailto:smallbeardict@163.com)

*	Northwest Universite of China
*	Beijing Normal Universy
*	Site: [Daimajia](http://www.zhan-dui.com)
*	Weibo:[代码家](http://weibo.com/daimajia)
*	Twitter:[LinHuiwen](http://twitter.com/LinHuiwen)
*	Instagram:[daimajia](http://instagram.com/daimajia)
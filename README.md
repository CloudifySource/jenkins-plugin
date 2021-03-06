jenkins-plugin
==============
A Jenkins plugin that invokes a cloudify custom command with parameters related to the build

The example depend on other plugins: 

1. archive plugin help archiving WAR 
2. S3 plugin to publish WAR file to external storage.

The `recipes` directory include a simple application `artifact.zip`, this application include custom command named `downloadFromS3`. extract the `artifact.zip` application to `<cloudify install dir>/recipes/apps` then install install `artifact` application `install-application artifact`

Building
========

1. clone plugin source `git clone https://github.com/CloudifySource/jenkins-plugin.git`
2. from command line navigate to the `<workspace>/jenkins-plugin/cloudify-jenkins`
3. run `mvn` will generate package `<workspace>/jenkins-plugin/cloudify-jenkins/target/cloudify-jenkins.hpi`

Installing
==========
1. open jenkins management in browser `http://<your server address>/jenkins`
2. click `Manage Jenkins` link at the left pane ![step 1](/readme/step1.png "Manage Jenkins")
3. click `Manage Plugins` link ![step 2](/readme/step2.png "Manage Plugins")
4. click on `Advanced` tab ![step 3](/readme/step3.png "Advanced")
5. under "Upload Plugin" section click `Choose file` button then click `Upload` button ![step 4](/readme/step4.png)
6. wait while an installation is done ![step 5](/readme/step5.png) ![step 6](/readme/step6.png)
7. verify that the plugin is presented in the `Installed` tab ![step 7](/readme/step7.png)

> Note: for this sample we will use S3 as extenal storage to keep the generated war so, to install S3 
> * click the `Available` tab at the Plugin manager
> * type at the filter form `S3`
> * check `S3 plugin` and click `install without restart` button 

Usage
=====
* Go to the jenkins start page and create `new job` of type `Build a maven2/3 project`

* #### S3 plugin Global configuration

1. after installing S3 plugin successfully will add configuration section at `Manage Jenkins > Configure System` 
2. fill profile fields under the Amazon S3 profiles ![step 8](/readme/step8.png)

* #### Cloudify custom command invoker

1. click the job name link and then click `Configure` at the left pane
2. scroll to `Post-build Actions` section add the following actions by selecting from the `Add post-build action`

> Note: important to select actions at the following order:
> * select `Archive the artifacts`
> * select `Publish artifacts to S3 Bucket`
> * select `Cloudify custom command invoker`

![step 9](/readme/step9.png)

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/CloudifySource/jenkins-plugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

jenkins-plugin
==============

A Jenkins plugin that invokes a cloudify custom command with parameters related to the build

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

### S3 plugin Global configuration

1. after installing S3 plugin successfully will add configuration section at `Manage Jenkins > Configure System` 
2. 

### Cloudify custom command

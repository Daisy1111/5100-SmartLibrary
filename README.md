# 5100-SmartLibrary

1. This project is written in Play frame work version 2.2.1. And the Java JDK & JRE is 1.8. You need to download the play framework from https://downloads.typesafe.com/play/2.2.1/play-2.2.1.zip and unzip file.
2. Configuration about the environment variable.
 - vim ~/.bash_profile
 - add two lines:
   export PLAY_HOME=”your path to play2.2.1”
   export PATH=$PATH:$PLAY_HOME
 - source ~/.bash_profile
3. Open intellij with the source code, connect to the H2 database
4. Run command: cd /path/to/5100-SmartLibrary
5. Run command: play run
6. Access http://localhost:9000/ from your web browser

# csga-bot
  
beep-beeeeeep  
I'm a bot  
A Discord bot  
Probably just for testing  
beep beep  
  
## Building

This project use [Lombok](https://projectlombok.org/) to generate getters and setters.
You may also use [material](https://github.com/equinusocio/material-theme) to optimize your theme.

### Running the project

- IDEA: `Run -> Edit Configurations...`, click the plus (+) button and select `Application`.
Set the `Name` to `csga-bot`. Set the field `Use classpath of module` to `csga-bot_main`,
then click on the button of the `Main class` field and select the `BotLauncher` class.
Click `Apply` and then `OK`. You have now created a run configuration for your desktop project.
You can now select the configuration and run it.

- Eclipse: Right click the desktop project, `Run As -> Java Application`.
Select the desktop starter class (e.g. BotLauncher.java).
    
### Packaging the project

`gradlew dist`

This will create a runnable JAR file located in the `build/libs/` folder.
It contains all necessary code and can be run either by double clicking or on the command line
via `java -jar jar-file-name.jar`. Your audience must have a JVM installed for this to work. 
The JAR will work on Windows, Linux and Mac OS X!

## Licence

    Copyright 2016 AltMirrorBell

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

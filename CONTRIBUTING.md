## Contributing to TestMe IJ Plugin Project

Contributions are welcomed :)
Unless your intended contribution bares minor effort - it is highly recommended to post an issue/feature request first on the [project forum](https://weirddev.com/forum#!/testme) -
the changes you plan might not fit well with planned features on the project road map or with existing functionality, so it's better to start a debate and agree on the suggested changes.
We just don't want to waste your time and effort on a pull request that didn't get to be approved and merged into the project main branch :(

### Developing
The Project is built with Gradle - import build.gradle to sync/create project settings.
- Gradle version - see `distributionUrl` in [gradle/wrapper/gradle-wrapper.properties](gradle/wrapper/gradle-wrapper.properties)
- JDK Version - see `jvmTargetVersion` in [gradle.properties](gradle.properties)

Please add/update Unit Tests for new/updated functionality. Please adhere to commonly known Java development best practices and code styling standards (I have no idea what that means :) )

### Building

IDEA related development lifecycle task are provided by [gradle-intellij-plugin](https://github.com/JetBrains/gradle-intellij-plugin)
main relevant gradle tasks
- `gradle runIde` - loads IDEA application already bundled with TestMe plugin being developed
- `gradle check` - run Unit and Integration tests
- `gradle buildPlugin` - build and package TestMe plugin zip installation (for manually testing installation process)  

### Contributor License Agreement

Please sign the [Contributor License Agreement](https://cla-assistant.io/wrdv/testme-idea). 
It can be signed now, or right after raising a Pull Request (Credits to [CLA-Assistant](https://github.com/cla-assistant/cla-assistant) for their cool, automated CLA form, linked from the pull request)
 
The CLA is a pretty standard non-exclusive license grant. If you have ever signed one before - you'll find it familiar.

*Thanks for contributing!*
# osu-map-downloader
jar file that automatically downloads osu maps given a range of ids and a destination path.

# Installation Guide
- must have java 21 installed on your system
  - you can install java 21 [here](https://www.oracle.com/java/technologies/downloads/)
- once you have installed jdk 21, you can test it in the terminal by running `java --version`
- next you can go to releases in the repo and under the lastest release's assets download the .jar file

# Usage

```jshell
java -jar osu-map-downloader.jar [startId] [endId] [path-to-directory]
```
- the ordering of the ids doesn't matter as long as they are valid and found on [the OSU! beatmaps website](https://osu.ppy.sh/beatmaps/packs)


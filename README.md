# MK4DuoBoards
A graphical tool to help MK4Duo developers managing their **boards/ files**

### Made with love for the MK4Duo community, hoping it will turn out to be helpful!

## System requirements
- An updated version of Java 8 JRE or newer.
- At least about 200 MB of free RAM

## Steps to install
- Download this repository: to run the program you only need the MK4DuoBoards.jar file.
- Run it once and close it. It should now have created a folder inside your user directory (in Linux: /home/<USER_NAME>/.MK4DuoBoards/, in Windows: C:\\Users\<USER_NAME>\.MK4DuoBoards\).
- You need now to replace the files contained in that folder with the ones you can find in the ConfigFiles folder.
- If you need to edit those files, please **read [this wiki page](https://github.com/iosonopersia/MK4DuoBoards/wiki/Config-files-format)** before!!!
- Run again the program: you now should be able to manage the MK4Duo **boards/ files** in a much simpler way!!!

## Known issues
### On Linux
- After loading data from **boards/ files**, the application window suddenly becomes unresizable. It seems like a JavaFX bug, but I'll check my code again and again!

### On Windows
- This program adapts its knowledge of the line separator to the one of the operating system on which it's running. Since apparently MK4Duo **boards/ files** and the config files follow the UNIX format (only \n character), using those files on Windows will result in the program unable to do its job. Please, **remember to convert those files to the Windows format (\r\n characters) before running this program!!!**

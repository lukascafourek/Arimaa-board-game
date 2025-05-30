# THIS IS CTU FEE WORK ARCHIVE

# Arimaa

This is a semester thesis for the subject Programming in Java at CTU FEL. The most important information including the user manual and program description can be found [here](https://gitlab.fel.cvut.cz/B232_B0B36PJV/cafoulu1/-/wikis/home).

## Launching the game

> Open the project cafoulu1 in IntelliJ Idea with JDK 21 installed
>
> Load Maven project if Idea did not prepare it itself
>
> Click the Current File icon in the top right corner
>
> Go to Edit Configurations > Add new run configuration > Application > Choose the name you like > In Build and Run - Main class type "cz.cvut.fel.pjv.Launcher" > Click Modify options > Choose Add VM Options > Type in the VM options field one of the options below whether you want loggers or not
>
> -Djava.util.logging.config.file=logs/loggingON.properties
>
> -Djava.util.logging.config.file=logs/loggingOFF.properties
>
> Click Apply and OK
>
> Now you can run the program with the green play icon in the top right corner

## File hierarchy

* **logs** - directory for log files and config files for loggers
* **object_draft** - uxf and png files of the game object draft
* **saved_games** - directory with saved games as JSON files
* **scheme** - png file of the game screens scheme
* **src** - here, in other directories and packages, the files of the game itself are located
  * main
    * java
      * cz.cvut.fel.pjv (package)
        * gui (package)
          * HowToPlay.java
          * Load.java
          * MainGame.java
          * MainMenu.java
          * NotationScreen.java
          * Start.java
        * logic (package)
          * AiPlayer.java
          * Board.java
          * Figure.java
          * HumanPlayer.java
          * MoveRules.java
          * Notation.java
          * Player.java
          * SaveAndLoad.java
          * Time.java
          * WinRules.java
        * Controller.java
        * GameData.java
        * Launcher.java
      * module-info.java
    * resources
      * C.png
      * cc.png
      * D.png
      * dd.png
      * E.png
      * ee.png
      * H.png
      * hh.png
      * M.png
      * mm.png
      * R.png
      * rr.png
  * test
    * java
      * cz.cvut.fel.pjv.logic (package)
        * BoardTest.java
        * FigureTest.java
        * MoveRulesTest.java
        * NotationTest.java
        * SaveAndLoadTest.java
        * WinRulesTest.java
* **.gitignore** - to ignore unnecessary files on git
* **pom.xml** - maven project file
* **README.md** - this file

The explanation of the individual files is in the description of the program on the Wiki in the link above.

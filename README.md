# MetaStone #

This project is a personal modified project based on a java Hearthstone simulator: https://github.com/demilich1/metastone. However, since one of its gradle build file (`app/build.gradle`) has some issues, this project is actually forked from: https://github.com/jphavoc/metastone.

We modified the following files: `GameStateValueBehaviour.java`, `GameStatistics.java`, `PlayerConfigView.java`, `SimulationResult.java`, `GameLogic.java`, `app/build.gradle`.

We added the following files: `SimulateGamesCommand2.java`, `MyMetaStone.java`, `SpecificDeck.java`.

Our eventual goal is to output an executable jar which can be used as a black-box function evaluator in the project: https://github.com/czxttkl/X-AI.

To output such executable jar, you need to navigate to `app` directory, and execute `gradle shadowjar`. The output jar will be stored in `app/build/libs/shadow.jar`. You can use `java -jar app/build/libs/shadow.jar` to execute it.







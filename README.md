# Tactical Chaos
## About
Tactical Chaos is a multiplayer strategy battle board game, designed to run on PC.

It is similar in logic and rules to [TeamFight Tactics](https://teamfighttactics.leagueoflegends.com/) mode in **League of Legends** game.

This project was required for the Programming Languages course in my faculty (FITE of Damascus University) at the third year (2019), purposefully to apply the Object-Oriented Programming (OOP)'s concepts and multi-threading with Java.

The game was firstly built to be played on console interface, then a GUI developed with JavaFX was added. So the game can now be played in both console and GUI modes. 
(Built with **IntelliJ IDEA** and **Corretto 8** OpenJDK distribution (JDK 8)).

## Game Description
### Overview
###### _The following is an ovreview including only the most important aspects to know in order to take a quick trial of the game. For full description and details, please see the [Full Description](#full-description) section._
Tactical Chaos is played by two or more players (the player can be a user or a bot). They can play individually or collaborate in teams against other teams.

All movements and actions in the game occur on a rectangular board of N x M **cells**, which represents the **battlefield**.

Firstly, all players have 0 **gold**, golds is used to buy champions, which increase by 2 golds each round. **Items**, which make champions more powerful, are distributed  randomly on the board.

The game consists of two phases:

1- **First Phase:** consists of 9 rounds; at each round, the player's golds increase by 2, and he/she can buy champions from a temporal **store** that shows 5 champions taken randomly from the base store (every champion has a specific cost). When the player buys a champion, he/she can put it on the field on the chosen cell or on his/her **bench** side for later use. When all players finish their first phase, the second phase begins.

_Note:_ Player can has no more than 9 champions on the field and no more than 8 champions on the bench at the same time.

2- **Second Phase:** this phase has two distinct rounds, the **Planning Round** and the **Executing Round**, which consecute and repeat until the game over.

   - **Planning Round:** in this round, players can buy new champions from the temporal store and place them on the field or on the bench. They can also swap between champions on the bench and ones on the field. Then, every player makes a plan for the battle by arranging his/her champions and giving each of them a set of commands to be performed in the Executing Round. A command can be one of the following:
     - **Movement:** to move towards one or more of 8 directions: up, down, left, right, up & left, up & right, down & left, down & right.
     - **Attack:** to attack a specific nearby enemy champion.
     - **Use Ability:** to use the champion's ability if available.
     - **Sell:** to sell the champion for half of its buying cost.
   - **Executing Round:** in this round, the players' plans will be executed command by command. In order to minimize the gap in chance given to the players, commands are executed respectively between them (the first command by the first player is executed first, followed by the first command by the second player, and so on ..).

The player loses when a round comes and he/she doesn't have any champion on the field (even if he/she has champions on the bench) and quits the game. The team loses if all of its players lost.

The player (or the team) wins if there is no more enemy champions on the field.

### Full Description
The full game description and all rules in details are available [here](https://github.com/Amir-Diab/Test/files/8457279/Tactical.Chaos.Description.and.Requirements.zip)
.

## Gameplay (Demo)
https://user-images.githubusercontent.com/57280180/188235961-fa3a0b22-af90-490f-a338-4ef62f1cd0d4.mp4

## Download
You can try the game by downloading the release version [here](https://github.com/Amir-Diab/Tactical-Chaos/releases/tag/v1.0).

## System Architecture
### OOP Employment
Like any project based on having distinct entities and methods that indicate functional integration between them, the project’s classes was built in a way that applies the OOP's concepts (such as inheritance, polymorphism, ..), in order to obtain a clear abstract structure. This is very useful for the program building process through facilitating: dividing tasks between team members, debugging and troubleshooting, achieving flexibility, modifying the code and adding new features in the future.
### MVC as Architecture Pattern
As for the architecture pattern that the system adopts as a whole; it is the **Model-View-Controller** (MVC) pattern, which decouples user-interface (view), data (model) and application logic (controller).

This pattern is useful for games due to the fact that they often contain a large amount of information (model) that obviously represents the game (or game state), and should be displayed on the user-interface (view), but -at the same time- shouldn't be directly modifiable by the viewing classes or functions. Instead, the part responsible for the logic of the game (controller) should process and modify the data, taking into account the user inputs and respecting the game rules.

This ensures the proper course of the game, and prevents it from getting out into undesired situations, especially for multiplayer games and having centralized data that needs to be processed by one party (controller), not multiple parties (viewers).

Here is an image showing the MVC pattern diagram taken from [MDN Web Docs](https://developer.mozilla.org/en-US/docs/Glossary/MVC) website.
![MVC](https://user-images.githubusercontent.com/57280180/160621551-dbd930d0-ac39-41ee-b2be-69545dae1ccc.jpg)

We review hereunder the assumptions and rules were considered while implementation:
- The model class is just a container of data represents the corresponding entity well, and has no methods or extra data.
- The view class can have references to model classes in order to display information, and it should not modify them (read only). It should have methods for input/output operations from/to the user-interface for each interaction or move in the game.
- The controllers are arranged in levels (from general to specific, like management levels), depending on the degree of generalization or specialization of the corresponding entity. Here is a review of the controllers at their levels:

![controlling levels](https://user-images.githubusercontent.com/57280180/162480627-0c1b71b4-e511-4f29-a57d-611f7e08a681.png)

- The controller class can have a reference to the corresponding model class in order to modify it's data according to the game logic (game rules).
- The controller class can have a reference to the corresponding view class in order to call its input/output methods at points of the game course when needed.
- The controller class can have references to other controller classes from a lower level (not the same or an upper level) in order to call methods that serve methods in this class as a part of the process (subtasks / more specific tasks).

### Project Structure
#### Project Tree
![project tree](https://user-images.githubusercontent.com/57280180/160621177-a90b0f50-a4d5-4d3b-bd52-4eafc88bf915.png)

Here we have several notable entities, which are: **Game**, **Store**, **Battlefield**, **Player**, **Champion** and **Item**.

We arranged our main classes into 3 packages: model, view and controller.
- `model`: includes the classes that contain data representing the entities.
- `view`: includes the classes responsible for displaying on the user-interface.
- `controller`: includes the control classes, which collectively make up the control unit of the game (game logic).

#### Project Classes
Here we show what is each class for:
- `Attributes`: contains the constant champion attributes related to the champion's kind (_name, max health, gold cost, .._).
- `Champion`: contains the previous champion attributes as well as the variable attributes related to the game context (_current health, current mana, collected items, moves to perform, .._).
- `Item`: contains the additional powering values that the item adds to the champion (_extra basic attack, extra armor, .._).
- `Player`: contains the player's information (_player name, team number, .._) and the player's champions on both the field and the bench as well as the visibility of the field as an array.
- `Battlefield`: contains the field's information (_cells' types, champions and items on the field_).
- `Store`: contains all champions in the store.
- `Game`: represents the entire game (game state), containing all players, the store and the battlefield.

- `GameDisplay`: an interface, contains `gameSettings()` abstract function that must display game settings to be specified by the user (_including the number of players, players' names, players' types (human or bot), and the dimensions of the battlefield_) before the game starts.
- `GuiGameDisplay`: implements `GameDisplay` interface for GUI mode.
- `ConsoleGameDisplay`: implements `GameDisplay` interface for console mode.
- `PlayerIO`: an interface, contains the input/output functions for all playing interactions.
- `GuiPlayerIO`: implements `PlayerIO` interface for GUI mode.
- `ConsolePlayerIO`: implements `PlayerIO` interface for console mode.
- `BotPlayerIO`: implements `PlayerIO` interface for the bot playing, in a way that fulfills the [bot strategy](#bot-strategy) as a whole. This class has no input/output operations, and depends on the game state to make decisions, so it seems more logical than graphical and has been placed in the `controller` package. However, it can be placed in the `view` package as it just decides how `PlayerIO`'s functions work and doesn't affect the game logic or models directly. In order to view the game from the bot player's perspective, output commands can be easily added, (and it will seem more reasonable to move the class to the `view` package).

- `ChampionController`: contains the functions responsible for the champion-level operations (_adding/deleting items, applying damages, .._) . Unlike the other controllers, directly calling these functions via the class name (by making them `static` and making the class `abstract`) and passing the champion under treatment to these functions is easier than associating a controller for every champion instance.

- `BattleFieldController`: contains the functions responsible for the field-level operations (_generating a random field, adding/deleting champions and items to/from cells, .._).

- `StoreController`: contains the functions responsible for the store-level operations _(generating a temporal store, restoring champions from a temporal store, .._).

- `PlayerController`: contains the functions responsible for the player-level operations such as these in the first phase's round and the planning round (_buying champions with placing, moving, swapping, attacking,.._) and the other necessary operations (_field appearance array updating, check for loosing, .._). This class is strongly connected to the `PlayerIO` class.

- `GameController`: contains the functions responsible for the game-level (top-level) operations )_such as these in the executing round (execution of all moves types (actions) planned by players), check for game over, save the game, .._).

`GameController`'s inner classes:
- `Move`: an abstract class represents the single move or action that the player can plan for, including:
  - `Movement`
  - `BasicAttack`
  - `Sell`
  - All champions `abilities`

So, for each of the previous moves, there is a class (defined inside the `GameController` class as an inner class), which `extends Move` class and must implement the `performMove(Player, Champion)` abstract method.

- `GroupAbility`: an abstract class represents the champion group ability. It is similar to the `Move` class but has two abstract methods:
  - `activate(Champion, int)`: to activate the ability.
  - `deactivate(Champion, int)`: to deactivate the ability.

So, for each group ability, there is a class (defined inside the `GameController` class as an inner class), which `extends GroupAbility` class and must implement the previous two abstract methods.

- `exceptions` package classes: represent the custom exceptions for the game. (These classes are empty but each `extends Exception` class).
- `util` package classes:
  - `Generator`: generates `all data.json` file at the beginning of the game, which contains the full game information it requires to run properly (_all champions kinds and their constant attributes values, champions-to-groups mapping, items names, .._).
  - Lock: for solving sync-related issues. (_as described in [Lock Usage](#waiting-for-a-gui-event--lock-usage) section_).
  
## Multi-Threading & Synchronization
### Parallel Playing & Rounds Synchronization
For the console mode, the entire application runs -from start to finish- on one thread, and players play consecutively on the same console interface (player 1 plays first, then player 2 plays and so on ..).

For the GUI mode, when the game starts, a new window is created for each player (on the same PC device). Also, there is a special thread for each (all are organized by the `GameController` class on the main thread), so that they can play in parallel (each on his/her own window), and the program runs as follows:
1. each player starts his/her first phase on a new thread. Then, when a thread ends, it waits for all other threads to end (by calling `.join()` command).
2. when all players finish their first phase, the same scenario happens for the planning round.
3. then the executing round is performed by the `GameController` class on the main thread, and the output is updated for all players for every move or action done.

steps 2 & 3 are repeated until the game over.

### Waiting for a GUI Event & Lock Usage
When a method requires user to interact or do something (such as pressing a specific button or click on a cell, etc..), the thread waits until it is notified as soon as the required action done.

In order not to continue to the following part of the program in case we have a false notification (this happens when user conducts a wrong act out of game context, such as pressing a wrong button by mistake), the `Lock` class was created.

Locks are used to achieve synchronization between the dependent parts of the code (waiting and notifier). It has a `boolean` attribute `locked` refers to the locking state (keep waiting in loop if `locked` is still `true`, break it and continue elsewise), which is useful in dealing with false notifications.

## Save & Load Game
Since the `Game` class represents the entire game (game state), we can simply save the game by writing the game object in a file (`Game` implements `Serializable` interface as other model classes do), and we can load the game by reading the written object followed by an initialization of the game.

## Bot Strategy
Bot strategy is very simple:
- At the first phase:
  - **Buying**: the bot buys champions from the temporal store as much as possible in left-to-right order.
  - **Placing**: the bought champions are placed first on the field, so that the first champion is placed on a random cell, then the following champions are placed randomly near to it (with 10 cells as maximum distance). When the number of the field champions reaches the maximum permissible limit, the newly bought champions are placed on the bench.
- At the second phase: the bot makes its plan according to the following steps:
1. **Buying & Placing**: as described above.
2. bench champions are placed on the field as much as possible in left-to-right order. (Placing mechanism is the same as described above (placing near to the first field champion)).
3. for each champion on the field, the bot acts as follows:
   1. if there is an enemy in the champion's attack range, it activates the ability if available, and makes a basic attack on it.
   2. move towards a random direction.
   3. if the champion's health is very bad, it swaps the champion with another one from the bench which has a good health.

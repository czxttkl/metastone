package net.demilich.metastone.my;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.IBehaviour;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.*;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.decks.RandomDeck;
import net.demilich.metastone.game.decks.SpecificDeck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.simulationmode.SimulateGamesCommand2;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MyMetaStone {

    static {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        try {
            CardCatalogue.loadLocalCards();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CardParseException e) {
            System.err.println(e.getMessage());
        }
    }

    protected static HeroCard getHeroCardForClass(HeroClass heroClass) {
        for (Card card : CardCatalogue.getHeroes()) {
            HeroCard heroCard = (HeroCard) card;
            if (heroCard.getHeroClass() == heroClass) {
                return heroCard;
            }
        }
        return null;
    }

    private static int readNumberOfGames(String[] args) {
        int numberOfGames = 100;
        if (args.length > 0) {
            numberOfGames = Integer.parseInt(args[0]);
        }
        return numberOfGames;
    }

    private static IBehaviour readPlayer1AI(String[] args) {
        IBehaviour playerAI = null;
        if (args.length > 0) {
            String typeOfAI = args[1];
            if (typeOfAI.equals("random")) {
                playerAI = new PlayRandomBehaviour();
            } else if (typeOfAI.equals("gamestate")) {
                playerAI = new GameStateValueBehaviour();
            } else if (typeOfAI.equals("greedymove")) {
                playerAI = new GreedyOptimizeMove(new WeightedHeuristic());
            }
        } else {
            // default
           //   playerAI = new GreedyOptimizeMove(new WeightedHeuristic());
            playerAI = new GameStateValueBehaviour();
        }
        return playerAI;
    }

    private static IBehaviour readPlayer2AI(String[] args) {
        IBehaviour playerAI = null;
        if (args.length > 0) {
            String typeOfAI = args[2];
            if (typeOfAI.equals("random")) {
                playerAI = new PlayRandomBehaviour();
            } else if (typeOfAI.equals("gamestate")) {
                playerAI = new GameStateValueBehaviour();
            } else if (typeOfAI.equals("greedymove")) {
                playerAI = new GreedyOptimizeMove(new WeightedHeuristic());
            }
        } else {
            // default
            //   playerAI = new GreedyOptimizeMove(new WeightedHeuristic());
            playerAI = new GameStateValueBehaviour();
        }
        return playerAI;
    }

    private static HeroClass readHeroClass(String[] args) {
        HeroClass playerHeroClass = null;
        if (args.length > 0) {
            String typeOfHeroClass = args[3];
            switch (typeOfHeroClass) {
                case "warrior":
                    playerHeroClass = HeroClass.WARRIOR;
                    break;
                case "mage":
                    playerHeroClass = HeroClass.MAGE;
                    break;
                case "priest":
                    playerHeroClass = HeroClass.PRIEST;
                default:
                    //
            }
        } else {
            playerHeroClass = HeroClass.WARRIOR;
        }
        return playerHeroClass;
    }

    private static DeckFormat initDeckFormat() {
        DeckFormat deckFormat = new DeckFormat();
        for (CardSet set : CardSet.values()) {
            if (set.name().equals("BASIC") ||
                    set.name().equals("CLASSIC") ||
                    set.name().equals("THE_OLD_GODS") ||
                    set.name().equals("ONE_NIGHT_IN_KARAZHAN") ||
                    set.name().equals("MEAN_STREETS_OF_GADGETZAN")) {
                deckFormat.addSet(set);
            }
        }
        deckFormat.setName("standard");
        deckFormat.setFilename("standard.json");
        return deckFormat;
    }

    private static int[] readPlayer1CardIdx(String[] args) {
        int[] player1CardIdxArr = new int[30];
        if (args.length > 0 && args.length == 6) {
            String player1CardIdxStr = args[4];
            String[] player1CardIdxStrArr = player1CardIdxStr.split(",");
            assert player1CardIdxStrArr.length == 30;
            for (int i = 0; i < 30; i++) {
                player1CardIdxArr[i] = Integer.parseInt(player1CardIdxStrArr[i]);
            }
        } else {
            player1CardIdxArr = ThreadLocalRandom.current()
                    .ints(0, SpecificDeck.getAvailableCardSize() * 2)
                    .distinct().limit(30).toArray();
        }
        return player1CardIdxArr;
    }

    private static int[] readPlayer2CardIdx(String[] args) {
        int[] player2CardIdxArr = new int[30];
        if (args.length > 0 && args.length == 6) {
            String player2CardIdxStr = args[5];
            String[] player2CardIdxStrArr = player2CardIdxStr.split(",");
            assert player2CardIdxStrArr.length == 30;
            for (int i = 0; i < 30; i++) {
                player2CardIdxArr[i] = Integer.parseInt(player2CardIdxStrArr[i]);
            }
        } else {
            player2CardIdxArr = ThreadLocalRandom.current()
                        .ints(0, SpecificDeck.getAvailableCardSize() * 2)
                        .distinct().limit(30).toArray();
        }
        return player2CardIdxArr;
    }

    public static void main(String[] args) {
        long timeStamp = System.currentTimeMillis();

        int numberOfGames = readNumberOfGames(args);
        IBehaviour player1AI = readPlayer1AI(args);
        IBehaviour player2AI = readPlayer2AI(args);
        HeroClass player1HeroClass = readHeroClass(args);
        HeroClass player2HeroClass = readHeroClass(args);
        DeckFormat deckFormat = initDeckFormat();
        SpecificDeck.initCards(player1HeroClass, deckFormat);
        int[] player1CardIdxArr = readPlayer1CardIdx(args);
        int[] player2CardIdxArr = readPlayer2CardIdx(args);

        // set up game config
        GameConfig gameConfig = new GameConfig();
        gameConfig.setNumberOfGames(numberOfGames);
        gameConfig.setDeckFormat(deckFormat);

        SpecificDeck player1Deck = new SpecificDeck(player1HeroClass, player1CardIdxArr);
        SpecificDeck player2Deck = new SpecificDeck(player2HeroClass, player2CardIdxArr);

        PlayerConfig player1Config =
                new PlayerConfig(
                        player1Deck,
                        player1AI
                );
        player1Config.setName("Player 1");
        player1Config.setHeroCard(getHeroCardForClass(player1HeroClass));

        PlayerConfig player2Config =
                new PlayerConfig(
                         player2Deck,
                         player2AI
                );
        player2Config.setName("Player 2");
        player2Config.setHeroCard(getHeroCardForClass(player2HeroClass));

        gameConfig.setPlayerConfig1(player1Config);
        gameConfig.setPlayerConfig2(player2Config);

        SimulateGamesCommand2 simulateGames = new SimulateGamesCommand2();
        simulateGames.execute(gameConfig);

        long player2GamesLost = simulateGames.getResult().getPlayer2Stats().getPlayer2GamesLost();
        long player2GamesWon = simulateGames.getResult().getPlayer2Stats().getPlayer2GamesWon();
        long duration = System.currentTimeMillis() - timeStamp;
        System.out.println("duration:" + duration);
        System.out.println("player 2 games won:lost");
        System.out.println(player2GamesLost + ":" + player2GamesWon);
        System.exit(0);
    }
}

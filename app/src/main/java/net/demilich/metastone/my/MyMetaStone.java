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
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.gui.simulationmode.SimulateGamesCommand2;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
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

    private static HeroClass getRandomClass() {
        HeroClass randomClass = HeroClass.ANY;
        HeroClass[] values = HeroClass.values();
        while (!randomClass.isBaseClass()) {
            randomClass = values[ThreadLocalRandom.current().nextInt(values.length)];
        }
        return randomClass;
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

    public static void main(String[] args) {
        long timeStamp = System.currentTimeMillis();

        int numberOfGames = 100;
        IBehaviour player1AI = new GameStateValueBehaviour();
        IBehaviour player2AI = new GameStateValueBehaviour();
        HeroClass player1HeroClass = HeroClass.WARRIOR;
        HeroClass player2HeroClass = HeroClass.WARRIOR;

        // read parameters
        if (args.length > 0) {
            numberOfGames = Integer.parseInt(args[0]);
            String typeOfAI = args[1];
            if (typeOfAI.equals("random")) {
                player1AI = new PlayRandomBehaviour();
                player2AI = new PlayRandomBehaviour();
            } else if (typeOfAI.equals("gamestate")) {
                // already initialized
            } else if (typeOfAI.equals("greedymove")) {
                player1AI = new GreedyOptimizeMove(new WeightedHeuristic());
                player2AI = new GreedyOptimizeMove(new WeightedHeuristic());
            }
            String typeOfHeroClass = args[2];
            switch (typeOfHeroClass) {
                case "warrior":
                    // already initialized
                    break;
                case "mage":
                    player1HeroClass = HeroClass.MAGE;
                    player2HeroClass = HeroClass.MAGE;
                    break;
                case "priest":
                    player1HeroClass = HeroClass.PRIEST;
                    player2HeroClass = HeroClass.PRIEST;
                default:
                    //
            }
        }

        // set up game config
        GameConfig gameConfig = new GameConfig();
        gameConfig.setNumberOfGames(numberOfGames);
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

        PlayerConfig player1Config =
                new PlayerConfig(
                        new RandomDeck(player1HeroClass, deckFormat),
                        player1AI
                );
        player1Config.setName("Player 1");
        player1Config.setHeroCard(getHeroCardForClass(player1HeroClass));

        PlayerConfig player2Config =
                new PlayerConfig(
                        new RandomDeck(player2HeroClass, deckFormat),
                        player2AI
                );
        player2Config.setName("Player 2");
        player2Config.setHeroCard(getHeroCardForClass(player2HeroClass));

        gameConfig.setPlayerConfig1(player1Config);
        gameConfig.setPlayerConfig2(player2Config);
        gameConfig.setDeckFormat(deckFormat);

        SimulateGamesCommand2 simulateGames = new SimulateGamesCommand2();
        simulateGames.execute(gameConfig);

        long player2GamesLost = simulateGames.getResult().getPlayer2Stats().getPlayer2GamesLost();
        long player2GamesWon = simulateGames.getResult().getPlayer2Stats().getPlayer2GamesWon();
        long duration = System.currentTimeMillis() - timeStamp;
        System.out.println("duration:" + duration);
        System.out.println("player 2 games won:" + player2GamesLost + ", player 2 games lost:" + player2GamesWon);
        System.exit(0);
    }
}

package net.demilich.metastone.my;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour;
import net.demilich.metastone.game.cards.*;
import net.demilich.metastone.game.decks.DeckFactory;
import net.demilich.metastone.game.decks.DeckFormat;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.gameconfig.GameConfig;
import net.demilich.metastone.game.gameconfig.PlayerConfig;
import net.demilich.metastone.gui.simulationmode.PlayerConfigView;
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

        GameConfig gameConfig = new GameConfig();
        gameConfig.setNumberOfGames(100);

        DeckFormat deckFormat = new DeckFormat();
        for (CardSet set : CardSet.values()) {
            if (set.name().equals("BASIC") || set.name().equals("CLASSIC") || set.name().equals("THE_OLD_GODS") || set.name().equals("ONE_NIGHT_IN_KARAZHAN") || set.name().equals("MEAN_STREETS_OF_GADGETZAN")) {
                deckFormat.addSet(set);
            }
        }

        HeroClass heroClass1 = getRandomClass();
        PlayerConfig player1Config =
                new PlayerConfig(
                        DeckFactory.getRandomDeck(heroClass1, deckFormat),
//                        new PlayRandomBehaviour()
                        new GameStateValueBehaviour()

                );
        player1Config.setName("Player 1");
        player1Config.setHeroCard(getHeroCardForClass(heroClass1));
//        Player player1 = new Player(player1Config);

        HeroClass heroClass2 = getRandomClass();
        PlayerConfig player2Config =
                new PlayerConfig(
                        DeckFactory.getRandomDeck(heroClass2, deckFormat),
//                        new PlayRandomBehaviour()
                        new GameStateValueBehaviour()
                );
        player2Config.setName("Player 2");
        player2Config.setHeroCard(getHeroCardForClass(heroClass2));
//        Player player2 = new Player(player2Config);

        PlayerConfig player3Config = new PlayerConfig();

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

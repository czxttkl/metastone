package net.demilich.metastone.game.decks;

import net.demilich.metastone.game.cards.*;
import net.demilich.metastone.game.entities.heroes.HeroClass;

import java.util.ArrayList;
import java.util.List;

public class SpecificDeck extends Deck {
    public static List<Card> availableCards = new ArrayList<Card>();

    public static int getAvailableCardSize() {
        return availableCards.size();
    }

    public static void initCards(HeroClass heroClass, DeckFormat deckFormat) {
        CardCollection classCards = CardCatalogue.query(deckFormat, card -> {
            return card.isCollectible()
                    && !card.getCardType().isCardType(CardType.HERO)
                    && !card.getCardType().isCardType(CardType.HERO_POWER)
                    && card.hasHeroClass(heroClass);
        });
        CardCollection neutralCards = CardCatalogue.query(deckFormat, card -> {
            return card.isCollectible()
                    && !card.getCardType().isCardType(CardType.HERO)
                    && !card.getCardType().isCardType(CardType.HERO_POWER)
                    && card.hasHeroClass(HeroClass.ANY);
        });
        availableCards.addAll(classCards.toList());
        availableCards.addAll(neutralCards.toList());
    }

    private Deck copyDeck;

    public SpecificDeck(HeroClass heroClass, int[] index) {
        super(heroClass);
        setName("[Specific deck]");
        this.copyDeck = new Deck(getHeroClass());
        for (int i : index) {
            // every card has two copies
            this.copyDeck.getCards().add(availableCards.get(i));
            this.copyDeck.getCards().add(availableCards.get(i));
        }
    }

    @Override
    public CardCollection getCardsCopy() {
        return this.copyDeck.getCardsCopy();
    }
}

package Blackjack;

import java.util.ArrayList;
import java.util.Random;


public class Blackjack {
	
	private ArrayList<Card> deck;
	Random rand = new Random();//to shuffle the deck
	
	//dealer
	private Card closedCard;
	private ArrayList<Card> dealerHand;
	private int dealerSum;
	private int dealerAces;

	//player
	private ArrayList<Card> playerHand;
	private int playerSum;
	private int playerAces;

	Blackjack(){
		startGame();
    }

	 public static class Card{
		String value;
		String type;

		Card(String value,String type){
			this.value = value;
			this.type = type;
		}

		public String toString(){
			return value + "-" + type;
		}

		public int getValue(){
			switch (value){
				case "A"
					: return 11;
				case "K":
				case "Q":
				case "J":
					return 10;
				default:
					return Integer.parseInt(value);//converts a string containing digits to a string value
			}
		}

        public String getImagePath(){
            return "/cards/" + toString() + ".png";
        }

		public boolean isAce(){
			return value.equals("A");
		}

	}

	public void startGame(){
		//deck
		buildDeck();
		shuffleDeck();

		//dealer
		dealerHand = new ArrayList<Card>();
		dealerSum = 0;
		dealerAces = 0;

		closedCard = deck.remove(deck.size()-1); //take last index card
		dealerSum += closedCard.getValue();
		dealerAces += closedCard.isAce() ? 1 : 0;

		Card card = deck.remove(deck.size() -1);
		dealerSum += card.getValue();
		dealerAces += card.isAce() ? 1 : 0;
		dealerHand.add(card);

		/*testing
		System.out.println("Dealer:");
		System.out.println(closedCard);
		System.out.println(dealerHand);
		System.out.println(dealerSum);
		System.out.println(dealerAces); */

		//player
		playerHand = new ArrayList<Card>();
		playerSum = 0;
		playerAces = 0;

		for (int i = 0; i < 2; i++){
			 card = deck.remove(deck.size()-1);
			 playerSum += card.getValue();
			 playerAces += card.isAce() ? 1 : 0;
			 playerHand.add(card);
		}

		/*
		System.out.println("Player:");
		System.out.println(playerHand);
		System.out.println(playerSum);
		System.out.println(playerAces); */
	}

	public void buildDeck(){
		deck = new ArrayList<Card>();
		String[] values = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
		String[] types = {"C","D","H","S"};

		for (String type : types){
			for (String value : values){
				Card card = new Card(value,type);
				deck.add(card);
			}
		}
        /* testing
		System.out.println("Build Deck:");
		System.out.println(deck); */
	}

	public void shuffleDeck(){
		for (int i = 0; i < deck.size(); i++){
			int j = rand.nextInt(52);
			Card currentCard = deck.get(i);
			Card randomCard = deck.get(j);
			deck.set(i,randomCard);
			deck.set(j,currentCard);
		}
        /* testing
		System.out.println("After shuffling:");
		System.out.println(deck); */
	}
    //gameplay methods
    public boolean playerIsBust(){
        if ( playerSum > 21){
            playerUseAce();
            return playerSum > 21;
        }return false;
    }
    private void playerUseAce(){
        while (playerSum > 21 && playerAces > 0){
            playerSum -= 10;
            playerAces --;
        }
    }
    public boolean dealerIsBust(){
        if (dealerSum > 21){
            dealerUseAce();
            return dealerSum > 21;
        }return false;
    }
    private void dealerUseAce(){
        while (dealerSum > 21 && dealerAces > 0){
            dealerSum -= 10;
            dealerAces --;
        }
    }
    public void dealerDraw(){
        while(dealerSum < 17){ //dealer must draw if sum less than 17
            Card card = deck.remove(deck.size()-1);
            dealerSum += card.getValue();
            dealerAces += card.isAce() ? 1 : 0;
            dealerUseAce();
            dealerHand.add(card);
        }
    }
    public String winner(){
        String result = "";
        if (playerIsBust())
            result = "Dealer";
        else if (dealerIsBust())
            result = "Player";
        else if (dealerSum == playerSum)
            result = "Push";
        else if (playerSum > dealerSum)
            result = "Player";
        else result = "Dealer";
        return result;
    }

    //accesors
    public ArrayList<Card> getDealerHand(){
        return dealerHand;
    }
    public ArrayList<Card> getPlayerHand(){
        return playerHand;
    }
    public  int getDealerSum(){
        return dealerSum;
    }
    public  int getPlayerSum(){
        return playerSum;
    }
    public  int getPlayerAces(){
        return playerAces;
    }
    public ArrayList<Card> getDeck(){
        return deck;
    }
    public Card getClosedCard(){
        return closedCard;
    }
    public Card removeLast(){
        return deck.remove(deck.size()-1);
    }

    //mutators
    public void updatePlayerSum(int n){
        playerSum += n;
    }
    public void updatePlayerAces(Card A){
        playerAces += A.isAce() ? 1 : 0;
        playerUseAce();
    }
    public void addPlayerCard(Card card){
        playerHand.add(card);
    }
}


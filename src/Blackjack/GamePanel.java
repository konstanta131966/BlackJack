package Blackjack;

import java.awt.*;
import java.util.Objects;
import javax.swing.*;

public class GamePanel extends JPanel {
    
    private final Blackjack game;
    private final Runnable onRoundFinished;

    private boolean roundOver      = false;
    private boolean showDealerSum  = false;
    private boolean showPlayerSum  = false;
    private boolean showMessageSum = false;

    //card aspect ration
    private static final double CARD_ASPECT_RATIO = 1.4; //height = width * 1.4
    //starting dimensions
    private int cardWidth = 110;
    private int cardHeight = 154;
    private int cardGap = 10;


    public GamePanel(Blackjack game,Runnable onRoundFinished){
        this.game = game;
        this.onRoundFinished = onRoundFinished;
        setBackground(new Color(55,100,75));
    }

     private void calculateCardDimensions(){
            //calculating card size proportionally
            this.cardHeight = (int) (getHeight() * 0.26);
            this.cardWidth = (int) (cardHeight / CARD_ASPECT_RATIO);

            //tie to bounds
            //cardWidth = Math.max(MIN_CARD_WIDTH,Math.min(MAX_CARD_WIDTH,calculateWidth));
            
            //cardHeight = (int) (cardWidth * CARD_ASPECT_RATIO);
            this.cardGap = Math.max(6,cardWidth);
        }

        @Override
        protected void paintComponent(Graphics g) {
            calculateCardDimensions();
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            //enable smooth scalling and text rendering
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

            drawDealer(g2);
            drawPlayer(g2);
            if (roundOver) {
                drawResults(g2);
            }
        }
        private void drawDealer(Graphics2D g) {
            try {
                int startY = 30; //30px from the top
                int totalCards = 1 + game.getDealerHand().size();
                int totalWidth = totalCards * cardWidth + (totalCards - 1) * 10;
                int startX = Math.max(20,(getWidth()-totalWidth)/2);

                //draw dealer's closed card
                Image closedCardImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/cards/BACK.png"))).getImage();
                if (roundOver) {
                    closedCardImg = new ImageIcon(getClass().getResource(game.getClosedCard().getImagePath())).getImage();
                }
                g.drawImage(closedCardImg, startX, startY, cardWidth, cardHeight, null);
                //draw dealer's hand
                for (int i = 0; i < game.getDealerHand().size(); i++) {
                    Blackjack.Card card = game.getDealerHand().get(i); //accesing the static class through the outer class
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, startX + (cardWidth + 10) * (i+1),startY,  cardWidth, cardHeight, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void drawPlayer(Graphics2D g) {
            //draw player's hand
            try {
                int startY = getHeight() - cardHeight - 40; //anchored to bottom above button panel
                int totalCards = game.getPlayerHand().size();
                int totalWidth = totalCards * cardWidth + (totalCards - 1) * 10;
                int startX = Math.max(20,(getWidth() - totalWidth) / 2); //centered horizontally

                for (int i = 0; i < game.getPlayerHand().size(); i++) {
                    Blackjack.Card card = game.getPlayerHand().get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, startX + (cardWidth + 10) * i, startY,cardWidth , cardHeight, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void drawResults(Graphics2D g) {
            //dynamically scale font based on current panel height
            int statusFontSize = Math.max(16,getHeight() / 22);
            int outcomeFontSize = Math.max(22,getHeight()/ 15);

            String output;
            if (showPlayerSum) {
                g.setFont(new Font("SansSerif",Font.BOLD,statusFontSize));
                g.setColor(Color.YELLOW);
                output = String.format("You got %s", game.getPlayerSum());
                drawCenteredString(g,output);
            }
            if (showDealerSum){
                g.setFont(new Font("SansSerif",Font.BOLD,statusFontSize));
                g.setColor(Color.YELLOW);
                output = String.format("Dealer has %s", game.getDealerSum());
                drawCenteredString(g,output);
            }
            if (showMessageSum) {
                String message;
                switch (game.winner()) {
                    case "Dealer" -> message = "Dealer wins";
                    case "Player" -> message = "Player wins";
                    default -> message = "Push";
                }
                g.setFont(new Font("Serif", Font.PLAIN, outcomeFontSize));
                g.setColor(Color.WHITE);
                drawCenteredString(g,message);
            }
        }

        private void drawCenteredString(Graphics g, String text) {
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            //Center dynamically using the actual current panel dimensions
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight()) / 2;
            g.drawString(text,x,y);
        }

        public void showResults(){
            this.roundOver = true;
            this.showPlayerSum = true;
            repaint();

            javax.swing.Timer dealerTimer =  new javax.swing.Timer(1000,e1 ->{
                showDealerSum = true;
                showPlayerSum = false;
                repaint();

                javax.swing.Timer messageTimer = new javax.swing.Timer(1000, e2 ->{
                    showMessageSum = true;
                    showDealerSum = false;
                    repaint();
                });

                //deal button timer
                javax.swing.Timer dealButtonTimer =  new javax.swing.Timer(1000, e3 ->{
                    if(onRoundFinished != null){
                        onRoundFinished.run();
                    }
                });
                dealButtonTimer.setRepeats(false);
                dealButtonTimer.start();
                messageTimer.setRepeats(false);
                messageTimer.start();
            });
            dealerTimer.setRepeats(false);
            dealerTimer.start();
        }
        public void resetResults(){
            showPlayerSum = false;
            showDealerSum = false;
            showMessageSum = false;
        }
}



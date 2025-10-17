package Blackjack;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.*;
import javax.swing.Timer;


public class BlackjackGUI{
    //window dimensions
    private final int boardWidth = 600;
    private final int boardHeight = 600;

    private JFrame frame;
    private JPanel gamePanel;
    private JPanel buttonPanel = new JPanel();
    private JButton hitButton = new JButton("Hit");
    private JButton stayButton = new JButton("Stay");
    private JButton dealButton = new JButton("Deal");
    private Blackjack game; //declaring the game variable

    public BlackjackGUI(){
        game = new Blackjack(); // GUI "owns" the backend
        frame = new JFrame("BlackJack 21");
        gamePanel = new GamePanel(game);
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(55,100,75));
        frame.add(gamePanel,BorderLayout.CENTER);

        //adding the buttons to the frame
        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        dealButton.setFocusable(false);
        dealButton.setVisible(false);//hidden at first
        buttonPanel.add(dealButton);
        frame.add(buttonPanel,BorderLayout.SOUTH);
        //hit button setup
        hitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (game.playerIsBust()) {
                    hitButton.setEnabled(false);
                }
                Blackjack.Card card = game.removeLast();
                game.updatePlayerSum(card.getValue());
                game.updatePlayerAces(card);
                game.addPlayerCard(card);
                gamePanel.repaint();
            }
        });
        //stay button setup
        stayButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);
                game.dealerDraw();
                ((GamePanel)gamePanel).showResults();
            }
        });
        //deal button setup
        dealButton.addActionListener(e->{
            dealButton.setVisible(false);
            hitButton.setEnabled(true);
            stayButton.setEnabled(true);

            game.startGame();
            ((GamePanel) gamePanel).resetResults();
            gamePanel.repaint();
        });


        gamePanel.repaint();
        //frame setup
        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    //inner class for the JPanel
    private class GamePanel extends JPanel{
        private Blackjack game;
        int cardWidth = 110; //ration should 1/1.4
        int cardHeight = 154;

        public GamePanel(Blackjack game){
            this.game = game;
            setBackground(new Color(55,100,75));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            drawDealer(g2);
            drawPlayer(g2);
            if (!stayButton.isEnabled()) {
                drawResults(g2);
            }
        }
        private void drawDealer(Graphics2D g) {
            try {
                //draw dealer's closed card
                Image closedCardImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/cards/BACK.png"))).getImage();
                if (!stayButton.isEnabled()) {
                    closedCardImg = new ImageIcon(getClass().getResource(game.getClosedCard().getImagePath())).getImage();
                }
                g.drawImage(closedCardImg, 20, 20, cardWidth, cardHeight, null);
                //draw dealer's hand
                for (int i = 0; i < game.getDealerHand().size(); i++) {
                    Blackjack.Card card = game.getDealerHand().get(i); //accesing the static class through the outer class
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void drawPlayer(Graphics2D g) {
            //draw player's hand
            try {
                for (int i = 0; i < game.getPlayerHand().size(); i++) {
                    Blackjack.Card card = game.getPlayerHand().get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void drawResults(Graphics2D g) {
            //applying winning conditions
            g.setFont(new Font("SansSerif", Font.PLAIN, 28));
            g.setColor(Color.YELLOW);

            if (showPlayerSum)
                g.drawString("You got " + game.getPlayerSum(), 210, 250);
            if (showDealerSum)
                g.drawString("Dealer got " + game.getDealerSum(), 210, 250);
            if (showMessageSum) {
                String message;
                switch (game.winner()) {
                    case "Dealer" -> message = "Dealer wins";
                    case "Player" -> message = "Player wins";
                    default -> message = "Push";
                }
                g.setFont(new Font("Serif", Font.PLAIN, 36));
                g.setColor(Color.WHITE);
                drawCenteredString(g,message, 260);
            }
        }

        private void drawCenteredString(Graphics g, String text, int y) {
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (boardWidth - textWidth) /2 ;
            g.drawString(text, x, y);
        }



        private boolean showPlayerSum = false;
        private boolean showDealerSum = false;
        private boolean showMessageSum = false;

        private void showResults(){
            showPlayerSum = true;
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
                    dealButton.setVisible(true);
                    dealButton.setEnabled(true);
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
}





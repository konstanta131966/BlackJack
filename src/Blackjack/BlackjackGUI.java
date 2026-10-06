package Blackjack;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.*;


public class BlackjackGUI{
    //window dimensions
    private  int boardWidth = 600;
    private  int boardHeight = 600;

    private JFrame frame;
    private JPanel gamePanel;
    private JPanel buttonPanel = new JPanel();
    private JButton hitButton = new JButton("Hit");
    private JButton stayButton = new JButton("Hold");
    private JButton dealButton = new JButton("Deal");
    private JButton fullscreenButton = new JButton("Fullscreen (F11)");
    private Blackjack game; //declaring the game variable

    public BlackjackGUI(){
        game = new Blackjack(); // GUI connects to the backend
        frame = new JFrame("BlackJack 21");
        gamePanel = new GamePanel(game);
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(55,100,75));
        frame.add(gamePanel,BorderLayout.CENTER);

        //frame setup
        frame.setSize(boardWidth,boardHeight);
        frame.setMinimumSize(new Dimension(600,500));
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        //adding the buttons to the frame
        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);

        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);

        dealButton.setFocusable(false);
        dealButton.setVisible(false);//hidden at first
        buttonPanel.add(dealButton);

        fullscreenButton.setFocusable(false);
        buttonPanel.add(fullscreenButton);


        
        frame.add(buttonPanel,BorderLayout.SOUTH);
        frame.addComponentListener(new ComponentAdapter(){
            @Override 
            public void componentResized(ComponentEvent e){
                updateButtonScaling();
            }
        });

        
        //hit button setup
        hitButton.addActionListener(e->{
            Blackjack.Card card = game.removeLast();
            game.updatePlayerSum(card.getValue());
            game.updatePlayerAces(card);
            game.addPlayerCard(card);

            if(game.playerIsBust()){
                hitButton.setEnabled(false);
                stayButton.doClick();
            }
            gamePanel.repaint();;
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

        //Fullscreen toggle logic
        Runnable toggleFullscreen= () -> {
            int state = frame.getExtendedState();
            if((state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH){
                frame.setExtendedState(JFrame.NORMAL);
                fullscreenButton.setText("Fullscreen F(11)");
            }
            else{
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                fullscreenButton.setText("Window (F11)");
            }
        };
        fullscreenButton.addActionListener(e->toggleFullscreen.run());
        
        //F11 Shortcut
        frame.getRootPane().registerKeyboardAction(
            e -> toggleFullscreen.run(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F11,0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );


        //finished adding the components
        frame.setVisible(true);
        gamePanel.repaint();

    }
    //helper method
    private void updateButtonScaling(){
            int width = frame.getWidth();
            int height = frame.getHeight();

            //scale font size proportionally betweein 14px , 24px
            int fontSize = Math.max(14,Math.min(24, height/36));
            Font dynamicButtonFont = new Font("SansSerif",Font.BOLD,fontSize);

            //scale button padding
            int padY = Math.max(6 , height /70);
            int padX = Math.max(12 , width / 60);

            JButton[] buttons = {hitButton,stayButton,dealButton,fullscreenButton};
            for (JButton button : buttons){
                button.setFont(dynamicButtonFont);
                button.setMargin(new java.awt.Insets(padY,padX,padY,padX));
            }
            buttonPanel.revalidate();
            buttonPanel.repaint();
    }





    //inner class for the JPanel
    private class GamePanel extends JPanel{
        private Blackjack game;

        //base sizing and aspect ration constraints
        private static final double CARD_ASPECT_RATIO = 1.4; //height = width * 1.4
        //private static final int MIN_CARD_WIDTH = 70;
        //private static final int MAX_CARD_WIDTH = 150;


        //current dynamically calculated dimensions
        private int cardWidth = 110;
        private int cardHeight = 154;
        private int cardGap = 10;

        public GamePanel(Blackjack game){
            this.game = game;
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
            if (!stayButton.isEnabled()) {
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
                if (!stayButton.isEnabled()) {
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





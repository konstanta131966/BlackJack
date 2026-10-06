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
    private GamePanel gamePanel;
    private JPanel buttonPanel = new JPanel();
    private JButton hitButton = new JButton("Hit");
    private JButton stayButton = new JButton("Hold");
    private JButton dealButton = new JButton("Deal");
    private JButton fullscreenButton = new JButton("Fullscreen (F11)");
    private Blackjack game; //declaring the game variable

    public BlackjackGUI(){
        game = new Blackjack(); // GUI connects to the backend
        frame = new JFrame("BlackJack 21");

        //declaring the gamepanel
        gamePanel = new GamePanel(game , () -> {
            dealButton.setVisible(true);
            dealButton.setEnabled(true);
        });
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
        stayButton.addActionListener(e -> {
            hitButton.setEnabled(false);
            stayButton.setEnabled(false);
            game.dealerDraw();
            gamePanel.showResults();
        });

        dealButton.setFocusable(false);
        dealButton.setVisible(false);//hidden at first
        buttonPanel.add(dealButton);
        dealButton.addActionListener(e-> {
            dealButton.setVisible(false);
            hitButton.setEnabled(true);
            stayButton.setEnabled(true);

            game.startGame();
            gamePanel.resetResults();
            gamePanel.repaint();
        });

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

}


       





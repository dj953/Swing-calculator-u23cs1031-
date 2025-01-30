package calculator.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorGUI extends JFrame {
    private JTextField display;
    private double result = 0;
    private String lastCommand = "=";
    private boolean start = true;
    private Timer fadeTimer;
    
    // Custom colors
    private final Color DARK_GRAY = new Color(51, 51, 51);
    private final Color LIGHT_GRAY = new Color(64, 64, 64);
    private final Color ORANGE = new Color(255, 149, 0);
    private final Color DISPLAY_BG = new Color(30, 30, 30);
    private final Color NUMBER_BTN_COLOR = new Color(75, 75, 75);
    private final Color OPERATOR_BTN_COLOR = new Color(102, 102, 102);
    private final Color BTN_TEXT_COLOR = Color.WHITE;
    
    public CalculatorGUI() {
        initializeUI();
        setupFadeTimer();
    }
    
    private void setupFadeTimer() {
        fadeTimer = new Timer(50, null);
        fadeTimer.setRepeats(true);
    }
    
    private void initializeUI() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(DARK_GRAY);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(DARK_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Enhanced display with animation support
        display = new JTextField("0") {
            private float alpha = 1.0f;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                super.paintComponent(g2);
                g2.dispose();
            }
            
            public void setAlpha(float alpha) {
                this.alpha = alpha;
                repaint();
            }
        };
        
        display.setEditable(false);
        display.setBackground(DISPLAY_BG);
        display.setForeground(Color.WHITE);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("SF Pro Display", Font.PLAIN, 40));
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(DARK_GRAY);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        displayPanel.add(display);
        mainPanel.add(displayPanel, BorderLayout.NORTH);
        
        mainPanel.add(createButtonPanel(), BorderLayout.CENTER);
        add(mainPanel);
        
        setSize(350, 500);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 8, 8));
        buttonPanel.setBackground(DARK_GRAY);
        
        String[] buttonLabels = {
            "C", "±", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "←", "="
        };
        
        for (String buttonLabel : buttonLabels) {
            buttonPanel.add(createStyledButton(buttonLabel));
        }
        
        return buttonPanel;
    }
    
    private JButton createStyledButton(final String buttonLabel) {
        JButton button = new JButton(buttonLabel) {
            private float alpha = 1.0f;
            private Color currentBackground;
            
            {
                if (buttonLabel.matches("[0-9.]")) {
                    currentBackground = NUMBER_BTN_COLOR;
                } else if (buttonLabel.equals("=")) {
                    currentBackground = ORANGE;
                } else {
                    currentBackground = OPERATOR_BTN_COLOR;
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw animated background
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(currentBackground);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw text with shadow
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawString(getText(), 2, getHeight() / 2 + 2);
                g2.setColor(BTN_TEXT_COLOR);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);
                
                g2.dispose();
            }
            
            public void setAlpha(float alpha) {
                this.alpha = alpha;
                repaint();
            }
            
            public void setCurrentBackground(Color color) {
                this.currentBackground = color;
                repaint();
            }
        };
        
        // Style the button
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 24));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        
        // Add press animation
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                animatePress((JButton)e.getSource());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                animateRelease((JButton)e.getSource());
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton btn = (JButton)e.getSource();
                Color baseColor = buttonLabel.matches("[0-9.]") ? NUMBER_BTN_COLOR :
                                buttonLabel.equals("=") ? ORANGE : OPERATOR_BTN_COLOR;
                ((JButton)e.getSource()).setBackground(baseColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                Color baseColor = buttonLabel.matches("[0-9.]") ? NUMBER_BTN_COLOR :
                                buttonLabel.equals("=") ? ORANGE : OPERATOR_BTN_COLOR;
                ((JButton)e.getSource()).setBackground(baseColor);
            }
        });
        
        // Add action listeners
        String finalLabel = buttonLabel.replace("÷", "/").replace("×", "*");
        if (finalLabel.matches("[0-9.]")) {
            button.addActionListener(e -> {
                animateDisplay();
                handleNumber(finalLabel);
            });
        } else {
            button.addActionListener(e -> {
                animateDisplay();
                handleCommand(finalLabel);
            });
        }
        
        return button;
    }
    
    private void animatePress(JButton button) {
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 22)); // Slightly smaller
        button.setBounds(button.getX() + 1, button.getY() + 1, 
                        button.getWidth() - 2, button.getHeight() - 2); // Slight shift
    }
    
    private void animateRelease(JButton button) {
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 24)); // Original size
        button.setBounds(button.getX() - 1, button.getY() - 1, 
                        button.getWidth() + 2, button.getHeight() + 2); // Original position
    }
    
    private void animateDisplay() {
        final float[] alpha = {1.0f};
        fadeTimer.stop();
        fadeTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha[0] -= 0.1f;
                if (alpha[0] <= 0.5f) {
                    alpha[0] = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
                ((JTextField)display).repaint();
            }
        });
        fadeTimer.start();
    }
    
    // ... [rest of the methods remain the same: handleNumber, handleCommand, calculate, etc.]
    private void handleNumber(String number) {
        if (start) {
            display.setText(number);
            start = false;
        } else {
            display.setText(display.getText() + number);
        }
    }
    
    private void handleCommand(String command) {
        switch (command) {
            case "C":
                result = 0;
                lastCommand = "=";
                display.setText("0");
                start = true;
                break;
            case "←":
                handleBackspace();
                break;
            case "±":
                handleNegate();
                break;
            default:
                if (!start) {
                    calculate(Double.parseDouble(display.getText()));
                    lastCommand = command;
                    start = true;
                }
                break;
        }
    }
    
    private void calculate(double x) {
        switch (lastCommand) {
            case "+": result += x; break;
            case "-": result -= x; break;
            case "*": result *= x; break;
            case "/": 
                if (x != 0) {
                    result /= x;
                } else {
                    display.setText("Error");
                    start = true;
                    return;
                }
                break;
            case "%": 
                if (x != 0) {
                    result %= x;
                } else {
                    display.setText("Error");
                    start = true;
                    return;
                }
                break;
            case "=": result = x; break;
        }
        
        String resultStr = String.valueOf(result);
        if (resultStr.endsWith(".0")) {
            resultStr = resultStr.substring(0, resultStr.length() - 2);
        }
        display.setText(resultStr);
    }
    
    private void handleBackspace() {
        String currentText = display.getText();
        if (currentText.length() > 0) {
            display.setText(currentText.substring(0, currentText.length() - 1));
            if (display.getText().isEmpty()) {
                display.setText("0");
                start = true;
            }
        }
    }
    
    private void handleNegate() {
        double value = Double.parseDouble(display.getText());
        display.setText(String.valueOf(-value));
    }
}
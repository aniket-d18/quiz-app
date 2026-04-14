package com.quiz.gui;

import com.quiz.model.Student;
import com.quiz.service.QuizEngine;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Full-screen gradient login screen with glassmorphism card.
 */
public class LoginPanel extends JPanel {

    private final MainFrame  frame;
    private final QuizEngine engine = new QuizEngine();

    private JTextField nameField;
    private JTextField rollField;
    private JLabel     errorLabel;
    private JButton    startBtn;

    public LoginPanel(MainFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new GridBagLayout()); // PERFECTLY centers the card
        buildUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Gradient background trickles down from MainFrame
    }

    // ── UI construction ────────────────────────────────────────────────────────

    private void buildUI() {
        JPanel card = new GlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(44, 50, 44, 50));
        card.setPreferredSize(new Dimension(500, 580));

        // ── Branding ──
        JLabel appName = centeredLabel("QUIZ APP", new Font("Segoe UI", Font.BOLD, 52), MainFrame.ACCENT);
        JLabel tagLine = centeredLabel("SY B.Tech  •  Java Mini Project",
                new Font("Segoe UI", Font.PLAIN, 13), MainFrame.TEXT_MUTED);

        // Thin gradient divider (painted)
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, MainFrame.ACCENT, getWidth(), 0, MainFrame.VIOLET));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        divider.setPreferredSize(new Dimension(0, 2));

        // ── Form fields ──
        JPanel formContainer = new JPanel();
        formContainer.setOpaque(false);
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setAlignmentX(CENTER_ALIGNMENT);
        formContainer.setMaximumSize(new Dimension(320, 200));

        JLabel nameLabel = fieldLabel("Full Name");
        nameField = darkGlowField("Enter your full name");

        JLabel rollLabel = fieldLabel("Roll Number");
        rollField = darkGlowField("eg. 22510");

        formContainer.add(nameLabel);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(nameField);
        formContainer.add(Box.createVerticalStrut(15));
        formContainer.add(rollLabel);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(rollField);

        // ── Error ──
        errorLabel = new JLabel(" ");
        errorLabel.setFont(MainFrame.FONT_SMALL);
        errorLabel.setForeground(MainFrame.DANGER);
        errorLabel.setAlignmentX(CENTER_ALIGNMENT);

        // ── Info chips ──
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(CENTER_ALIGNMENT);
        chips.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        chips.add(chip("20 Questions", MainFrame.ACCENT));
        chips.add(chip("30 Minutes",   MainFrame.VIOLET));
        chips.add(chip("No Negatives", MainFrame.SUCCESS));

        // ── Start button ──
        startBtn = new GradientButton("  START QUIZ  ", MainFrame.ACCENT, MainFrame.VIOLET);
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        startBtn.setAlignmentX(CENTER_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(320, 50)); 
        startBtn.addActionListener(e -> handleStart());

        nameField.addActionListener(e -> rollField.requestFocus());
        rollField.addActionListener(e -> handleStart());

        // ── Hint ──
        JLabel hint = centeredLabel("Press Ctrl+Shift+Q to exit (admin only)",
                new Font("Segoe UI", Font.ITALIC, 11), MainFrame.TEXT_DIM);

        // ── Assemble ──
        card.add(appName);
        card.add(Box.createVerticalStrut(4));
        card.add(tagLine);
        card.add(Box.createVerticalStrut(22));
        card.add(divider);
        card.add(Box.createVerticalStrut(28));
        card.add(formContainer);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(chips);
        card.add(Box.createVerticalStrut(25));
        card.add(startBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(hint);

        add(card);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private JLabel centeredLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(MainFrame.FONT_LABEL);
        l.setForeground(MainFrame.TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(320, 20));
        return l;
    }

    private JTextField darkGlowField(String placeholder) {
        JTextField f = new JTextField(20);
        f.setText(placeholder);
        f.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        f.setForeground(MainFrame.TEXT_DIM);
        f.setBackground(new Color(20, 15, 45, 180)); // Deep dark transluscent background
        f.setCaretColor(MainFrame.ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MainFrame.BORDER, 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        f.setMaximumSize(new Dimension(320, 48));
        f.setPreferredSize(new Dimension(320, 48));
        f.setAlignmentX(LEFT_ALIGNMENT);

        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                    f.setForeground(MainFrame.TEXT_PRIMARY);
                }
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(MainFrame.ACCENT, 2, true),
                        BorderFactory.createEmptyBorder(11, 13, 11, 13)));
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().trim().isEmpty()) {
                    f.setText(placeholder);
                    f.setFont(new Font("Segoe UI", Font.ITALIC, 15));
                    f.setForeground(MainFrame.TEXT_DIM);
                }
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(MainFrame.BORDER, 1, true),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)));
            }
        });
        return f;
    }

    private JPanel chip(String text, Color color) {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        c.setOpaque(false);
        c.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(color);
        c.add(l);
        return c;
    }

    // ── Action ────────────────────────────────────────────────────────────────

    private void handleStart() {
        String name = nameField.getText().trim();
        String roll = rollField.getText().trim();
        if (name.isEmpty()) { errorLabel.setText("Please enter your full name."); return; }
        if (roll.isEmpty()) { errorLabel.setText("Please enter your roll number."); return; }
        errorLabel.setText(" ");
        startBtn.setEnabled(false);

        new SwingWorker<Void, Void>() {
            private String err;
            protected Void doInBackground() {
                try { engine.startSession(new Student(name, roll)); }
                catch (Exception ex) { err = ex.getMessage(); }
                return null;
            }
            protected void done() {
                startBtn.setEnabled(true);
                if (err != null) { errorLabel.setText("Error loading questions: " + err); }
                else {
                    frame.getQuizPanel().startQuiz(engine);
                    frame.showCard(MainFrame.CARD_QUIZ);
                }
            }
        }.execute();
    }

    // ── Reusable inner UI components ──────────────────────────────────────────

    /** Glassmorphism floating card suitable for Dark Theme */
    public static class GlassCard extends JPanel {
        public GlassCard() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Shadow
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRoundRect(8, 8, getWidth()-8, getHeight()-8, 26, 26);
            // Glass background
            g2.setColor(MainFrame.SURFACE);
            g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 24, 24);
            // Border
            g2.setColor(new Color(255, 255, 255, 30));
            g2.setStroke(new java.awt.BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth()-7, getHeight()-7, 24, 24);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Gradient horizontal button */
    public static class GradientButton extends JButton {
        private final Color c1, c2;
        private boolean hov;

        public GradientButton(String text, Color c1, Color c2) {
            super(text);
            this.c1 = c1; this.c2 = c2;
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color a = hov ? c1.brighter() : c1;
            Color b = hov ? c2.brighter() : c2;
            g2.setPaint(new GradientPaint(0, 0, a, getWidth(), 0, b));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            
            // Inner highlight outline
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new java.awt.BasicStroke(1f));
            g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 14, 14);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

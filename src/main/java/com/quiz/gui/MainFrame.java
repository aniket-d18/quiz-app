package com.quiz.gui;

import com.quiz.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Full-screen kiosk window.
 * • Reverted to Dark Theme (but fresher & more vibrant)
 * • Re-grabs focus if user Alt+Tabs (exam lockdown)
 * • Ctrl+Shift+Q = admin exit
 */
public class MainFrame extends JFrame {

    public static final String CARD_LOGIN  = "LOGIN";
    public static final String CARD_QUIZ   = "QUIZ";
    public static final String CARD_RESULT = "RESULT";

    // ── Fresh Vibrant Dark Theme ──────────────────────────────────────────────
    // A fresh, modern, deep dark gradient (no plain black/grey)
    public static final Color BG_TOP         = new Color(15,  12,  41);   // Very dark indigo
    public static final Color BG_BOTTOM      = new Color(48,  43,  99);   // Deep rich violet

    // Surfaces (Glassmorphic)
    public static final Color SURFACE        = new Color(30,  25,  65, 230);
    public static final Color SURFACE_HOVER  = new Color(45,  40,  85, 240);
    public static final Color HEADER_BG      = new Color(20,  15,  50, 240);

    // Vibrant Accents (Stand out beautifully on dark)
    public static final Color ACCENT         = new Color(56, 189, 248);  // Fresh sky blue
    public static final Color VIOLET         = new Color(167, 139, 250); // Fresh violet
    public static final Color CYAN           = new Color(45, 212, 191);  // Fresh teal

    // Option-badge colours
    public static final Color OPT_A          = new Color(59, 130, 246);
    public static final Color OPT_B          = new Color(139, 92, 246);
    public static final Color OPT_C          = new Color(16, 185, 129);
    public static final Color OPT_D          = new Color(245, 158, 11);
    public static final Color[] OPT_COLORS   = { OPT_A, OPT_B, OPT_C, OPT_D };

    // Status colours
    public static final Color SUCCESS        = new Color(52, 211, 153);
    public static final Color DANGER         = new Color(248, 113, 113);
    public static final Color WARNING        = new Color(251, 191,  36);

    // Text hierarchy
    public static final Color TEXT_PRIMARY   = new Color(248, 250, 252); // pure bright white-blue
    public static final Color TEXT_MUTED     = new Color(148, 163, 184); // soft slate
    public static final Color TEXT_DIM       = new Color(100, 116, 139); // deep slate

    // Borders
    public static final Color BORDER         = new Color(71, 85, 105, 120);
    public static final Color BORDER_BRIGHT  = new Color(100, 116, 139, 180);

    // Typography
    public static final Font FONT_BIG    = new Font("Segoe UI", Font.BOLD, 36);
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MONO   = new Font("Consolas", Font.BOLD, 22);

    // ── Layout ────────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel;

    private LoginPanel  loginPanel;
    private QuizPanel   quizPanel;
    private ResultPanel resultPanel;

    public MainFrame() {
        super(Constants.APP_TITLE);

        // ── Kiosk setup ───────────────────────────────────────────────────────
        setUndecorated(true);                           // no title bar
        setExtendedState(JFrame.MAXIMIZED_BOTH);        // full screen
        setAlwaysOnTop(true);                           // stay on top
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  // block Alt+F4

        // Gradient root panel
        cardPanel = new JPanel(cardLayout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);

        initComponents();

        // ── Focus lock: re-grab focus if user Alt+Tabs ────────────────────────
        addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) {
                SwingUtilities.invokeLater(() -> { toFront(); requestFocus(); });
            }
            public void windowGainedFocus(WindowEvent e) {}
        });

        // ── Admin exit: Ctrl + Shift + Q ──────────────────────────────────────
        KeyStroke exitKey = KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(exitKey, "ADMIN_EXIT");
        getRootPane().getActionMap().put("ADMIN_EXIT", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int ok = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Exit quiz application?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) System.exit(0);
            }
        });

        setVisible(true);
    }

    private void initComponents() {
        loginPanel  = new LoginPanel(this);
        quizPanel   = new QuizPanel(this);
        resultPanel = new ResultPanel(this);

        cardPanel.add(loginPanel,  CARD_LOGIN);
        cardPanel.add(quizPanel,   CARD_QUIZ);
        cardPanel.add(resultPanel, CARD_RESULT);

        setContentPane(cardPanel);
        showCard(CARD_LOGIN);
    }

    public void showCard(String card) {
        cardLayout.show(cardPanel, card);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public LoginPanel  getLoginPanel()  { return loginPanel;  }
    public QuizPanel   getQuizPanel()   { return quizPanel;   }
    public ResultPanel getResultPanel() { return resultPanel; }
}

package com.quiz.gui;

import com.quiz.model.Question;
import com.quiz.model.Result;
import com.quiz.service.QuizEngine;
import com.quiz.util.Constants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

public class QuizPanel extends JPanel {

    private final MainFrame frame;
    private QuizEngine engine;
    private int timeLeft;
    private Timer countdownTimer;

    private boolean[] markedForReview;

    private JLabel qCounterLabel;
    private JLabel timerLabel;
    private JProgressBar progressBar;

    private JLabel     categoryBadge;
    private JLabel     questionNumLabel;
    private JLabel     questionText;
    private OptionCard[] optionCards;
    private int          selectedIdx = -1;

    private JButton[] paletteButtons;
    private JPanel    paletteGrid;

    private JButton prevBtn;
    private JButton nextBtn;
    private JButton reviewBtn;
    private JButton submitBtn;

    public QuizPanel(MainFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new BorderLayout());
        buildUI();
    }

    public void startQuiz(QuizEngine eng) {
        this.engine   = eng;
        this.timeLeft = Constants.QUIZ_DURATION_SECONDS;
        this.markedForReview = new boolean[eng.getTotalQuestions()];
        rebuildPalette();
        engine.navigateTo(0);
        refreshQuestion();
        startTimer();
    }

    private void buildUI() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(MainFrame.HEADER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight()-2, MainFrame.ACCENT,
                        getWidth(), getHeight()-2, MainFrame.VIOLET));
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setPreferredSize(new Dimension(0, 68));
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        JLabel appTitle = new JLabel("QUIZ APP");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(MainFrame.ACCENT);

        qCounterLabel = new JLabel("Q 1 of 20", SwingConstants.CENTER);
        qCounterLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        qCounterLabel.setForeground(MainFrame.TEXT_MUTED);

        timerLabel = new JLabel("30:00", SwingConstants.RIGHT);
        timerLabel.setFont(MainFrame.FONT_MONO);
        timerLabel.setForeground(MainFrame.SUCCESS);

        hdr.add(appTitle,     BorderLayout.WEST);
        hdr.add(qCounterLabel,BorderLayout.CENTER);
        hdr.add(timerLabel,   BorderLayout.EAST);

        progressBar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255,255,255,20));
                g2.fillRect(0, 0, getWidth(), getHeight());
                int filled = (int)(getWidth() * getValue() / 100.0);
                g2.setPaint(new GradientPaint(0,0, MainFrame.ACCENT, filled, 0, MainFrame.VIOLET));
                g2.fillRect(0, 0, filled, getHeight());
                g2.dispose();
            }
        };
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 4));

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(hdr,         BorderLayout.CENTER);
        north.add(progressBar, BorderLayout.SOUTH);
        return north;
    }

    private JPanel buildCenter() {
        JPanel root = new JPanel(new BorderLayout(24, 0));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(24, 40, 12, 40));

        JPanel qArea = new JPanel();
        qArea.setOpaque(false);
        qArea.setLayout(new BoxLayout(qArea, BoxLayout.Y_AXIS));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(LEFT_ALIGNMENT);

        questionNumLabel = badgeLabel("Q1", MainFrame.ACCENT);
        categoryBadge    = badgeLabel("Category", MainFrame.VIOLET);
        badgeRow.add(questionNumLabel);
        badgeRow.add(categoryBadge);

        questionText = new JLabel();
        questionText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        questionText.setForeground(MainFrame.TEXT_PRIMARY);
        questionText.setAlignmentX(LEFT_ALIGNMENT);

        JPanel optPanel = new JPanel();
        optPanel.setOpaque(false);
        optPanel.setLayout(new BoxLayout(optPanel, BoxLayout.Y_AXIS));
        optPanel.setAlignmentX(LEFT_ALIGNMENT);

        optionCards = new OptionCard[4];
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            optionCards[i] = new OptionCard(i, letters[i], "");
            optionCards[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    selectOption(idx);
                }
            });
            optPanel.add(optionCards[i]);
            if (i < 3) optPanel.add(Box.createVerticalStrut(10));
        }

        qArea.add(badgeRow);
        qArea.add(Box.createVerticalStrut(18));
        qArea.add(questionText);
        qArea.add(Box.createVerticalStrut(30));
        qArea.add(optPanel);

        JPanel sidebarContainer = new JPanel(new BorderLayout());
        sidebarContainer.setOpaque(false);
        sidebarContainer.add(buildSidebar(), BorderLayout.NORTH);

        root.add(qArea,            BorderLayout.CENTER);
        root.add(sidebarContainer, BorderLayout.EAST);
        return root;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new LoginPanel.GlassCard();
        sidebar.setLayout(new BorderLayout(0, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
        sidebar.setPreferredSize(new Dimension(280, 230));

        JLabel title = new JLabel("Question Palette", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(MainFrame.TEXT_MUTED);

        paletteGrid = new JPanel(new GridLayout(0, 5, 8, 8));
        paletteGrid.setBorder(BorderFactory.createEmptyBorder(8,0,8,0));
        paletteGrid.setOpaque(false);

        JPanel legend = new JPanel(new GridLayout(2, 2, 0, 4));
        legend.setOpaque(false);
        legend.add(legendItem(MainFrame.ACCENT,  "Current"));
        legend.add(legendItem(MainFrame.SUCCESS, "Answered"));
        legend.add(legendItem(MainFrame.VIOLET,  "Review"));
        legend.add(legendItem(MainFrame.SURFACE, "Pending"));

        sidebar.add(title,       BorderLayout.NORTH);
        sidebar.add(paletteGrid, BorderLayout.CENTER);
        sidebar.add(legend,      BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel legendItem(Color color, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 10));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(MainFrame.TEXT_MUTED);
        row.add(dot);
        row.add(lbl);
        return row;
    }

    private JLabel badgeLabel(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 160));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(color);
        l.setOpaque(false);
        l.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        return l;
    }

    private JPanel buildFooter() {
        JPanel ftr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(MainFrame.HEADER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, MainFrame.ACCENT, getWidth(), 0, MainFrame.VIOLET));
                g2.fillRect(0, 0, getWidth(), 2);
                g2.dispose();
            }
        };
        ftr.setOpaque(false);
        ftr.setPreferredSize(new Dimension(0, 70));
        ftr.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        prevBtn   = navButton("← Previous", false, MainFrame.TEXT_MUTED);
        nextBtn   = navButton("Next →",     true,  MainFrame.ACCENT);
        reviewBtn = navButton("⚑ Mark Review", false, MainFrame.VIOLET);

        submitBtn = new LoginPanel.GradientButton("SUBMIT QUIZ", MainFrame.SUCCESS, new Color(22,163,74));
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        submitBtn.setPreferredSize(new Dimension(160, 42));

        prevBtn.addActionListener(e -> navigate(-1));
        nextBtn.addActionListener(e -> navigate(+1));
        reviewBtn.addActionListener(e -> toggleReview());
        submitBtn.addActionListener(e -> customSubmitDialog());

        JPanel left  = new JPanel(new FlowLayout(FlowLayout.LEFT,  0, 13));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 13));
        left.setOpaque(false); right.setOpaque(false);

        left.add(prevBtn);
        
        right.add(reviewBtn);
        right.add(Box.createHorizontalStrut(12));
        right.add(nextBtn);
        right.add(Box.createHorizontalStrut(12));
        right.add(submitBtn);

        ftr.add(left,  BorderLayout.WEST);
        ftr.add(right, BorderLayout.EAST);
        return ftr;
    }

    private JButton navButton(String text, boolean primary, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = primary 
                    ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 35)
                    : new Color(255,255,255,15);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                Color bc = primary ? color : MainFrame.BORDER;
                g2.setColor(bc);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(color);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 42));
        return btn;
    }

    private void rebuildPalette() {
        paletteGrid.removeAll();
        int total = engine.getTotalQuestions();
        paletteButtons = new JButton[total];
        for (int i = 0; i < total; i++) {
            final int idx = i;
            JButton btn = new JButton(String.valueOf(i + 1)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(getForeground());
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(getText(), x, y);
                    g2.dispose();
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(36, 32));
            btn.addActionListener(e -> { engine.navigateTo(idx); refreshQuestion(); });
            paletteButtons[i] = btn;
            paletteGrid.add(btn);
        }
        updatePalette();
        paletteGrid.revalidate();
        paletteGrid.repaint();
    }

    private void updatePalette() {
        if (paletteButtons == null) return;
        int curr = engine.getCurrentIndex();
        for (int i = 0; i < paletteButtons.length; i++) {
            String ans = engine.getAnswer(i);
            boolean isMarked = markedForReview[i];
            
            Color bg;
            Color fg = Color.WHITE;
            
            if (i == curr) {
                bg = MainFrame.ACCENT;
            } else if (isMarked) {
                bg = MainFrame.VIOLET;
            } else if (ans != null) {
                bg = MainFrame.SUCCESS;
            } else {
                bg = MainFrame.SURFACE; 
                fg = MainFrame.TEXT_MUTED;
            }
            paletteButtons[i].setBackground(bg);
            paletteButtons[i].setForeground(fg);
        }
    }

    private void refreshQuestion() {
        int  idx   = engine.getCurrentIndex();
        int  total = engine.getTotalQuestions();
        Question q = engine.getCurrentQuestion();

        qCounterLabel.setText("Q " + (idx + 1) + " of " + total);
        progressBar.setValue((int) Math.round(idx * 100.0 / total));

        questionNumLabel.setText("Q" + (idx + 1));
        categoryBadge.setText(q.getCategory());

        questionText.setText("<html><body style='width:560px; line-height:1.5'>"
                + escHtml(q.getQuestionText()) + "</body></html>");

        String   saved   = engine.getAnswer(idx);
        String[] opts    = q.getAllOptions();
        String[] letters = {"A","B","C","D"};

        selectedIdx = -1;
        for (int i = 0; i < 4; i++) {
            optionCards[i].setText(opts[i]);
            boolean sel = letters[i].equals(saved);
            optionCards[i].setSelected(sel);
            if (sel) selectedIdx = i;
        }

        prevBtn.setEnabled(idx > 0);
        boolean last = (idx == total - 1);
        nextBtn.setVisible(!last);
        submitBtn.setVisible(last);
        
        if (markedForReview[idx]) {
            reviewBtn.setText("⚑ Unmark");
            reviewBtn.setForeground(MainFrame.TEXT_MUTED);
        } else {
            reviewBtn.setText("⚑ Mark Review");
            reviewBtn.setForeground(MainFrame.VIOLET);
        }

        updatePalette();
    }

    private void selectOption(int idx) {
        selectedIdx = idx;
        String[] letters = {"A","B","C","D"};
        engine.setAnswer(engine.getCurrentIndex(), letters[idx]);
        for (int i = 0; i < 4; i++) optionCards[i].setSelected(i == idx);
        
        markedForReview[engine.getCurrentIndex()] = false;
        if (markedForReview[engine.getCurrentIndex()]) {
            reviewBtn.setText("⚑ Unmark");
        } else {
            reviewBtn.setText("⚑ Mark Review");
            reviewBtn.setForeground(MainFrame.VIOLET);
        }
        
        updatePalette();
    }
    
    private void toggleReview() {
        int idx = engine.getCurrentIndex();
        markedForReview[idx] = !markedForReview[idx];
        refreshQuestion();
    }

    private void navigate(int delta) {
        int next = engine.getCurrentIndex() + delta;
        if (next >= 0 && next < engine.getTotalQuestions()) {
            engine.navigateTo(next);
            refreshQuestion();
        }
    }

    private void startTimer() {
        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> tick());
        countdownTimer.start();
    }

    private void tick() {
        timeLeft--;
        int m = timeLeft / 60, s = timeLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", m, s));
        if      (timeLeft <= 300) timerLabel.setForeground(MainFrame.DANGER);
        else if (timeLeft <= 600) timerLabel.setForeground(MainFrame.WARNING);
        if (timeLeft <= 0) { countdownTimer.stop(); doSubmit(); }
    }

    private void customSubmitDialog() {
        JDialog dialog = new JDialog(frame, "Submit", true);
        dialog.setUndecorated(true);
        dialog.getRootPane().setOpaque(false);
        dialog.getContentPane().setBackground(new Color(0,0,0,0));
        dialog.setBackground(new Color(0,0,0,0));

        JPanel panel = new LoginPanel.GlassCard();
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel msg = new JLabel("<html><h2 style='margin:0;color:#f8fafc'>Submit Quiz?</h2>"
                + "<p style='color:#cbd5e1;font-weight:normal;margin-top:8px'>Unanswered questions will be scored zero. Are you sure you are ready?</p></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btns.setOpaque(false);

        JButton cancel = new JButton("Cancel") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cancel.setForeground(MainFrame.TEXT_PRIMARY);
        cancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancel.setContentAreaFilled(false);
        cancel.setBorderPainted(false);
        cancel.setFocusPainted(false);
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.setPreferredSize(new Dimension(100, 38));
        cancel.addActionListener(e -> dialog.dispose());

        JButton ok = new LoginPanel.GradientButton("Submit", MainFrame.SUCCESS, new Color(22,163,74));
        ok.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ok.setPreferredSize(new Dimension(120, 38));
        ok.addActionListener(e -> { dialog.dispose(); doSubmit(); });

        btns.add(cancel);
        btns.add(ok);

        panel.add(msg, BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void doSubmit() {
        if (countdownTimer != null) countdownTimer.stop();
        try {
            Result result = engine.finish();
            frame.getResultPanel().showResult(result, engine.getLastFeedback());
            frame.showCard(MainFrame.CARD_RESULT);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Save error: " + ex.getMessage());
        }
    }

    private String escHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    static class OptionCard extends JPanel {
        private static final int ARC = 14;
        private final int optIdx;
        private final Color badgeColor;
        private final JLabel letterLbl;
        private final JLabel textLbl;
        private boolean selected;
        private boolean hovered;

        OptionCard(int idx, String letter, String text) {
            this.optIdx     = idx;
            this.badgeColor = MainFrame.OPT_COLORS[idx];
            setOpaque(false);
            setLayout(new BorderLayout(14, 0));
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 20));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            letterLbl = new JLabel(letter, SwingConstants.CENTER);
            letterLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
            letterLbl.setForeground(Color.WHITE);
            letterLbl.setPreferredSize(new Dimension(38, 38));
            letterLbl.setMinimumSize(new Dimension(38, 38));
            letterLbl.setMaximumSize(new Dimension(38, 38));

            textLbl = new JLabel(text);
            textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            textLbl.setForeground(MainFrame.TEXT_PRIMARY);

            add(letterLbl, BorderLayout.WEST);
            add(textLbl,   BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            });
        }

        public void setText(String t) { textLbl.setText(t); }

        public void setSelected(boolean s) {
            this.selected = s;
            textLbl.setForeground(s ? Color.WHITE : MainFrame.TEXT_PRIMARY);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg;
            if (selected) {
                bg = new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 60);
            } else if (hovered) {
                bg = MainFrame.SURFACE_HOVER;
            } else {
                bg = MainFrame.SURFACE; // Dark
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, ARC, ARC);

            Color borderCol = selected ? badgeColor : (hovered ? MainFrame.BORDER_BRIGHT : new Color(255,255,255,30));
            g2.setColor(borderCol);
            g2.setStroke(new BasicStroke(selected ? 2f : 1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, ARC, ARC);

            int bx = 16, by = (getHeight() - 38) / 2;
            Color badgeBg = selected ? badgeColor : new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 200);
            g2.setColor(badgeBg);
            g2.fillRoundRect(bx, by, 38, 38, 10, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}

package com.quiz.gui;

import com.quiz.model.Feedback;
import com.quiz.model.Question;
import com.quiz.model.Result;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * Full-screen dark results panel with:
 *  • Bright circular score arc
 *  • Left-aligned category breakdown table
 *  • Left-aligned Answer review cards
 *  • Interactive table hover states
 */
public class ResultPanel extends JPanel {

    private final MainFrame frame;

    // Dynamic widgets
    private ScoreCircle  scoreCircle;
    private JLabel       gradeLabel;
    private JLabel       pctLabel;
    private JLabel       feedbackLabel;
    private JPanel       catHolder;
    private JPanel       reviewHolder;

    public ResultPanel(MainFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new BorderLayout());
        buildShell();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void showResult(Result result, Feedback feedback) {
        scoreCircle.setValues(result.getScore(), result.getTotalQuestions(),
                result.getPercentage(), result.getGrade());

        gradeLabel.setText(feedback.getLabel() + "  —  Grade " + result.getGrade());
        gradeLabel.setForeground(gradeColor(result.getGrade()));

        pctLabel.setText(String.format("%.1f%%  (%d / %d correct)",
                result.getPercentage(), result.getScore(), result.getTotalQuestions()));

        feedbackLabel.setText("<html><div style='text-align:left;width:380px'>"
                + feedback.getMessage() + "</div></html>");

        catHolder.removeAll();
        catHolder.add(buildCatTable(result.getCategoryStats()), BorderLayout.WEST);
        catHolder.revalidate();

        reviewHolder.removeAll();
        buildReview(result.getQuestions(), result.getSelectedAnswers(), reviewHolder);
        reviewHolder.revalidate();
    }

    // ── Shell ─────────────────────────────────────────────────────────────────

    private void buildShell() {
        // Header
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(MainFrame.HEADER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight()-2, MainFrame.ACCENT,
                        getWidth(), getHeight()-2, MainFrame.CYAN));
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setPreferredSize(new Dimension(0, 68));
        // Push button away from edge
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 80)); 

        JLabel title = new JLabel("Quiz Results");
        title.setFont(MainFrame.FONT_TITLE);
        title.setForeground(MainFrame.TEXT_PRIMARY);

        JButton retake = new LoginPanel.GradientButton(" Retake Quiz ", MainFrame.ACCENT, MainFrame.CYAN);
        retake.setFont(new Font("Segoe UI", Font.BOLD, 14));
        retake.setPreferredSize(new Dimension(140, 40));
        retake.addActionListener(e -> frame.showCard(MainFrame.CARD_LOGIN));

        hdr.add(title,  BorderLayout.WEST);
        hdr.add(retake, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        // Scrollable body
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        // Force all children to be strictly left-aligned within the BoxLayout
        body.setAlignmentX(LEFT_ALIGNMENT);

        // ── Score hero row ──
        JPanel hero = buildHero();
        hero.setAlignmentX(LEFT_ALIGNMENT);
        body.add(hero);
        body.add(Box.createVerticalStrut(32));

        // ── Category table ──
        JLabel catTitle = sectionTitle("Category Breakdown");
        catTitle.setAlignmentX(LEFT_ALIGNMENT);
        body.add(catTitle);
        body.add(Box.createVerticalStrut(12));

        catHolder = new JPanel(new BorderLayout());
        catHolder.setOpaque(false);
        catHolder.setMaximumSize(new Dimension(650, 250)); // Bound width
        catHolder.setAlignmentX(LEFT_ALIGNMENT);
        body.add(catHolder);
        body.add(Box.createVerticalStrut(32));

        // ── Answer review ──
        JLabel reviewTitle = sectionTitle("Answer Review");
        reviewTitle.setAlignmentX(LEFT_ALIGNMENT);
        body.add(reviewTitle);
        body.add(Box.createVerticalStrut(12));

        reviewHolder = new JPanel();
        reviewHolder.setOpaque(false);
        reviewHolder.setLayout(new BoxLayout(reviewHolder, BoxLayout.Y_AXIS));
        reviewHolder.setAlignmentX(LEFT_ALIGNMENT);
        body.add(reviewHolder);
        body.add(Box.createVerticalStrut(32));

        // Using a FlowLayout wrapper to ensure the entire BoxLayout is anchored WEST.
        JPanel bodyWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 60, 32));
        bodyWrapper.setOpaque(false);
        bodyWrapper.add(body);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getVerticalScrollBar().setBackground(new Color(20, 15, 35));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHero() {
        JPanel hero = new LoginPanel.GlassCard();
        hero.setLayout(new GridBagLayout());
        hero.setMaximumSize(new Dimension(650, 220));
        hero.setPreferredSize(new Dimension(650, 220));

        scoreCircle  = new ScoreCircle();
        scoreCircle.setPreferredSize(new Dimension(160, 160));
        scoreCircle.setMinimumSize(new Dimension(160, 160));
        scoreCircle.setMaximumSize(new Dimension(160, 160));

        gradeLabel = new JLabel("", SwingConstants.LEFT);
        gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        gradeLabel.setForeground(MainFrame.SUCCESS);

        pctLabel = new JLabel("", SwingConstants.LEFT);
        pctLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pctLabel.setForeground(MainFrame.TEXT_MUTED);

        feedbackLabel = new JLabel("", SwingConstants.LEFT);
        feedbackLabel.setFont(MainFrame.FONT_BODY);
        feedbackLabel.setForeground(MainFrame.TEXT_DIM);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(gradeLabel);
        info.add(Box.createVerticalStrut(8));
        info.add(pctLabel);
        info.add(Box.createVerticalStrut(12));
        info.add(feedbackLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 24, 0, 32);
        gbc.gridx = 0; hero.add(scoreCircle, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1; hero.add(info, gbc);
        return hero;
    }

    // ── Category table ────────────────────────────────────────────────────────

    private JPanel buildCatTable(Map<String, int[]> stats) {
        String[] cols = {"Category", "Correct", "Attempted", "Score %"};
        Object[][] rows = new Object[stats.size()][4];
        int r = 0;
        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            int cor = e.getValue()[0], att = e.getValue()[1];
            double pct = att == 0 ? 0 : cor * 100.0 / att;
            rows[r++] = new Object[]{ e.getKey(), cor, att, String.format("%.0f%%", pct) };
        }

        DefaultTableModel mdl = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(mdl);
        styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(MainFrame.BORDER, 1, true));
        sp.getViewport().setBackground(MainFrame.SURFACE);
        sp.setPreferredSize(new Dimension(650, Math.min(220, stats.size() * 38 + 40)));

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(sp, BorderLayout.WEST);
        return p;
    }

    private void styleTable(JTable t) {
        t.setBackground(MainFrame.SURFACE);
        t.setForeground(MainFrame.TEXT_PRIMARY);
        t.setFont(MainFrame.FONT_BODY);
        t.setGridColor(MainFrame.BORDER);
        t.setRowHeight(38);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(99,179,237,40));
        t.setSelectionForeground(MainFrame.TEXT_PRIMARY);
        
        t.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                t.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        JTableHeader hdr = t.getTableHeader();
        hdr.setBackground(MainFrame.SURFACE_HOVER);
        hdr.setForeground(MainFrame.TEXT_MUTED);
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hdr.setBorder(new MatteBorder(0, 0, 1, 0, MainFrame.BORDER));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        center.setBackground(MainFrame.SURFACE);
        center.setForeground(MainFrame.TEXT_PRIMARY);
        for (int i = 1; i < t.getColumnCount(); i++)
            t.getColumnModel().getColumn(i).setCellRenderer(center);
    }

    // ── Answer review ─────────────────────────────────────────────────────────

    private void buildReview(List<Question> qs, List<String> answers, JPanel holder) {
        for (int i = 0; i < qs.size(); i++) {
            Question q      = qs.get(i);
            String   chosen = i < answers.size() ? answers.get(i) : null;
            boolean  ok     = q.isCorrect(chosen);

            Color borderCol = chosen == null ? MainFrame.BORDER
                            : ok            ? MainFrame.SUCCESS
                                            : MainFrame.DANGER;

            JPanel card = new JPanel();
            card.setBackground(MainFrame.SURFACE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(borderCol, 2, true),
                    BorderFactory.createEmptyBorder(16, 20, 16, 20)));
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setMaximumSize(new Dimension(850, 999)); // Restrict card width
            card.setAlignmentX(LEFT_ALIGNMENT);

            String prefix = chosen == null ? "[?]" : ok ? "[V]" : "[X]";
            JLabel qLbl = new JLabel("<html><b style='color:#38bdf8'>" + prefix + "  Q" + (i+1) + ".</b>  "
                    + escHtml(q.getQuestionText()) + "</html>");
            qLbl.setFont(MainFrame.FONT_BODY);
            qLbl.setForeground(MainFrame.TEXT_PRIMARY);
            qLbl.setAlignmentX(LEFT_ALIGNMENT);

            String yourText = chosen == null ? "Not answered" : chosen + ".  " + optText(q, chosen);
            JLabel yourLbl = new JLabel("Your answer:    " + yourText);
            yourLbl.setFont(MainFrame.FONT_SMALL);
            yourLbl.setForeground(ok ? MainFrame.SUCCESS : MainFrame.DANGER);
            yourLbl.setAlignmentX(LEFT_ALIGNMENT);

            card.add(qLbl);
            card.add(Box.createVerticalStrut(6));
            card.add(yourLbl);

            if (!ok) {
                JLabel corrLbl = new JLabel("Correct answer: "
                        + q.getCorrectOption() + ".  " + q.getCorrectAnswerText());
                corrLbl.setFont(MainFrame.FONT_SMALL);
                corrLbl.setForeground(MainFrame.CYAN);
                corrLbl.setAlignmentX(LEFT_ALIGNMENT);
                card.add(Box.createVerticalStrut(3));
                card.add(corrLbl);
            }

            holder.add(card);
            holder.add(Box.createVerticalStrut(12));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(MainFrame.TEXT_PRIMARY);
        return l;
    }

    private Color gradeColor(String g) {
        switch (g) {
            case "A": return MainFrame.SUCCESS;
            case "B": return MainFrame.ACCENT;
            case "C": return MainFrame.WARNING;
            case "D": return new Color(245,158,11); // Amber
            default:  return MainFrame.DANGER;
        }
    }

    private String optText(Question q, String opt) {
        if (opt == null) return "";
        switch (opt.toUpperCase()) {
            case "A": return q.getOptionA();
            case "B": return q.getOptionB();
            case "C": return q.getOptionC();
            case "D": return q.getOptionD();
            default:  return "";
        }
    }

    private String escHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    // ── ScoreCircle ─────────────────────────────────────────────────────────────

    static class ScoreCircle extends JComponent {
        private int score, total;
        private double pct;
        private String grade;

        void setValues(int score, int total, double pct, String grade) {
            this.score = score; this.total = total;
            this.pct = pct;     this.grade = grade;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (total == 0) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pad = 10;
            int d   = Math.min(getWidth(), getHeight()) - pad*2;
            int x   = (getWidth()  - d) / 2;
            int y   = (getHeight() - d) / 2;

            // Track ring (White transparent for dark theme)
            g2.setColor(new Color(255, 255, 255, 25));
            g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x, y, d, d);

            // Score arc 
            Color arcColor = gradeArcColor(grade);
            g2.setColor(arcColor);
            int angle = (int)(360 * pct / 100.0);
            g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(x, y, d, d, 90, -angle);

            // Inner text 
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2.setColor(MainFrame.TEXT_PRIMARY); // Bright White text
            FontMetrics fm = g2.getFontMetrics();
            String top = score + "/" + total;
            g2.drawString(top, (getWidth() - fm.stringWidth(top)) / 2,
                    getHeight() / 2 - 2 + fm.getAscent() / 4);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(arcColor);
            FontMetrics fm2 = g2.getFontMetrics();
            String sub = "Grade " + grade;
            g2.drawString(sub, (getWidth() - fm2.stringWidth(sub)) / 2,
                    getHeight() / 2 + fm2.getAscent() + 8);

            g2.dispose();
        }

        private Color gradeArcColor(String g) {
            if (g == null) return MainFrame.ACCENT;
            switch (g) {
                case "A": return MainFrame.SUCCESS;
                case "B": return MainFrame.ACCENT;
                case "C": return MainFrame.WARNING;
                case "D": return new Color(245,158,11); 
                default:  return MainFrame.DANGER;
            }
        }
    }
}

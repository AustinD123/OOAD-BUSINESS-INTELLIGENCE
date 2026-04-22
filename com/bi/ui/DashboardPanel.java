package com.bi.ui;

import com.bi.controller.DashboardController;
import com.bi.util.AnalysisResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.bi.ui.LoginPanel.*;

/**
 * View: Dashboard screen.
 * Shows KPI summary cards and a hand-drawn sales bar chart.
 * All data comes from DashboardController — no service calls here.
 */
public class DashboardPanel extends JPanel {

    private final DashboardController controller;

    public DashboardPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildCenter(),   BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);

        JLabel title = new JLabel("BI Dashboard");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(ACCENT);

        JLabel sub = new JLabel("Car Manufacturing ERP  ·  OREO Team");
        sub.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sub.setForeground(TXT_DIM);

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setBackground(BG_DARK);
        left.add(title);
        left.add(sub);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    // ── Center: cards row + chart ─────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setBackground(BG_DARK);
        center.add(buildCardsRow(), BorderLayout.NORTH);
        center.add(buildChartCard(), BorderLayout.CENTER);
        return center;
    }

    // ── KPI Summary Cards ─────────────────────────────────────────
    private JPanel buildCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(BG_DARK);

        AnalysisResult analysis = controller.getAnalysis();

        row.add(kpiCard("Total KPIs",         String.valueOf(controller.totalKPIs()),     ACCENT,  "All tracked metrics"));
        row.add(kpiCard("Achieved",            String.valueOf(controller.achievedKPIs()),  ACCENT2, "On or above target"));
        row.add(kpiCard("Not Achieved",        String.valueOf(controller.notAchievedKPIs()), RED_SOFT, "Below target"));
        row.add(kpiCard("Achievement Rate",    String.format("%.0f%%", controller.achievementRate()), new Color(255, 200, 60), "Overall performance"));

        return row;
    }

    private JPanel kpiCard(String title, String value, Color valueColor, String subtitle) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(18, 20, 18, 20)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lbl.setForeground(TXT_DIM);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Monospaced", Font.BOLD, 28));
        val.setForeground(valueColor);
        val.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel(subtitle);
        sub.setFont(new Font("Monospaced", Font.PLAIN, 10));
        sub.setForeground(new Color(60, 80, 110));
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(6));
        card.add(val);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        return card;
    }

    // ── Bar Chart Card ────────────────────────────────────────────
    private JPanel buildChartCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Monthly Sales Trend (Units)");
        title.setFont(new Font("Monospaced", Font.BOLD, 13));
        title.setForeground(ACCENT2);
        card.add(title, BorderLayout.NORTH);

        card.add(new BarChartCanvas(controller.getTrendMonths(), controller.getTrendSales()), BorderLayout.CENTER);
        return card;
    }

    // ── Inner bar-chart canvas (pure Swing, no external lib) ──────
    private static class BarChartCanvas extends JPanel {
        private final String[] months;
        private final double[] values;

        BarChartCanvas(String[] months, double[] values) {
            this.months = months;
            this.values = values;
            setBackground(BG_CARD);
            setPreferredSize(new Dimension(0, 220));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int padL = 60, padR = 20, padT = 20, padB = 40;
            int chartW = w - padL - padR;
            int chartH = h - padT - padB;

            double maxVal = 0;
            for (double v : values) if (v > maxVal) maxVal = v;

            int n      = months.length;
            int barW   = Math.max(8, (chartW / n) - 10);
            int gap    = (chartW - barW * n) / (n + 1);

            // grid lines
            g2.setColor(new Color(40, 55, 80));
            for (int i = 0; i <= 4; i++) {
                int y = padT + (int)(chartH * i / 4.0);
                g2.drawLine(padL, y, w - padR, y);
                g2.setColor(new Color(90, 110, 140));
                g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                g2.drawString(String.format("%.0f", maxVal * (4 - i) / 4), 2, y + 4);
                g2.setColor(new Color(40, 55, 80));
            }

            // bars
            for (int i = 0; i < n; i++) {
                int barH = (int)((values[i] / maxVal) * chartH);
                int x    = padL + gap + i * (barW + gap);
                int y    = padT + chartH - barH;

                // gradient bar
                GradientPaint gp = new GradientPaint(x, y, ACCENT, x, y + barH, new Color(0, 80, 160));
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, barW, barH, 4, 4);

                // month label
                g2.setColor(TXT_DIM);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                g2.drawString(months[i], x + barW / 2 - 8, h - padB + 14);

                // value label on bar top
                g2.setColor(TXT_MAIN);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 8));
                String vStr = String.format("%.0fk", values[i] / 1000);
                g2.drawString(vStr, x + barW / 2 - 10, y - 3);
            }

            // axis lines
            g2.setColor(BORDER_C);
            g2.drawLine(padL, padT, padL, padT + chartH);
            g2.drawLine(padL, padT + chartH, w - padR, padT + chartH);
        }
    }
}

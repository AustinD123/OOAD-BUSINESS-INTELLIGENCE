package com.bi.ui;

import com.bi.controller.ReportController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.bi.ui.LoginPanel.*;

/**
 * View: Report Generation screen.
 * Lets the user select a report type, date range, then generate and export.
 * All actions delegate to ReportController.
 */
public class ReportPanel extends JPanel {

    private final ReportController controller;

    private JComboBox<String> reportTypeBox;
    private JTextField        fromField, toField;
    private JTextArea         outputArea;

    public ReportPanel(ReportController controller) {
        this.controller = controller;
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildForm(),    BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        JLabel title = new JLabel("Report Generation");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(ACCENT);
        JLabel sub = new JLabel("Generate and export BI reports");
        sub.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sub.setForeground(TXT_DIM);
        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setBackground(BG_DARK);
        left.add(title); left.add(sub);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    // ── Form + Output ─────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setBackground(BG_DARK);

        // ── Config card ───────────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(20, 24, 20, 24)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        reportTypeBox = new JComboBox<>(new String[]{
                "Sales Report", "KPI Summary", "Production Report",
                "Finance Report", "HR Analytics Report"});
        styleCombo(reportTypeBox);

        fromField = styledField("YYYY-MM-DD");
        toField   = styledField("YYYY-MM-DD");

        g.gridx = 0; g.gridy = 0; g.weightx = 0; card.add(lbl("Report Type:"), g);
        g.gridx = 1; g.gridy = 0; g.weightx = 1; card.add(reportTypeBox, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0; card.add(lbl("From Date:"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 1; card.add(fromField, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0; card.add(lbl("To Date:"), g);
        g.gridx = 1; g.gridy = 2; g.weightx = 1; card.add(toField, g);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.add(accentBtn("⊞  Generate Report", ACCENT,   e -> generate()));
        btnRow.add(accentBtn("↓  Export PDF",       ACCENT2,  e -> exportPDF()));
        btnRow.add(accentBtn("↓  Export Excel",      new Color(255, 200, 60), e -> exportExcel()));

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2; g.insets = new Insets(14, 6, 6, 6);
        card.add(btnRow, g);

        // ── Output card ───────────────────────────────────────────
        JPanel outCard = new JPanel(new BorderLayout(0, 8));
        outCard.setBackground(BG_CARD);
        outCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel outTitle = new JLabel("Output");
        outTitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        outTitle.setForeground(ACCENT2);
        outCard.add(outTitle, BorderLayout.NORTH);

        outputArea = new JTextArea(6, 0);
        outputArea.setBackground(BG_INPUT);
        outputArea.setForeground(TXT_MAIN);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C, 1));
        outCard.add(scroll, BorderLayout.CENTER);

        wrap.add(card,    BorderLayout.NORTH);
        wrap.add(outCard, BorderLayout.CENTER);
        return wrap;
    }

    // ── Actions ───────────────────────────────────────────────────
    private void generate() {
        String type = (String) reportTypeBox.getSelectedItem();
        String from = fromField.getText().trim();
        String to   = toField.getText().trim();
        String result = controller.generateReport(type, from, to);
        outputArea.setText("✔ " + result);
    }

    private void exportPDF() {
        outputArea.setText(controller.exportPDF());
    }

    private void exportExcel() {
        outputArea.setText(controller.exportExcel());
    }

    // ── Helpers ───────────────────────────────────────────────────
    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.PLAIN, 11));
        l.setForeground(TXT_DIM);
        return l;
    }

    private JTextField styledField(String hint) {
        JTextField f = new JTextField();
        f.setBackground(BG_INPUT);
        f.setForeground(TXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setToolTipText(hint);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(5, 8, 5, 8)));
        return f;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(BG_INPUT);
        box.setForeground(TXT_MAIN);
        box.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private JButton accentBtn(String label, Color color, java.awt.event.ActionListener al) {
        JButton b = new JButton(label);
        b.setFont(new Font("Monospaced", Font.BOLD, 11));
        b.setBackground(color);
        b.setForeground(BG_DARK);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        b.addActionListener(al);
        return b;
    }
}

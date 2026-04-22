package com.bi.ui;

import com.bi.controller.KPIController;
import com.bi.models.KPI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

import static com.bi.ui.LoginPanel.*;

/**
 * View: KPI Table screen.
 * Displays KPIs in a styled JTable with a status filter.
 * All data sourced from KPIController.
 */
public class KPIPanel extends JPanel {

    private final KPIController controller;
    private DefaultTableModel tableModel;
    private JLabel recordCountLabel;

    public KPIPanel(KPIController controller) {
        this.controller = controller;
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
    }

    // ── Header + filter bar ───────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG_DARK);

        // Title
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(BG_DARK);
        JLabel title = new JLabel("KPI Monitoring Table");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(ACCENT);
        recordCountLabel = new JLabel();
        recordCountLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        recordCountLabel.setForeground(TXT_DIM);
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(recordCountLabel, BorderLayout.EAST);

        // Filter bar
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setBackground(BG_DARK);
        JLabel filterLbl = new JLabel("Filter by Status:");
        filterLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        filterLbl.setForeground(TXT_DIM);

        String[] options = {"All", "Achieved", "Not Achieved"};
        JComboBox<String> filterBox = new JComboBox<>(options);
        filterBox.setBackground(BG_INPUT);
        filterBox.setForeground(TXT_MAIN);
        filterBox.setFont(new Font("Monospaced", Font.PLAIN, 11));
        filterBox.addActionListener(e -> loadData((String) filterBox.getSelectedItem()));

        filterRow.add(filterLbl);
        filterRow.add(filterBox);

        p.add(titleRow,  BorderLayout.NORTH);
        p.add(filterRow, BorderLayout.SOUTH);
        return p;
    }

    // ── Table card ────────────────────────────────────────────────
    private JPanel buildTable() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(14, 14, 14, 14)));

        String[] cols = {"ID", "KPI Name", "Target", "Actual", "Achievement %", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C, 1));
        card.add(scroll, BorderLayout.CENTER);

        loadData("All");
        return card;
    }

    // ── Data loader ───────────────────────────────────────────────
    private void loadData(String filter) {
        tableModel.setRowCount(0);
        List<KPI> list = controller.getFilteredKPIs(filter);
        for (KPI kpi : list) {
            tableModel.addRow(new Object[]{
                    kpi.getKpiId(),
                    kpi.getKpiName(),
                    String.format("%.0f", kpi.getTargetValue()),
                    String.format("%.0f", kpi.getActualValue()),
                    controller.getAchievementPct(kpi) + "%",
                    kpi.getStatus().name().replace("_", " ")
            });
        }
        recordCountLabel.setText(list.size() + " record(s)");
    }

    // ── Table styling ─────────────────────────────────────────────
    private void styleTable(JTable t) {
        t.setBackground(BG_DARK);
        t.setForeground(TXT_MAIN);
        t.setFont(new Font("Monospaced", Font.PLAIN, 12));
        t.setRowHeight(28);
        t.setGridColor(BORDER_C);
        t.setShowGrid(true);
        t.setSelectionBackground(new Color(0, 80, 140));
        t.setSelectionForeground(Color.WHITE);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(10, 14, 22));
        h.setForeground(ACCENT);
        h.setFont(new Font("Monospaced", Font.BOLD, 11));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C));

        // Status column color
        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(CENTER);
                setForeground("ACHIEVED".equalsIgnoreCase(value.toString().replace(" ", "_")) ? ACCENT2 : RED_SOFT);
                setBackground(isSelected ? new Color(0, 80, 140) : BG_DARK);
                return this;
            }
        });

        // Centre-align numeric columns
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(JLabel.CENTER);
        centre.setBackground(BG_DARK);
        centre.setForeground(TXT_MAIN);
        for (int i : new int[]{0, 2, 3, 4}) t.getColumnModel().getColumn(i).setCellRenderer(centre);

        int[] widths = {40, 230, 90, 90, 110, 120};
        for (int i = 0; i < widths.length; i++)
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }
}

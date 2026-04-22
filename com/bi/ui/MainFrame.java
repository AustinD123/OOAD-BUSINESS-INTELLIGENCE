package com.bi.ui;

import com.bi.controller.*;
import com.bi.mock.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.bi.ui.LoginPanel.*;

/**
 * MainFrame — the application shell.
 *
 * Architecture:
 *   MainFrame
 *   ├── LoginPanel         (CardLayout card: "LOGIN")
 *   └── AppShell           (CardLayout card: "APP")
 *       ├── Sidebar (nav)
 *       └── ContentArea (CardLayout)
 *           ├── DashboardPanel  ("DASHBOARD")
 *           ├── KPIPanel        ("KPI")
 *           └── ReportPanel     ("REPORT")
 *
 * Services are instantiated here and injected into controllers,
 * which are then injected into panels — clean MVC wiring.
 */
public class MainFrame extends JFrame {

    // ── CardLayout keys ──────────────────────────────────────────
    private static final String CARD_LOGIN     = "LOGIN";
    private static final String CARD_APP       = "APP";
    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final String CARD_KPI       = "KPI";
    private static final String CARD_REPORT    = "REPORT";

    // ── Layout managers ───────────────────────────────────────────
    private final CardLayout outerLayout  = new CardLayout();
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel     outerPanel   = new JPanel(outerLayout);
    private final JPanel     contentPanel = new JPanel(contentLayout);

    // ── Services (mock impls of existing interfaces) ──────────────
    private final SecurityServiceMock  securityService  = new SecurityServiceMock();
    private final KPIServiceMock       kpiService       = new KPIServiceMock();
    private final ReportServiceMock    reportService    = new ReportServiceMock();
    private final AnalyticsServiceMock analyticsService = new AnalyticsServiceMock();

    // ── Controllers ───────────────────────────────────────────────
    private final LoginController     loginController     = new LoginController(securityService);
    private final KPIController       kpiController       = new KPIController(kpiService);
    private final DashboardController dashboardController = new DashboardController(kpiService, analyticsService);
    private final ReportController    reportController    = new ReportController(reportService);

    // ── Sidebar nav labels (to highlight active) ──────────────────
    private JLabel navDashboard, navKPI, navReport;
    private JLabel userLabel;

    public MainFrame() {
        setTitle("OREO ERP – Business Intelligence System");
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 560));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().add(outerPanel);

        // ── Login screen ──────────────────────────────────────────
        LoginPanel loginPanel = new LoginPanel(loginController, this::showApp);
        outerPanel.add(loginPanel, CARD_LOGIN);

        // ── App shell (shown after login) ─────────────────────────
        outerPanel.add(buildAppShell(), CARD_APP);

        // Start on login screen
        outerLayout.show(outerPanel, CARD_LOGIN);
    }

    // ── App shell: sidebar + content ─────────────────────────────
    private JPanel buildAppShell() {
        JPanel shell = new JPanel(new BorderLayout(0, 0));

        shell.add(buildSidebar(), BorderLayout.WEST);
        shell.add(buildContent(), BorderLayout.CENTER);
        return shell;
    }

    // ── Sidebar ───────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(210, 0));
        side.setBackground(BG_CARD);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_C));

        // Logo block
        JPanel logoBlock = new JPanel(new BorderLayout());
        logoBlock.setBackground(new Color(10, 14, 22));
        logoBlock.setMaximumSize(new Dimension(210, 65));
        logoBlock.setBorder(new EmptyBorder(14, 18, 14, 18));
        JLabel logoTxt = new JLabel("◈ OREO ERP");
        logoTxt.setFont(new Font("Monospaced", Font.BOLD, 15));
        logoTxt.setForeground(ACCENT);
        JLabel logoSub = new JLabel("Business Intelligence");
        logoSub.setFont(new Font("Monospaced", Font.PLAIN, 10));
        logoSub.setForeground(TXT_DIM);
        logoBlock.add(logoTxt, BorderLayout.CENTER);
        logoBlock.add(logoSub, BorderLayout.SOUTH);
        side.add(logoBlock);

        side.add(Box.createVerticalStrut(20));
        side.add(sideLabel("NAVIGATION"));

        navDashboard = navItem("⊞  Dashboard", () -> showContent(CARD_DASHBOARD, navDashboard));
        navKPI       = navItem("▤  KPI Monitor",() -> showContent(CARD_KPI, navKPI));
        navReport    = navItem("⊡  Reports",    () -> showContent(CARD_REPORT, navReport));

        side.add(navDashboard);
        side.add(navKPI);
        side.add(navReport);

        side.add(Box.createVerticalStrut(24));
        side.add(sideLabel("SESSION"));

        userLabel = new JLabel("  · Not logged in");
        userLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        userLabel.setForeground(new Color(80, 100, 130));
        userLabel.setMaximumSize(new Dimension(210, 22));
        side.add(userLabel);

        side.add(Box.createVerticalGlue());

        // Logout button
        JLabel logoutLabel = navItem("← Logout", this::logout);
        logoutLabel.setMaximumSize(new Dimension(210, 36));
        side.add(logoutLabel);
        side.add(Box.createVerticalStrut(12));

        return side;
    }

    private JLabel sideLabel(String text) {
        JLabel l = new JLabel("  " + text);
        l.setFont(new Font("Monospaced", Font.BOLD, 10));
        l.setForeground(TXT_DIM);
        l.setMaximumSize(new Dimension(210, 26));
        l.setBorder(new EmptyBorder(4, 12, 2, 0));
        return l;
    }

    private JLabel navItem(String label, Runnable action) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Monospaced", Font.PLAIN, 12));
        l.setForeground(TXT_MAIN);
        l.setMaximumSize(new Dimension(210, 38));
        l.setBorder(new EmptyBorder(10, 20, 10, 0));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
            public void mouseEntered(java.awt.event.MouseEvent e) { if (!l.getForeground().equals(ACCENT)) l.setForeground(new Color(180, 210, 245)); }
            public void mouseExited (java.awt.event.MouseEvent e) { if (!l.getForeground().equals(ACCENT)) l.setForeground(TXT_MAIN); }
        });
        return l;
    }

    // ── Content area ──────────────────────────────────────────────
    private JPanel buildContent() {
        contentPanel.setBackground(BG_DARK);

        contentPanel.add(new DashboardPanel(dashboardController), CARD_DASHBOARD);
        contentPanel.add(new KPIPanel(kpiController),             CARD_KPI);
        contentPanel.add(new ReportPanel(reportController),       CARD_REPORT);

        return contentPanel;
    }

    // ── Navigation ────────────────────────────────────────────────
    private void showContent(String card, JLabel activeNav) {
        contentLayout.show(contentPanel, card);
        // Reset all nav items, highlight active
        for (JLabel nav : new JLabel[]{navDashboard, navKPI, navReport}) {
            nav.setForeground(TXT_MAIN);
        }
        activeNav.setForeground(ACCENT);
    }

    private void showApp() {
        userLabel.setText("  · " + loginController.getLoggedInUser());
        outerLayout.show(outerPanel, CARD_APP);
        showContent(CARD_DASHBOARD, navDashboard);
    }

    private void logout() {
        outerLayout.show(outerPanel, CARD_LOGIN);
    }

    // ── Entry point ───────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

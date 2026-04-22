package com.bi.ui;

import com.bi.controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * View: Login screen.
 * Calls LoginController.login() — no business logic here.
 */
public class LoginPanel extends JPanel {

    // ── palette (shared across all panels) ───────────────────────
    static final Color BG_DARK   = new Color(15,  20,  30);
    static final Color BG_CARD   = new Color(22,  30,  46);
    static final Color BG_INPUT  = new Color(30,  40,  58);
    static final Color ACCENT    = new Color(0,  160, 255);
    static final Color ACCENT2   = new Color(0,  210, 140);
    static final Color TXT_MAIN  = new Color(220, 230, 245);
    static final Color TXT_DIM   = new Color(120, 140, 170);
    static final Color RED_SOFT  = new Color(255,  80,  80);
    static final Color BORDER_C  = new Color(40,  55,  80);

    private final LoginController controller;
    private final Runnable onLoginSuccess;

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;

    public LoginPanel(LoginController controller, Runnable onLoginSuccess) {
        this.controller     = controller;
        this.onLoginSuccess = onLoginSuccess;
        setBackground(BG_DARK);
        setLayout(new GridBagLayout());
        add(buildCard());
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(40, 50, 40, 50)));
        card.setPreferredSize(new Dimension(380, 340));

        // ── Logo ──────────────────────────────────────────────────
        JLabel logo = new JLabel("◈ OREO ERP", SwingConstants.CENTER);
        logo.setFont(new Font("Monospaced", Font.BOLD, 22));
        logo.setForeground(ACCENT);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Business Intelligence System", SwingConstants.CENTER);
        sub.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sub.setForeground(TXT_DIM);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // ── Fields ────────────────────────────────────────────────
        usernameField = styledTextField();
        passwordField = new JPasswordField();
        stylePasswordField(passwordField);

        // ── Status label ──────────────────────────────────────────
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(RED_SOFT);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);

        // ── Login button ──────────────────────────────────────────
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        loginBtn.setBackground(ACCENT);
        loginBtn.setForeground(BG_DARK);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> attemptLogin());

        // Allow Enter key to trigger login
        passwordField.addActionListener(e -> attemptLogin());

        // ── Hint ──────────────────────────────────────────────────
        JLabel hint = new JLabel("Hint: admin / admin123", SwingConstants.CENTER);
        hint.setFont(new Font("Monospaced", Font.PLAIN, 10));
        hint.setForeground(new Color(60, 80, 110));
        hint.setAlignmentX(CENTER_ALIGNMENT);

        // ── Assemble ──────────────────────────────────────────────
        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(hint);

        return card;
    }

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty()) { statusLabel.setText("Enter your username."); return; }
        if (pass.isEmpty()) { statusLabel.setText("Enter your password."); return; }

        if (controller.login(user, pass)) {
            statusLabel.setForeground(ACCENT2);
            statusLabel.setText("Welcome, " + user + "!");
            onLoginSuccess.run();
        } else {
            statusLabel.setForeground(RED_SOFT);
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }

    // ── helpers ───────────────────────────────────────────────────
    private JTextField styledTextField() {
        JTextField f = new JTextField();
        f.setBackground(BG_INPUT);
        f.setForeground(TXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(6, 10, 6, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return f;
    }

    private void stylePasswordField(JPasswordField f) {
        f.setBackground(BG_INPUT);
        f.setForeground(TXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                new EmptyBorder(6, 10, 6, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.PLAIN, 11));
        l.setForeground(TXT_DIM);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}

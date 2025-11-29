package view;

import data_access.DraftManager;
import entity.RecipeDraft;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DraftsDialog extends JDialog {
    private final String authorId;
    private final DraftManager draftManager;
    private RecipeDraft selectedDraft = null;
    private JPanel draftsListPanel;

    public DraftsDialog(Frame owner, String authorId) {
        super(owner, "My Recipe Drafts", true); // Modal dialog
        this.authorId = authorId;
        this.draftManager = DraftManager.getInstance();

        setupUI();
        loadDrafts();

        setSize(800, 600);
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Header
        JLabel titleLabel = new JLabel("My Recipe Drafts");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Drafts list
        draftsListPanel = new JPanel();
        draftsListPanel.setLayout(new BoxLayout(draftsListPanel, BoxLayout.Y_AXIS));
        draftsListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(draftsListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadDrafts() {
        draftsListPanel.removeAll();
        List<RecipeDraft> drafts = draftManager.loadDraftsForUser(authorId);

        if (drafts.isEmpty()) {
            draftsListPanel.setLayout(new GridBagLayout());
            JLabel emptyLabel = new JLabel("No drafts found");
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            emptyLabel.setForeground(Color.GRAY);
            draftsListPanel.add(emptyLabel);
        } else {
            draftsListPanel.setLayout(new BoxLayout(draftsListPanel, BoxLayout.Y_AXIS));
            // Sort by last saved (most recent first)
            drafts.sort((d1, d2) -> d2.getLastSaved().compareTo(d1.getLastSaved()));

            for (RecipeDraft draft : drafts) {
                draftsListPanel.add(createDraftItem(draft));
            }
        }

        draftsListPanel.revalidate();
        draftsListPanel.repaint();
    }

    private JPanel createDraftItem(RecipeDraft draft) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Left side - Draft info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(draft.getTitle().isEmpty() ? "[Untitled]" : draft.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        infoPanel.add(titleLabel);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        JLabel dateLabel = new JLabel("Last saved: " + dateFormat.format(draft.getLastSaved()));
        dateLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        dateLabel.setForeground(Color.GRAY);
        infoPanel.add(dateLabel);

        if (draft.getCategory() != null && !draft.getCategory().isEmpty()) {
            JLabel categoryLabel = new JLabel("Category: " + draft.getCategory());
            categoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            infoPanel.add(categoryLabel);
        }

        itemPanel.add(infoPanel, BorderLayout.CENTER);

        // Right side - Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
            selectedDraft = draft;
            dispose();
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this draft?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                draftManager.deleteDraft(draft.getDraftId());
                loadDrafts(); // Refresh the list
            }
        });

        actionPanel.add(loadButton);
        actionPanel.add(deleteButton);
        itemPanel.add(actionPanel, BorderLayout.EAST);

        return itemPanel;
    }

    public RecipeDraft getSelectedDraft() {
        return selectedDraft;
    }
}
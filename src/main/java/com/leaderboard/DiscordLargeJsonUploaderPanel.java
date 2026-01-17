package com.leaderboard;

import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DiscordLargeJsonUploaderPanel extends PluginPanel
{
    private final JCheckBox bossCheckbox;
    private final JCheckBox agiCheckbox;
    private final JComboBox<String> bossComboBox = new JComboBox<>(new String[]
    {
            "Kills",
            "Personal Best",
            "Both"
    });
    private final JComboBox<String> agiComboBox = new JComboBox<>(new String[]
    {
            "Completions",
            "Personal Best",
            "Both"
    });

    public DiscordLargeJsonUploaderPanel(DiscordLargeJsonUploaderConfig config)
    {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        //Container panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        //Panel title
        JLabel title = new JLabel("Discord Large Json Uploader Panel");
        title.setFont(FontManager.getRunescapeBoldFont());
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        //Current webhook
        JTextArea webhookTextArea = new JTextArea("Current Webhook:\n"+config.webhook());
        webhookTextArea.setForeground(Color.WHITE);
        webhookTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        webhookTextArea.setLineWrap(true);
        webhookTextArea.setEditable(false);
        webhookTextArea.setBorder(new EmptyBorder(0, 0, 10, 0));

        //Data Filtering checkboxes
        bossCheckbox = createCheckbox("Include Boss Data");
        agiCheckbox = createCheckbox("Include Agility Data");

        //Submit Data button
        JButton submitButton = new JButton("Submit Data to Discord");
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitButton.addActionListener(e ->
        {
            boolean one = bossCheckbox.isSelected();
            boolean two = agiCheckbox.isSelected();
        });

        //Add components to panel
        contentPanel.add(title);
        contentPanel.add(webhookTextArea);
        contentPanel.add(bossCheckbox);
        contentPanel.add(agiCheckbox);
        contentPanel.add(submitButton);

        add(contentPanel, BorderLayout.NORTH);
    }

    //General config options for how checkboxes look
    private JCheckBox createCheckbox(String text)
    {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setBackground(ColorScheme.DARK_GRAY_COLOR);
        checkBox.setForeground(Color.WHITE);
        checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        return checkBox;
    }
}
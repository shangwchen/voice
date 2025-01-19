import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

class MainFrame extends JFrame {
    private String[] labels = {"主音色", "辅助音色", "听感反馈", " ", "发展音色", "攻受属性", "声音色系", "听感年龄", "听感身高", "推荐音伴", "声音评级"};
    private JTextField[] textFields;
    private JLabel[] labelComponents;
    private int labelTextFieldSpacing = 5;
    private Point initialClick;
    private JLabel headerLabel;
    private SettingsManager settingsManager = new SettingsManager();
    private JButton clearButton;




    public MainFrame() {
        settingsManager = new SettingsManager(); // 初始化设置管理器

        // 加载窗口设置
        int width = Integer.parseInt(settingsManager.getSetting("windowWidth", "600"));
        int height = Integer.parseInt(settingsManager.getSetting("windowHeight", "600"));
        setSize(width, height);

        int x = Integer.parseInt(settingsManager.getSetting("windowX", "100"));
        int y = Integer.parseInt(settingsManager.getSetting("windowY", "100"));
        setLocation(x, y);

        // 设置背景颜色
        String bgColorValue = settingsManager.getSetting("bgColor", String.valueOf(Color.BLACK.getRGB()));
        Color bgColor = new Color(Integer.parseInt(bgColorValue));
        getContentPane().setBackground(bgColor);

        setTitle("温野（声鉴）");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 添加顶部文字
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        headerLabel = new JLabel(settingsManager.getSetting("headerText", "你不好奇自己的声音吗"), JLabel.LEADING);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 30));
        headerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(60, 60, 380, 30);
        headerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        headerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        headerLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = headerLabel.getLocation().x;
                int thisY = headerLabel.getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int X = thisX + xMoved;
                int Y = thisY + yMoved;

                headerLabel.setLocation(X, Y);
            }
        });

        layeredPane.add(headerLabel, JLayeredPane.DRAG_LAYER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(bgColor);
        inputPanel.setBounds(60, 100, 400, 450);

        textFields = new JTextField[labels.length];
        labelComponents = new JLabel[labels.length];

        for (int i = 0; i < labels.length; i++) {
            labels[i] = settingsManager.getSetting("label" + i, labels[i]);

            JLabel label = new JLabel(labels[i]);
            label.setForeground(Color.WHITE); // 设置字体颜色
            label.setFont(new Font("Default", Font.PLAIN, 25));

            JTextField textField = new JTextField(settingsManager.getSetting("textField" + i, ""));
            textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            textField.setFont(new Font("Default", Font.PLAIN, 20));
            textField.setForeground(Color.WHITE);
            textField.setBackground(Color.BLACK);
            textFields[i] = textField;
            labelComponents[i] = label;

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(3, 3, 3, labelTextFieldSpacing);
            inputPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(2, labelTextFieldSpacing, 2, 2);
            inputPanel.add(textField, gbc);
        }

        layeredPane.add(inputPanel, JLayeredPane.DEFAULT_LAYER);

        JButton clearButton = new JButton("一键清除");
        clearButton.setBounds(10, getHeight() - 90, 120, 30);
        clearButton.addActionListener(e -> clearInputs());
        layeredPane.add(clearButton, JLayeredPane.PALETTE_LAYER);

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("选项");

        JMenuItem saveItem = new JMenuItem("保存");
        saveItem.addActionListener(e -> saveInputs());
        optionsMenu.add(saveItem);

        JMenuItem colorItem = new JMenuItem("设置标签和背景颜色");
        colorItem.addActionListener(e -> setColors());
        optionsMenu.add(colorItem);

        JMenuItem textFieldColorItem = new JMenuItem("设置文本框颜色");
        textFieldColorItem.addActionListener(e -> setTextFieldColors());
        optionsMenu.add(textFieldColorItem);

        JMenuItem textColorItem = new JMenuItem("设置文本颜色");
        textColorItem.addActionListener(e -> setTextColors());
        optionsMenu.add(textColorItem);

        JMenuItem fontSizeItem = new JMenuItem("设置字体大小");
        fontSizeItem.addActionListener(e -> setFontSize());
        optionsMenu.add(fontSizeItem);

        JMenuItem labelFontSizeItem = new JMenuItem("设置标签字体大小");
        labelFontSizeItem.addActionListener(e -> setLabelFontSize());
        optionsMenu.add(labelFontSizeItem);

        JMenuItem spacingItem = new JMenuItem("设置标签和文本框间距");
        spacingItem.addActionListener(e -> setLabelTextFieldSpacing());
        optionsMenu.add(spacingItem);

        JMenuItem editHeaderItem = new JMenuItem("编辑顶部文字");
        editHeaderItem.addActionListener(e -> editHeaderText());
        optionsMenu.add(editHeaderItem);

        JMenuItem headerColorItem = new JMenuItem("设置顶部文字颜色");
        headerColorItem.addActionListener(e -> setHeaderTextColor());
        optionsMenu.add(headerColorItem);

        JMenuItem headerSizeItem = new JMenuItem("设置顶部文字大小");
        headerSizeItem.addActionListener(e -> setHeaderTextSize());
        optionsMenu.add(headerSizeItem);

        menuBar.add(optionsMenu);
        setJMenuBar(menuBar);

        add(layeredPane, BorderLayout.CENTER);

        // 设置关闭窗口时保存设置
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveSettings();
            }
        });
    }


    // 保存当前设置
    private void saveSettings() {
        settingsManager.setSetting("windowWidth", String.valueOf(getWidth()));
        settingsManager.setSetting("windowHeight", String.valueOf(getHeight()));
        settingsManager.setSetting("windowX", String.valueOf(getX()));
        settingsManager.setSetting("windowY", String.valueOf(getY()));

        // 保存背景颜色
        settingsManager.setSetting("bgColor", String.valueOf(getContentPane().getBackground().getRGB()));

        // 保存顶部文字
        settingsManager.setSetting("headerText", headerLabel.getText());

        // 保存标签和文本框内容
        for (int i = 0; i < labels.length; i++) {
            settingsManager.setSetting("label" + i, labels[i]);
            settingsManager.setSetting("textField" + i, textFields[i].getText());
        }

        settingsManager.saveSettings();
    }

    private void clearInputs() {
        for (JTextField textField : textFields) {
            textField.setText("");
        }
    }


    private void saveInputs() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < labels.length; i++) {
                    writer.write(labels[i] + ": " + textFields[i].getText());
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editLabels() {
        String[] options = {"编辑标签", "添加新标签", "删除标签"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "请选择操作：",
                "标签操作",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if ("编辑标签".equals(choice)) {
            // 编辑现有标签的逻辑
            String selectedLabel = (String) JOptionPane.showInputDialog(
                    this,
                    "请选择要编辑的标签：",
                    "编辑标签内容",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    labels,
                    labels[0]
            );

            if (selectedLabel != null) {
                String newLabel = JOptionPane.showInputDialog(
                        this,
                        "输入新的标签内容：",
                        selectedLabel
                );

                if (newLabel != null && !newLabel.trim().isEmpty()) {
                    for (int i = 0; i < labels.length; i++) {
                        if (labels[i].equals(selectedLabel)) {
                            labels[i] = newLabel;
                            labelComponents[i].setText(newLabel);
                            break;
                        }
                    }
                }
            }
        } else if ("添加新标签".equals(choice)) {
            // 添加新标签的逻辑
            String newLabel = JOptionPane.showInputDialog(
                    this,
                    "输入新的标签名称：",
                    "添加新标签",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (newLabel != null && !newLabel.trim().isEmpty()) {
                JTextField newTextField = new JTextField();
                JLabel newLabelComponent = new JLabel(newLabel);

                newLabelComponent.setForeground(Color.WHITE);

                // 动态更新布局
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = labels.length;
                gbc.anchor = GridBagConstraints.EAST;
                gbc.insets = new Insets(3, 3, 3, labelTextFieldSpacing);
                ((JPanel) labelComponents[0].getParent()).add(newLabelComponent, gbc);

                gbc.gridx = 1;
                gbc.weightx = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(2, labelTextFieldSpacing, 2, 2);
                ((JPanel) labelComponents[0].getParent()).add(newTextField, gbc);

                // 更新数组
                labels = extendArray(labels, newLabel);
                textFields = extendArray(textFields, newTextField);
                labelComponents = extendArray(labelComponents, newLabelComponent);

                // 刷新布局
                getContentPane().revalidate();
                getContentPane().repaint();
            }
        } else if ("删除标签".equals(choice)) {
            // 删除标签的逻辑
            String selectedLabel = (String) JOptionPane.showInputDialog(
                    this,
                    "请选择要删除的标签：",
                    "删除标签",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    labels,
                    labels[0]
            );

            if (selectedLabel != null) {
                for (int i = 0; i < labels.length; i++) {
                    if (labels[i].equals(selectedLabel)) {
                        // 从界面上移除对应的组件
                        ((JPanel) labelComponents[i].getParent()).remove(labelComponents[i]);
                        ((JPanel) textFields[i].getParent()).remove(textFields[i]);

                        // 更新数组
                        labels = removeFromArray(labels, i);
                        textFields = removeFromArray(textFields, i);
                        labelComponents = removeFromArray(labelComponents, i);

                        // 刷新布局
                        getContentPane().revalidate();
                        getContentPane().repaint();
                        break;
                    }
                }
            }
        }
    }

    private <T> T[] extendArray(T[] original, T newItem) {
        T[] extended = java.util.Arrays.copyOf(original, original.length + 1);
        extended[original.length] = newItem;
        return extended;
    }

    private <T> T[] removeFromArray(T[] original, int index) {
        T[] reduced = java.util.Arrays.copyOf(original, original.length - 1);
        if (index < original.length - 1) {
            System.arraycopy(original, index + 1, reduced, index, original.length - index - 1);
        }
        return reduced;
    }


    private void setColors() {
        Color backgroundColor = JColorChooser.showDialog(this, "选择背景颜色", getContentPane().getBackground());
        if (backgroundColor != null) {
            getContentPane().setBackground(backgroundColor);
            for (Component component : getContentPane().getComponents()) {
                if (component instanceof JPanel) {
                    component.setBackground(backgroundColor);
                } else if (component instanceof JLayeredPane) {
                    for (Component innerComponent : ((JLayeredPane) component).getComponents()) {
                        if (innerComponent instanceof JPanel) {
                            innerComponent.setBackground(backgroundColor);
                        }
                    }
                }
            }
        }

        Color fontColor = JColorChooser.showDialog(this, "选择字体颜色", Color.BLUE);
        if (fontColor != null) {
            for (Component component : getContentPane().getComponents()) {
                if (component instanceof JPanel) {
                    for (Component innerComponent : ((JPanel) component).getComponents()) {
                        if (innerComponent instanceof JLabel) {
                            ((JLabel) innerComponent).setForeground(fontColor);
                        }
                    }
                } else if (component instanceof JLayeredPane) {
                    for (Component innerComponent : ((JLayeredPane) component).getComponents()) {
                        if (innerComponent instanceof JPanel) {
                            for (Component innerInnerComponent : ((JPanel) innerComponent).getComponents()) {
                                if (innerInnerComponent instanceof JLabel) {
                                    ((JLabel) innerInnerComponent).setForeground(fontColor);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setTextFieldColors() {
        Color textFieldColor = JColorChooser.showDialog(this, "选择文本框背景颜色", Color.WHITE);
        if (textFieldColor != null) {
            for (JTextField textField : textFields) {
                textField.setBackground(textFieldColor);
            }
        }
    }

    private void setTextColors() {
        Color textColor = JColorChooser.showDialog(this, "选择文本颜色", Color.BLACK);
        if (textColor != null) {
            for (JTextField textField : textFields) {
                textField.setForeground(textColor);
            }
        }
    }

    private void setFontSize() {
        String sizeStr = JOptionPane.showInputDialog(this, "输入字体大小：", "设置字体大小", JOptionPane.PLAIN_MESSAGE);
        if (sizeStr != null && !sizeStr.isEmpty(

        )) {
            try {
                int size = Integer.parseInt(sizeStr);
                Font font = new Font("Default", Font.PLAIN, size);

                for (JTextField textField : textFields) {
                    textField.setFont(font);

                    // 动态调整文本框大小
                    FontMetrics fm = textField.getFontMetrics(font);
                    int textHeight = fm.getHeight();
                    int textWidth = fm.charWidth('W') * 15; // 假设默认宽度为15个字符宽度
                    textField.setPreferredSize(new Dimension(textWidth, textHeight + 5));

                    // 强制更新文本框的实际大小
                    textField.setSize(new Dimension(textWidth, textHeight + 5));
                }

                // 刷新布局
                getContentPane().revalidate();
                getContentPane().repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void setLabelFontSize() {
        String sizeStr = JOptionPane.showInputDialog(this, "输入标签字体大小：", "设置标签字体大小", JOptionPane.PLAIN_MESSAGE);
        if (sizeStr != null && !sizeStr.isEmpty()) {
            try {
                int size = Integer.parseInt(sizeStr);
                Font font = new Font("Default", Font.PLAIN, size);
                for (JLabel label : labelComponents) {
                    label.setFont(font);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setLabelTextFieldSpacing() {
        String spacingStr = JOptionPane.showInputDialog(this, "输入标签和文本框间距：", "设置标签和文本框间距", JOptionPane.PLAIN_MESSAGE);
        if (spacingStr != null && !spacingStr.isEmpty()) {
            try {
                labelTextFieldSpacing = Integer.parseInt(spacingStr);
                getContentPane().revalidate();
                getContentPane().repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editHeaderText() {
        String newText = JOptionPane.showInputDialog(this, "输入新的顶部文字：", "编辑顶部文字", JOptionPane.PLAIN_MESSAGE);
        if (newText != null && !newText.isEmpty()) {
            headerLabel.setText(newText);
        }
    }

    private void setHeaderTextColor() {
        Color newColor = JColorChooser.showDialog(this, "选择顶部文字颜色", headerLabel.getForeground());
        if (newColor != null) {
            headerLabel.setForeground(newColor);
        }
    }

    private void setHeaderTextSize() {
        String sizeStr = JOptionPane.showInputDialog(this, "输入顶部文字大小：", "设置顶部文字大小", JOptionPane.PLAIN_MESSAGE);
        if (sizeStr != null && !sizeStr.isEmpty()) {
            try {
                int size = Integer.parseInt(sizeStr);
                headerLabel.setFont(headerLabel.getFont().deriveFont((float) size));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addDraggableImage(JLayeredPane layeredPane) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());

            // 获取原始图片宽高比
            double aspectRatio = (double) icon.getIconWidth() / icon.getIconHeight();

            // 创建用于显示图片的标签
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setBounds(100, 100, icon.getIconWidth(), icon.getIconHeight());
            imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            // 添加鼠标拖动功能
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    initialClick = e.getPoint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // 检测右键单击用于删除
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int confirm = JOptionPane.showConfirmDialog(
                                layeredPane,
                                "确定要删除这张图片吗？",
                                "确认删除",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            layeredPane.remove(imageLabel);
                            layeredPane.repaint();
                        }
                    }
                }
            });

            // 调整大小相关的鼠标事件
            imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
                private boolean resizing = false;

                @Override
                public void mouseMoved(MouseEvent e) {
                    // 检测鼠标是否在图片右下角（用于调整大小）
                    if (e.getX() >= imageLabel.getWidth() - 10 && e.getY() >= imageLabel.getHeight() - 10) {
                        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                        resizing = true;
                    } else {
                        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        resizing = false;
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (resizing) {
                        // 计算新的宽度和高度，保持比例
                        int newWidth = e.getX();
                        int newHeight = (int) (newWidth / aspectRatio);

                        // 设置最小宽度和高度限制
                        if (newWidth > 50 && newHeight > 50) {
                            imageLabel.setSize(newWidth, newHeight);
                            ImageIcon scaledIcon = new ImageIcon(
                                    icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)
                            );
                            imageLabel.setIcon(scaledIcon);
                        }
                    } else {
                        // 拖动图片
                        int thisX = imageLabel.getLocation().x;
                        int thisY = imageLabel.getLocation().y;

                        int xMoved = e.getX() - initialClick.x;
                        int yMoved = e.getY() - initialClick.y;

                        int X = thisX + xMoved;
                        int Y = thisY + yMoved;

                        imageLabel.setLocation(X, Y);
                    }
                }
            });

            // 将图片标签添加到图层面板中
            layeredPane.add(imageLabel, JLayeredPane.DRAG_LAYER);
            layeredPane.repaint();
        }
    }
}

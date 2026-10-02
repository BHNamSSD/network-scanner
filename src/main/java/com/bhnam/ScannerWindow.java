package com.bhnam;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ScannerWindow extends JFrame {

    private final JTextField subnetField =
            new JTextField("192.168.1", 12);

    private final JSpinner startSpinner =
            new JSpinner(new SpinnerNumberModel(
                    1, 1, 254, 1));

    private final JSpinner endSpinner =
            new JSpinner(new SpinnerNumberModel(
                    254, 1, 254, 1));

    private final JComboBox<String> filterCombo =
            new JComboBox<>(new String[]{
                    "All",
                    "UP only"
            });

    private final JButton scanButton =
            new JButton("Scan");

    private final JButton stopButton =
            new JButton("Stop");

    private final JProgressBar progressBar =
            new JProgressBar();

    private final JLabel statusLabel =
            new JLabel("Ready");

    private final JLabel foundLabel =
            new JLabel("Online: 0");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                            "IP Address",
                            "Host Name",
                            "MAC Address",
                            "Status",
                            "Response"
                    }, 0) {

                @Override
                public boolean isCellEditable(
                        int row,
                        int column) {
                    return false;
                }
            };

    private final JTable table =
            new JTable(tableModel);

    private final TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(tableModel);

    private final NetworkScanner scanner =
            new NetworkScanner();

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private Future<?> currentTask;

    private volatile boolean scanning = false;

    public ScannerWindow() {

        setTitle("Java Network Scanner");
        setSize(800, 500);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                WindowConstants.EXIT_ON_CLOSE);

        createUI();

        stopButton.setEnabled(false);
    }

    private void createUI() {

        JPanel topPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        topPanel.add(new JLabel("Subnet:"));
        topPanel.add(subnetField);

        topPanel.add(new JLabel("Start:"));
        topPanel.add(startSpinner);

        topPanel.add(new JLabel("End:"));
        topPanel.add(endSpinner);

        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);

        topPanel.add(scanButton);
        topPanel.add(stopButton);

        add(topPanel, BorderLayout.NORTH);

        table.setRowHeight(28);

        table.setRowSorter(sorter);

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(180);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(100);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(150);

        add(new JScrollPane(table),
                BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(
                new BorderLayout()
        );

        JPanel infoPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        infoPanel.add(statusLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(foundLabel);

        bottomPanel.add(
                infoPanel,
                BorderLayout.WEST
        );

        bottomPanel.add(
                progressBar,
                BorderLayout.CENTER
        );

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        scanButton.addActionListener(
                e -> startScan()
        );

        stopButton.addActionListener(
                e -> stopScan()
        );

        filterCombo.addActionListener(
                e -> applyFilter()
        );
    }

    private void applyFilter() {

        String filter =
                (String) filterCombo.getSelectedItem();

        if ("UP only".equals(filter)) {

            sorter.setRowFilter(
                    new RowFilter<DefaultTableModel, Integer>() {

                        @Override
                        public boolean include(
                                Entry<? extends DefaultTableModel,
                                        ? extends Integer> entry) {

                            String status =
                                    entry.getStringValue(3);

                            return "UP".equals(status);
                        }
                    }
            );

        } else {

            sorter.setRowFilter(null);
        }
    }

    private void startScan() {

        if (scanning) {
            return;
        }

        String subnet =
                subnetField.getText().trim();

        int start =
                (Integer) startSpinner.getValue();

        int end =
                (Integer) endSpinner.getValue();

        if (!isValidSubnet(subnet)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Subnet không hợp lệ.\n"
                            + "Ví dụ: 192.168.1",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        if (start > end) {

            JOptionPane.showMessageDialog(
                    this,
                    "Start IP phải nhỏ hơn End IP.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        tableModel.setRowCount(0);

        int total = end - start + 1;

        progressBar.setMinimum(0);
        progressBar.setMaximum(total);
        progressBar.setValue(0);

        scanning = true;

        scanButton.setEnabled(false);
        stopButton.setEnabled(true);

        statusLabel.setText("Scanning...");
        foundLabel.setText("Online: 0");

        currentTask = executor.submit(() -> {

            int online = 0;

            for (int i = start;
                 i <= end && scanning;
                 i++) {

                String ip =
                        subnet + "." + i;

                NetworkScanner.ScanResult result =
                        scanner.scan(ip);

                if ("UP".equals(result.status())) {
                    online++;
                }

                int finalOnline = online;

                SwingUtilities.invokeLater(() -> {

                    tableModel.addRow(
                            new Object[]{
                                    result.ip(),
                                    result.hostname(),
                                    result.mac(),
                                    result.status(),
                                    result.responseTime() >= 0
                                            ? result.responseTime()
                                            + " ms"
                                            : "-"
                            }
                    );

                    progressBar.setValue(
                            progressBar.getValue() + 1
                    );

                    foundLabel.setText(
                            "Online: " + finalOnline
                    );

                    applyFilter();
                });
            }

            SwingUtilities.invokeLater(() -> {

                if (scanning) {

                    statusLabel.setText(
                            "Scan completed"
                    );

                } else {

                    statusLabel.setText(
                            "Scan stopped"
                    );
                }

                scanning = false;

                scanButton.setEnabled(true);
                stopButton.setEnabled(false);
            });
        });
    }

    private void stopScan() {

        scanning = false;

        if (currentTask != null) {
            currentTask.cancel(true);
        }

        statusLabel.setText("Stopping...");
        stopButton.setEnabled(false);
    }

    private boolean isValidSubnet(String subnet) {

        String[] parts =
                subnet.split("\\.");

        if (parts.length != 3) {
            return false;
        }

        try {

            for (String part : parts) {

                int value =
                        Integer.parseInt(part);

                if (value < 0 || value > 255) {
                    return false;
                }
            }

            return true;

        } catch (NumberFormatException e) {

            return false;
        }
    }
}


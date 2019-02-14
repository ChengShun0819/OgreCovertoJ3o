package Main;

import Core.JMonkey;
import Utility.FileUtility;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author shun.fang
 */
public class Main {

    public static void main(String[] args) {
        String[] str = new String[1];
        str[0] = "[" + FileUtility.getCurrentDirectory() + "assets\\Green," + FileUtility.getCurrentDirectory() + "assets\\Red]";
        new MyFrame(str);
    }

    public static class MyFrame extends JFrame {

        private ArrayList<String> exportFileList = new ArrayList();
        private ArrayList<String> fileList = new ArrayList();
        private String importPath = "";
        private String adjustImportPath = "";
        private JPanel topPanel = new JPanel();
        private JPanel centerPanel = new JPanel();
        private JPanel corePanel = new JPanel();
        public JMonkey modelPreview = null;
        private int addObjectFrameWidth = 520;
        private int addObjectFrameHight = 380;
        public File selectedFile = null;
        private JPanel modelListPanel = new JPanel();
        private JTable modelListTable = null;

        public MyFrame(String[] args) {
            String[] exportFileString = args[0].substring(1, args[0].length() - 1).split(",");
            exportFileList.clear();
            for (String fileStirng : exportFileString) {
                exportFileList.add(fileStirng.trim());
            }
            selectedFile = new File(adjustImportPath);
            layoutSetting();

            modelPreview = new JMonkey(300, 310);
            corePanel.add(modelPreview.coreCanvas);
            corePanel.revalidate();
            corePanel.repaint();

            new Thread() {
                @Override
                public void run() {
                    while (!modelPreview.IsFinsh) {
                        try {
                            Thread.sleep((long) 100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    modelPreview.selectedModel(exportFileList.get(0));
                    modelPreview.IsSelectedFinish = false;
                }
            }.start();
        }

        private void loadFileList() {
            if (exportFileList != null) {
                fileList.clear();
                int fileNumber = 0;
                for (int i = 0; i < exportFileList.size(); i++) {
                    File file = new File(exportFileList.get(i));
                    DefaultTableModel defaultTableModel = (DefaultTableModel) (modelListTable.getModel());
                    defaultTableModel.addRow(new Object[]{"", "", ""});
                    String fileName = file.getName();
                    modelListTable.getModel().setValueAt(fileName, fileNumber, 0);
                    fileList.add(file.getPath());
                    fileNumber++;
                }
            }

            modelListTable.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (modelListTable.getSelectedRow() != -1) {
                        modelPreview.selectedModel(fileList.get(modelListTable.getSelectedRow()));
                        modelPreview.IsSelectedFinish = false;
                    }
                }
            });

        }

        private void layoutSetting() {
            topPanel.setBackground(Color.WHITE);
            topPanel.setPreferredSize(new Dimension(addObjectFrameWidth, 20));
            topPanel.setLayout(null);

            corePanel.setPreferredSize(new Dimension(300, 310));
            corePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            modelListPanel.setPreferredSize(new Dimension(200, 310));
            modelListPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            modelListPanel.setBackground(Color.WHITE);
            centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            centerPanel.setBackground(Color.WHITE);
            centerPanel.add(Box.createHorizontalStrut(5));
            centerPanel.add(corePanel);
            centerPanel.add(Box.createHorizontalStrut(5));
            centerPanel.add(modelListPanel);

            JPanel modelListTablePanel = new JPanel();
            modelListTablePanel.setBackground(Color.WHITE);
            modelListTablePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
            modelListPanel.add(modelListTablePanel);
            Object[][] data = {};
            Object[] columns = {"List"};
            modelListTable = new JTable(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                public Class<?> getColumnClass(int column) {
                    switch (column) {
                        default:
                            return String.class;
                    }
                }
            });

            modelListTable.setPreferredScrollableViewportSize(new Dimension(185, 280));
            modelListTable.setRowHeight(23);
            modelListTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scroll = new JScrollPane(modelListTable);
            scroll.getViewport().setBackground(Color.WHITE);
            modelListTablePanel.add(scroll);
            loadFileList();
            modelListTable.setRowSelectionInterval(0, 0);
            setJTableColumnsWidth(modelListTable, 185, 130, 55);


            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(addObjectFrameWidth, addObjectFrameHight);
            this.getContentPane().setLayout(new BorderLayout());
            this.setTitle("Convert");
            this.setLocationRelativeTo(null);
            this.setResizable(false);
            this.setVisible(true);

            this.getContentPane().add(topPanel, BorderLayout.NORTH);
            this.getContentPane().add(centerPanel, BorderLayout.CENTER);
        }

        public void setJTableColumnsWidth(JTable table, int tablePreferredWidth, double... percentages) {
            double total = 0;
            for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
                total += percentages[i];
            }

            for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth((int) (tablePreferredWidth * (percentages[i] / total)));
            }
        }
    }
}

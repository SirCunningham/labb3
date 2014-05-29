package labb3;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class DirTree extends JFrame implements ActionListener {

    private JCheckBox box;
    private JTree tree;
    private final DefaultMutableTreeNode root;
    private final DefaultTreeModel treeModel;
    private final JPanel controls;
    private Scanner sca;
    String line = null;
    private static String katalog = ".";
    private static final String closeString = " Close ";
    private static final String showString = " Show Details ";

    public DirTree() {
        Container c = getContentPane();
        try {
            sca = new Scanner(new BufferedReader(new FileReader("Liv.xml")));
        } catch (FileNotFoundException e) {
            e.getStackTrace();
        }
        //*** Build the tree and a mouse listener to handle clicks
        root = new DefaultMutableTreeNode("Liv");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        buildTree();
        MouseListener ml =
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (box.isSelected()) {
                            showDetails(tree.getPathForLocation(e.getX(),
                                    e.getY()));
                        }
                    }
                };
        tree.addMouseListener(ml);
        //*** build the tree by adding the nodes
        //buildTree();
        //*** panel the JFrame to hold controls and the tree
        controls = new JPanel();
        box = new JCheckBox(showString);
        init(); //** set colors, fonts, etc. and add buttons
        c.add(controls, BorderLayout.NORTH);
        c.add(tree, BorderLayout.CENTER);
        setVisible(true); //** display the framed window
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals(closeString)) {
            dispose();
        }
    }

    private void init() {
        tree.setFont(new Font("Dialog", Font.BOLD, 12));
        controls.add(box);
        addButton(closeString);
        controls.setBackground(Color.lightGray);
        controls.setLayout(new FlowLayout());
        setSize(400, 400);
    }

    private void addButton(String n) {
        JButton b = new JButton(n);
        b.setFont(new Font("Dialog", Font.BOLD, 12));
        b.addActionListener(this);
        controls.add(b);
    }

    private void buildTree() {
        String[] s = {"Växter", "Djur", "Svampar"};
        for (String str : s) {
            DefaultMutableTreeNode child =
                    new DefaultMutableTreeNode(str);
            root.add(child);
            String[] str2 = {"Ordingar", "Familjer", "Släkten", "Arter"};
            buildTree(str2, child);
        }
    }

    private void buildTree(String[] s, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode[] childs = new DefaultMutableTreeNode[s.length];
        for (int i = 0; i < childs.length; i++) {
            childs[i] = new DefaultMutableTreeNode(s[i]);
        }
        parent.add(childs[0]);
        for (int i = 0; i < childs.length - 1; i++) {
            childs[i].add(childs[i + 1]);
        }

    }

    private void buildTree(File f, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode child =
                new DefaultMutableTreeNode(f.toString());
        parent.add(child);
        if (f.isDirectory()) {
            String list[] = f.list();
            for (String list1 : list) {
                buildTree(new File(f, list1), child);
            }
        }
    }

    private void showDetails(TreePath p) {
        if (p == null) {
            return;
        }
        File f = new File(p.getLastPathComponent().toString());
        JOptionPane.showMessageDialog(this, f.getPath()
                + "\n   "
                + getAttributes(f));
    }

    private String getAttributes(File f) {
        String t = "";
        if (f.isDirectory()) {
            t += "Directory";
        } else {
            t += "Nondirectory file";
        }
        t += "\n   ";
        if (!f.canRead()) {
            t += "not ";
        }
        t += "Readable\n   ";
        if (!f.canWrite()) {
            t += "not ";
        }
        t += "Writeable\n  ";
        if (!f.isDirectory()) {
            t += "Size in bytes: " + f.length() + "\n   ";
        } else {
            t += "Contains files: \n     ";
            String[] contents = f.list();
            for (String content : contents) {
                t += content + ", ";
            }
            t += "\n";
        }
        return t;
    }

    public static void main(String[] args) {

        if (args.length > 0) {
            katalog = args[0];
        }
        new DirTree();
    }
}
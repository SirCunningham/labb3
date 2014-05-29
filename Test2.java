package labb3;

import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.util.regex.*;

public class Test2 extends JFrame implements ActionListener {

    private JCheckBox box;
    private JTree tree;
    private DefaultMutableTreeNode root;
    private final JPanel controls;
    private Scanner sca;
    private String line;
    private static final String closeString = " Close ";
    private static final String showString = " Show Details ";

    public Test2() {
        String file = "Liv.xml";
        try {
            sca = new Scanner(new BufferedReader(new FileReader(file)));
        } catch (FileNotFoundException e) {
            System.err.println("Filen '" + file + "' kunde inte hittas");
            e.getStackTrace();
        }
        try {
            root = readNode();
        } catch (ErrorInFileException e) {
            System.err.println("Filen '" + file + "' kunde inte läsas");
        }

        tree = new JTree(new DefaultTreeModel(root));
        box = new JCheckBox(showString);
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (box.isSelected()) {
                    showDetails(tree.getPathForLocation(e.getX(), e.getY()));
                }
            }
        });
        tree.setFont(new Font("Dialog", Font.BOLD, 12));
        controls = new JPanel();
        controls.add(box);
        JButton btn = new JButton(closeString);
        btn.setFont(new Font("Dialog", Font.BOLD, 12));
        btn.addActionListener(this);
        controls.add(btn);
        controls.setBackground(Color.lightGray);
        controls.setLayout(new FlowLayout());
        setSize(400, 400);
        Container c = getContentPane();
        c.add(controls, BorderLayout.NORTH);
        c.add(tree, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Test2();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(closeString)) {
            dispose();
        }
    }

    //Uppgift E1
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

    //Uppgift E1
    private void buildTree(String[] s, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode[] kidArray = new DefaultMutableTreeNode[s.length];
        for (int i = 0; i < kidArray.length; i++) {
            kidArray[i] = new DefaultMutableTreeNode(s[i]);
        }
        parent.add(kidArray[0]);
        for (int i = 0; i < kidArray.length - 1; i++) {
            kidArray[i].add(kidArray[i + 1]);
        }
    }

    private void showDetails(TreePath p) {
        if (p == null) {
            return;
        }
        MyNode node = (MyNode) p.getLastPathComponent();
        String s1 = node.getLvl() + ": "
                + node.getName() + node.getDescription();
        String s2 = " men allt som är " + node.getName().toLowerCase();
        while (!node.isRoot()) {
            node = (MyNode) node.getParent();
            s2 += " är " + node.getName().toLowerCase();
        }
        JOptionPane.showMessageDialog(this, s1 + s2 + '.');
    }

    private Boolean isError(String test) {
        Matcher m = Pattern.compile("\\<(.+?)\\>").matcher(test);
        while (m.find()) {
            if (m.group(1).matches("([a-zA-ZåäöÅÄÖ\\d]+?) namn=((\"([a-zA-Zåäö"
                    + "ÅÄÖ\\d ]+?)\")|(\'([a-zA-ZåäöÅÄÖ\\d ]+?)"
                    + "\'))") || m.group(1).matches("/[a-zA-zåäöÅÄÖ\\d]+?")
                    || m.group(1).matches("\\?(.+?)\\?")) {
                return false;
            }
        }
        System.out.println("Trasig kod: " + test);
        return true;
    }

    public final MyNode readNode() throws ErrorInFileException {
        if (line == null) {
            line = sca.nextLine();
        }
        if (line.contains("<?") || line.contains("?>")) {
            if (line.contains("<?") && line.contains("?>")) {
                if (isError(line)) {
                    throw new ErrorInFileException();
                }
                line = sca.nextLine();
            } else {
                throw new ErrorInFileException();
            }
        }
        if (isError(line)) {
            throw new ErrorInFileException();
        }

        String level = line.substring(1, line.indexOf(" "));
        String name = line.substring(line.indexOf("=") + 2,
                line.indexOf(">") - 1);
        String description = line.substring(line.indexOf(">") + 1);
        MyNode retNode = new MyNode(name, level, description);

        String comp = "";
        line = sca.nextLine();
        if (isError(line)) {
            throw new ErrorInFileException();
        }

        if (line.contains("</")) {
            //Om starttag!=sluttag
            if (!line.substring(2, line.indexOf(">")).equals(level)) {
                throw new ErrorInFileException();
            }
            if (sca.hasNext()) {
                line = sca.nextLine();
                if (isError(line)) {
                    throw new ErrorInFileException();
                }
                return retNode;
            } else {
                return retNode;
            }
        } else {
            while (!level.equals(comp)) {
                //Om en sluttagg är fel så fortsätter while-loopen tills vi har gått igenom filen.
                if (!sca.hasNext()) {
                    throw new ErrorInFileException();
                }
                if (!line.contains("</")) {
                    retNode.add(readNode());
                } else {
                    line = sca.nextLine();
                }
                if (isError(line)) {
                    throw new ErrorInFileException();
                }
                //Om sluttag felstavad, fortsätter ibland loopen till en ny tagg av samma typ som level
                if (!line.contains("</") && line.contains(level)) {
                    throw new ErrorInFileException();
                }
                comp = line.substring(2, line.indexOf(">"));
            }
            return retNode;
        }
    }
}

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

public class Decryption extends JFrame implements ActionListener {
    JButton open = new JButton("OPEN FILE");
    JButton decode = new JButton("DECODE");
    JButton reset = new JButton("RESET");
    JTextArea message = new JTextArea(10, 3);
    BufferedImage image = null;
    JScrollPane imagePane = new JScrollPane();

    public Decryption() {
        super("DECODE IMAGE");
        this.assembleInterface();
        this.setSize(800, 600);
        this.setLocationRelativeTo((Component)null);
        this.setDefaultCloseOperation(2);
        this.setVisible(true);
    }

    private void assembleInterface() {
        JPanel p = new JPanel(new FlowLayout());
        p.add(this.open);
        p.add(this.decode);
        p.add(this.reset);
        this.getContentPane().add(p, "North");
        this.open.addActionListener(this);
        this.decode.addActionListener(this);
        this.reset.addActionListener(this);
        this.open.setMnemonic('O');
        this.decode.setMnemonic('D');
        this.reset.setMnemonic('R');
        p = new JPanel(new GridLayout(1, 1));
        p.add(new JScrollPane(this.message));
        this.message.setFont(new Font("Arial", 1, 20));
        p.setBorder(BorderFactory.createTitledBorder("HIDDEN MESSAGE : "));
        this.message.setEditable(false);
        this.getContentPane().add(p, "South");
        this.imagePane.setBorder(BorderFactory.createTitledBorder("ENCODED IMAGE"));
        this.getContentPane().add(this.imagePane, "Center");
    }

    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();
        if (o == this.open) {
            this.openImage();
        } else if (o == this.decode) {
            this.decodeMessage();
        } else if (o == this.reset) {
            this.resetInterface();
        }

    }

    private File showFileDialog(boolean open) {
        JFileChooser fc = new JFileChooser("CHOOSE FILE");
        FileFilter ff = new FileFilter() {
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return f.isDirectory() || name.endsWith(".png") || name.endsWith(".bmp");
            }

            public String getDescription() {
                return "Image (*.png, *.bmp)";
            }
        };
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(ff);
        File f = null;
        if (open && fc.showOpenDialog(this) == 0) {
            f = fc.getSelectedFile();
        } else if (!open && fc.showSaveDialog(this) == 0) {
            f = fc.getSelectedFile();
        }

        return f;
    }

    private void openImage() {
        File f = this.showFileDialog(true);

        try {
            this.image = ImageIO.read(f);
            JLabel l = new JLabel(new ImageIcon(this.image));
            this.imagePane.getViewport().add(l);
            this.validate();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private void decodeMessage() {
        if (this.image == null) {
            JOptionPane.showMessageDialog((Component)null, "PLEASE OPEN PICTURE FIRST");
        } else {
            int len = this.extractInteger(this.image, 0, 0);
            byte[] b = new byte[len];

            for(int i = 0; i < len; ++i) {
                b[i] = this.extractByte(this.image, i * 8 + 32, 0);
            }

            this.message.setText(new String(b));
        }
    }

    private int extractInteger(BufferedImage img, int start, int storageBit) {
        int maxX = img.getWidth();
        int maxY = img.getHeight();
        int startX = start / maxY;
        int startY = start - startX * maxY;
        int count = 0;
        int length = 0;

        for(int i = startX; i < maxX && count < 32; ++i) {
            for(int j = startY; j < maxY && count < 32; ++j) {
                int rgb = img.getRGB(i, j);
                int bit = this.getBitValue(rgb, storageBit);
                length = this.setBitValue(length, count, bit);
                ++count;
            }
        }

        return length;
    }

    private byte extractByte(BufferedImage img, int start, int storageBit) {
        int maxX = img.getWidth();
        int maxY = img.getHeight();
        int startX = start / maxY;
        int startY = start - startX * maxY;
        int count = 0;
        byte b = 0;

        for(int i = startX; i < maxX && count < 8; ++i) {
            for(int j = startY; j < maxY && count < 8; ++j) {
                int rgb = img.getRGB(i, j);
                int bit = this.getBitValue(rgb, storageBit);
                b = (byte)this.setBitValue(b, count, bit);
                ++count;
            }
        }

        return b;
    }

    private void resetInterface() {
        this.message.setText("");
        this.imagePane.getViewport().removeAll();
        this.image = null;
        this.validate();
    }

    private int getBitValue(int n, int location) {
        int v = n & (int)Math.round(Math.pow(2.0D, (double)location));
        return v == 0 ? 0 : 1;
    }

    private int setBitValue(int n, int location, int bit) {
        int toggle = (int)Math.pow(2.0D, (double)location);
        int bv = this.getBitValue(n, location);
        if (bv == bit) {
            return n;
        } else {
            if (bv == 0 && bit == 1) {
                n |= toggle;
            } else if (bv == 1 && bit == 0) {
                n ^= toggle;
            }

            return n;
        }
    }
}

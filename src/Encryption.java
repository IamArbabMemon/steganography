
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
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

public class Encryption extends JFrame implements ActionListener {
    JButton open = new JButton("OPEN");
    JButton embed = new JButton("EMBED");
    JButton save = new JButton("SAVE AS NEW FILE");
    JButton reset = new JButton("RESET");
    JTextArea message = new JTextArea(10, 3);
    BufferedImage sourceImage = null;
    BufferedImage embeddedImage = null;
    JSplitPane sp = new JSplitPane(1);
    JScrollPane originalPane = new JScrollPane();
    JScrollPane embeddedPane = new JScrollPane();

    public Encryption() {
        super("EMBEDDING MESSAGE IN IMAGE");
        this.assembleInterface();

        this.setSize(1000, 700);
        this.setLocationRelativeTo((Component)null);
        this.setDefaultCloseOperation(2);
        this.setVisible(true);
        this.sp.setDividerLocation(0.5D);
        this.validate();
    }

    private void assembleInterface() {
        JPanel p = new JPanel(new FlowLayout());
        p.add(this.open);
        p.add(this.embed);
        p.add(this.save);
        p.add(this.reset);
        this.getContentPane().add(p, "South");
        this.open.addActionListener(this);
        this.embed.addActionListener(this);
        this.save.addActionListener(this);
        this.reset.addActionListener(this);
        this.open.setMnemonic('O');
        this.embed.setMnemonic('E');
        this.save.setMnemonic('S');
        this.reset.setMnemonic('R');
        p = new JPanel(new GridLayout(1, 1));
        p.add(new JScrollPane(this.message));
        this.message.setFont(new Font("Arial", 1, 20));
        p.setBorder(BorderFactory.createTitledBorder("TYPE YOUR MESSAGE HERE"));
        this.getContentPane().add(p, "North");
        this.sp.setLeftComponent(this.originalPane);
        this.sp.setRightComponent(this.embeddedPane);
        this.originalPane.setBorder(BorderFactory.createTitledBorder("ORIGINAL IMAGE"));
        this.embeddedPane.setBorder(BorderFactory.createTitledBorder("ENCODED Image"));
        this.getContentPane().add(this.sp, "Center");
    }

    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();
        if (o == this.open) {
            this.openImage();
        } else if (o == this.embed) {
            this.embedMessage();
        } else if (o == this.save) {
            this.saveImage();
        } else if (o == this.reset) {
            this.resetInterface();
        }

    }

    private File showFileDialog(final boolean open) {
        JFileChooser fc = new JFileChooser("CHOOSE IMAGE");
        FileFilter ff = new FileFilter() {
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                if (open) {
                    return f.isDirectory() || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".tiff") || name.endsWith(".bmp") || name.endsWith(".dib");
                } else {
                    return f.isDirectory() || name.endsWith(".png") || name.endsWith(".bmp");
                }
            }

            public String getDescription() {
                return open ? "Image (*.jpg, *.jpeg, *.png, *.gif, *.tiff, *.bmp, *.dib)" : "Image (*.png, *.bmp)";
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
            this.sourceImage = ImageIO.read(f);
            JLabel l = new JLabel(new ImageIcon(this.sourceImage));
            this.originalPane.getViewport().add(l);
            this.validate();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private void embedMessage() {
        String mess = this.message.getText();
        this.embeddedImage = this.sourceImage.getSubimage(0, 0, this.sourceImage.getWidth(), this.sourceImage.getHeight());
        this.embedMessage(this.embeddedImage, mess);
        JLabel l = new JLabel(new ImageIcon(this.embeddedImage));
        this.embeddedPane.getViewport().add(l);
        this.validate();
    }

    private void embedMessage(BufferedImage img, String mess) {
        int messageLength = mess.length();
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        int imageSize = imageWidth * imageHeight;
        if (messageLength * 8 + 32 > imageSize) {
            JOptionPane.showMessageDialog(this, "MESSAGE IS TOO LONG FOR THE SELECTED IMAGE", "ERROR", 0);
        } else {
            this.embedInteger(img, messageLength, 0, 0);
            byte[] b = mess.getBytes();

            for(int i = 0; i < b.length; ++i) {
                this.embedByte(img, b[i], i * 8 + 32, 0);
            }

        }
    }

    private void embedInteger(BufferedImage img, int n, int start, int storageBit) {
        int maxX = img.getWidth();
        int maxY = img.getHeight();
        int startX = start / maxY;
        int startY = start - startX * maxY;
        int count = 0;

        for(int i = startX; i < maxX && count < 32; ++i) {
            for(int j = startY; j < maxY && count < 32; ++j) {
                int rgb = img.getRGB(i, j);
                int bit = this.getBitValue(n, count);
                rgb = this.setBitValue(rgb, storageBit, bit);
                img.setRGB(i, j, rgb);
                ++count;
            }
        }

    }

    private void embedByte(BufferedImage img, byte b, int start, int storageBit) {
        int maxX = img.getWidth();
        int maxY = img.getHeight();
        int startX = start / maxY;
        int startY = start - startX * maxY;
        int count = 0;

        for(int i = startX; i < maxX && count < 8; ++i) {
            for(int j = startY; j < maxY && count < 8; ++j) {
                int rgb = img.getRGB(i, j);
                int bit = this.getBitValue(b, count);
                rgb = this.setBitValue(rgb, storageBit, bit);
                img.setRGB(i, j, rgb);
                ++count;
            }
        }

    }

    private void saveImage() {
        if (this.embeddedImage == null) {
            JOptionPane.showMessageDialog(this, "No message has been embedded!", "Nothing to save", 0);
        } else {
            File f = this.showFileDialog(false);
            String name = f.getName();
            String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
            if (!ext.equals("png") && !ext.equals("bmp") && !ext.equals("dib")) {
                ext = "png";
                f = new File(f.getAbsolutePath() + ".png");
            }

            try {
                if (f.exists()) {
                    f.delete();
                }

                ImageIO.write(this.embeddedImage, ext.toUpperCase(), f);
            } catch (Exception var5) {
                var5.printStackTrace();
            }

        }
    }

    private void resetInterface() {
        this.message.setText("");
        this.originalPane.getViewport().removeAll();
        this.embeddedPane.getViewport().removeAll();
        this.sourceImage = null;
        this.embeddedImage = null;
        this.sp.setDividerLocation(0.5D);
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

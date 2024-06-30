import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.Random;

class GUI {
    public static JButton s_b,done;
    public static JFrame frame;
    public static JLabel l;

    public static void page_1(){


        s_b = new JButton("CLICK HERE FOR ENCRYPTION");
        s_b.setFont(new Font("MV Boli",Font.BOLD,20));
        s_b.setBounds(60, 80, 400, 50);
        s_b.setFocusable(false);

        done = new JButton("CLICK HERE FOR DECRYPTION");
        done.setFont(new Font("MV Boli",Font.BOLD,20));
        done.setBounds(60,170,400,50);
        done.setEnabled(true);


        frame = new JFrame("STEGANOGRAPHY");
        frame.setSize(500, 400);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);


        frame.add(s_b);
        frame.add(done);

        frame.getContentPane().setBackground(new Color(220, 220, 220));
        frame.setLocation(300, 60);
        frame.setVisible(true);

        s_b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Encryption encryption = new Encryption();
                encryption.setVisible(true);
            }
        });



        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Decryption decryption = new Decryption();
                decryption.setVisible(true);
            }
        });

    }

}



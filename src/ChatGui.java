import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;


public class ChatGui extends JFrame {


    private JTextArea textArea;
    private JTextField textField;
    private String name;
    private DataInputStream in;
    private DataOutputStream out;

    public JTextArea getOnline() {
        return online;
    }

    private  JTextArea online;
    private Set<String> userOnline;


    public ChatGui(String name, DataInputStream in, DataOutputStream out) {
        this.name=name;
        this.in=in;
        this.out=out;


        setTitle(name + " is online" );
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 600);


        JPanel jpanel = new JPanel(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setForeground(new Color(29, 106, 4));
        textArea.setFont(new Font("Verdana", Font.ITALIC, 18));
        textArea.setBackground(new Color(209, 231, 188));

        online=new JTextArea("Users are online\n");
        /*
        for (String s : userOnline){
            online.append(s);
            online.append("\n");
        }

         */

        online.setBackground(Color.ORANGE);
        online.setEditable(false);


        JScrollPane scroll = new JScrollPane(textArea);
        jpanel.add(scroll, BorderLayout.CENTER);
        jpanel.add(online, BorderLayout.EAST);

        add(jpanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(50, 50));
        textField = new JTextField();
        textField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }

        });
        panel.add(textField);


        JButton sendButton = new JButton("SEND");
        sendButton.setBackground(new Color(118, 227, 73));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }

        });

        panel.add(sendButton, BorderLayout.EAST);

        add(panel, BorderLayout.SOUTH);


        setVisible(true);


    }
    public JTextArea getTextArea() {
        return textArea;
    }


    public void sendMessage() {
        if (textField.getText().isBlank()) {
            return;
        }
        if (textField.getText().startsWith("/end")){
            dispose();
        }
        if (!textField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(textField.getText());
                textField.setText("");
                textField.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
            }
        }
    }


}


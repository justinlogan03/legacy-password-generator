import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class hash_gui extends JFrame implements ActionListener {
    /*
     * Initialize gui components
     */
    JTextField key; // Text field for keyword
    JTextField pin; // Text field for pin
    JTextField output;
    JButton enter = new JButton(" Enter "); // Enter Button
    JButton kclear = new JButton("Clear Key"); // Clear Key Button
    JButton pclear = new JButton("Clear Pin"); // Clear Pin Button
    JLabel klbl = new JLabel("Type Key (Case Sensitive): "); // Label for Key
    JLabel plbl = new JLabel("Type Pin (4 digits): "); // Label for Pin
    JLabel passlbl = new JLabel("Password:  "); // Label for Password Output
    // Array of Special Characters
    String[] special = new String[] { "!", "#", "$", "%", "&", "*", "+", "-", "{", "/", ":", ";", "<", "=", ">", "?",
            "@", "~", "}" };
    // Array of Capital Letters
    String[] caps = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S",
            "T", "U" };

    /*
     * SHA512 Function
     */
    public static String SHA512(String input) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            // return the HashText
            return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Number or Not String tester Input: String value Output: True or false if the
     * String is made up of numbers
     */
    boolean numberOrNot(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /*
     * Generates returns single digit representation of ascii value Input: character
     * Output: one digit value from ascii
     */
    int asciikey(char keychar) {
        int asciikey = keychar; // converts keychar to its ascii value
        if (asciikey >= 9) {
            asciikey = asciikey % 10; // reduces value to its last digit to get it between 0 and 9
        }
        return asciikey;// returns value
    }

    /*
     * Generate Password Input: pin and keyword Strings Output: password using
     * sha512
     */
    String generatePassword(String pin, String key) {
        // concat data, add pin to key data
        String data = pin + key; // data = pin + key
        // hash data
        String sharaw = SHA512(data); // sharaw = sha512 run on data
        // calculate shift distance
        int shift = key.length(); // shift distance = length of key word
        // Create final sha value after shifting data and limiting it to 10 characters
        String shashift = sharaw.substring(shift, shift + 10);

        // Break pin into 4 usable numbers
        int numPin = Integer.parseInt(pin); // convert string to int
        int digit4 = numPin % 10; // get last digit
        numPin /= 10; // reduce pin by one digit
        numPin /= 10; // two digits
        numPin /= 10; // three digits
        int digit1 = numPin % 10; // get first digit of number
        // calculate ascii key based off first sha value and first pin digit
        char sha0 = sharaw.charAt(0); // get first character of sha
        char sha1 = sharaw.charAt(1); // get second character of sha
        int ascii0 = asciikey(sha0); // ascii value from first sha value
        int ascii1 = asciikey(sha1); // ascii value from second sha value
        System.out.println(ascii0);
        System.out.println(ascii1);
        // Combine ascii value from sha 0 to first digit for a total value between 0 and
        // 18
        int asciif = ascii0 + digit1;
        // Combine ascii value from sha 1 to last digit for a total value between 0 and
        // 18
        int asciil = ascii0 + digit4;
        String cap = caps[asciif]; // get value ascii 1 value from array
        String spe = special[asciil]; // get value ascii 4 value from array

        // add capital letter and special character to password
        String shaFinal = shashift;
        // Add capital letter to location right of ascii0 value
        shaFinal = shaFinal.substring(0, ascii0) + cap + shaFinal.substring(ascii0);
        // Add special character to location right of ascii1 value
        shaFinal = shaFinal.substring(0, ascii1) + spe + shaFinal.substring(ascii1);
        // output final password
        return shaFinal;
    }

    /*
     * Password gui interface
     */
    public hash_gui() {
        // Create and add panel
        JPanel myPanel = new JPanel();
        add(myPanel);
        // Create Action listeners for all three buttons
        enter.addActionListener(this);
        kclear.addActionListener(this);
        pclear.addActionListener(this);
        // Create Text field size for pin and key textfeilds
        pin = new JTextField(20);
        key = new JTextField(20);
        output = new JTextField(16);
        // Add everything to panel
        myPanel.add(plbl); // pin label
        myPanel.add(pin); // pin text field
        myPanel.add(klbl); // key label
        myPanel.add(key); // key text field
        myPanel.add(enter); // ender button
        myPanel.add(kclear); // key clear button
        myPanel.add(pclear); // pin clear button
        myPanel.add(passlbl);
        myPanel.add(output); // password output label
    }

    /*
     * Action Listener events to get information from gui
     */
    public void actionPerformed(ActionEvent e) {
        // Enter Button is pressed
        if (e.getSource() == enter) {
            // Get data from text fields
            String kdata = key.getText(); // key data
            String pdata = pin.getText(); // pin data
            // Test if pin is a four digit number
            if (pdata.length() != 4 || numberOrNot(pdata) == false) {
                output.setText("Error: Invalid Pin"); // Output error if it is not
            }
            else if(kdata.equals("")) {
                output.setText("Error: No Key");
            }
            // If pin is valid, generate password
            else {
                String sha = generatePassword(pdata, kdata);
                // Print password to output table
                output.setText(sha);
                System.out.println(sha);
            }
        }
        // Key Clear Button is pressed
        else if (e.getSource() == kclear) {
            // set text field to empty
            key.setText("");
        }
        // Pin clear Button is pressed
        else if (e.getSource() == pclear) {
            // set text field to empty
            pin.setText("");
        }
    }

    // gui main method
    public static void main(String args[]) {
        // create gui
        hash_gui g = new hash_gui();
        g.setLocation(100, 100);
        g.setSize(325, 200);
        g.setVisible(true);
    }

}
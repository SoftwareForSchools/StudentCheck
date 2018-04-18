/*
 * OMNIReader.java
 *
 * Created november 2012
 * 
 * De ACR Reader code is aangepast voor de OMNIKEY 5321 reader.
 * De OMNIKEY reader is geschikt voor de PC/SC (PC Smartcard) standaard.
 * 
 * Daardoor kan de standaard Java smartcard package gebruikt worden.
 *
 * 
 */
package hvabucheckin.RFID;

//import acs.jni.ACR120U; //usb rfid reader version 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.smartcardio.*;
import javax.swing.Timer;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Interface to the ACR120 rfid reader. See also ACR120U-API-doc-html docs.
 *
 * @author jand
 *
 */
public class OMNIReader {

    /**
     * last result message
     */
    private String message;
    /**
     * default sector+block to write/read card
     */
    private byte blocknr = 8, sector = 2;
    //OMNICARD
    TerminalFactory factory;                    //
    java.util.List<CardTerminal> terminals;     //Get the terminals attached
    CardTerminal terminal;                      //receive the object of terminals
    CardTerminal rfReader;
    Card card;                                  //
    CardChannel channel;                        //
    String readerName;
    boolean IsAuthenticated = true; //TODO
    int card_Type;
    byte currentBlock;
    byte keynum;
    private Timer t;
    int h = 1;
    java.awt.event.MouseEvent evt1;
    boolean cardpre;
    String license;
    int chat1 = 0, chat = 0, chat4 = 0, chat14 = 0;
    char ch1;
    //==============

    public static void main(String[] args) throws InterruptedException {
        //System.out.println("This is OMNIReader.java main() without the GUI, testing card reader.");
        OMNIReader test = new OMNIReader();
        test.openReader();
        test.connect();
        test.loadKey();
        test.authenticate(8);
        test.writeBlock(8);
        test.readBlock(8);

//        long tag = test.readTag();
//        System.out.printf("\nTag read = %X\n", tag);
        //System.out.println("will sleep 60 sec");
        Thread.sleep(60000);

    }

    /**
     * Open connection to the cardreader and keep the handle and stationId in
     * this object's fields. Use COMport in this.comport
     */
    public void openReader() {
        String reader = "";
        String readertemp;
        int count = 0;
        int index1 = 0;
        int startindex1 = 0;
        String readerarray[];

        try {
            factory = TerminalFactory.getDefault();
            terminals = factory.terminals().list();
            rfReader = terminals.get(1); // neem aan dat CL (ContactLess) nummer 1 is?
            terminal = rfReader; // old code
            //System.out.println("rfreader selected info: " + rfReader.getName());

            reader = terminals.toString();
            readerName = reader; // TODO , nu een lijst
            //System.out.println("terminals= " + reader);
            while (index1 != -1) {
                index1 = reader.indexOf(",", startindex1 + 1);
                count++;
                startindex1 = index1;
            }
            readertemp = reader;
            reader = readertemp.replaceAll("]", " ");
            readertemp = reader.replaceAll("PC/SC terminal", "");
            reader = readertemp.replace('[', ' ');
            readerarray = reader.split(",", count);
            int i = 0;
            while (i < count) {
                // TODO jComboBox1.addItem(readerarray[i].toString().trim());
                //System.out.println("TODO " + readerarray[i].toString().trim());
                i++;
            }


            //OMNIKEY CardMan 5x21 0 OMNIKEY CardMan 5x21-CL 0
        } catch (CardException e) {
            //System.out.println(e.toString());
        }

        t = new Timer(2000, taskperformer);
        t.start();

        message = "Device opened. " + reader;
    }
    ActionListener taskperformer = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            //System.out.print("Timertick.");
            if (readerName.matches("")) {
                //System.out.println("No card");
            } else {
                try {
                    cardpre = terminal.isCardPresent();
                } catch (CardException e1) {
                    //System.out.println(e1.toString());
                }
                if (!cardpre) {
                    //System.out.println("Card removed");
                } else {
                    //System.out.println("Card inserted");
                }
            }
            cardpre = false;
        }
    };

    public boolean checkOpen() {
        if (terminal == null) {
            message = "Device not open";
            System.err.println(message);
            return false;
        }
        return true;
    }

    /**
     * Reset the cardreader
     */
    public void reset() {
        //Omnikey reset?
        disconnect();
        //cardReader.power(handle, (byte)1);
        // cardReader.reset(handle);   
        message = "Omnikey disconnected";
    }

    /**
     * buzzer does not work on serial ACR120S
     */
    public void buzzer(boolean status) {
        String read_str;
        byte receive[];
        try {
            CommandAPDU c1 = new CommandAPDU(0xff, 0xb0, 0x00, currentBlock, null, 0x00, 0x00, 0x12);
            ResponseAPDU r1 = channel.transmit(c1);
            //System.out.println(r1);
            receive = r1.getBytes();
            if (bytestohex(receive).substring(bytestohex(receive).length() - 4, bytestohex(receive).length()).matches("6282")) {
                read_str = bytestohex(receive).substring(0, bytestohex(receive).length() - 4);                   
            }
        } catch (CardException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * power+led on/off
     */
    public void power(boolean power) {
        // check precondition: device must be opened
        if (!checkOpen()) {
            return;
        }
        //System.out.println("POWER " + power);
        if (power) {
            //cardReader.power(handle, (byte)1);  
            //cardReader.writeUserPort(handle,(byte)0x05); //buzzer on
        } else {
            //cardReader.writeUserPort(handle,(byte)0x00);
            //cardReader.power(handle, (byte)0);              
        }
    }

    /**
     * get the last error or status message
     */
    public String getMessage() {
        return message;
    }

    /**
     * close the reader device and COM port
     */
    void close() {
        // check precondition: device must be opened
        if (!checkOpen()) {
            return;
        }

        message = "Device closed";
    }

    /**
     * write some data in a block
     */
    public void writeString(String s) {
        // TODO: pass parameter s to writeBlock
        writeBlock(8);  // testdata naar block 8
    }

    /**
     * read som block data
     */
    public String readString() {
        connect();
        loadKey();
        authenticate(8);
        String s;
        byte[] bdata;
        bdata = readBlock(8);
        s = new String(bdata);
        System.err.println("Read block: " + s);
        return s;
    }

    /**
     * login into a sector on a card
     */
    public boolean login() {
        authenticate(8);
        return IsAuthenticated;
    }

    //Description:Authenticate the card using key 
    //APDU Description: ClassByte bcla = 0xFF, Instruction Byte bins=0x88 / 0x86 ,
    //  Parameter P1=Address MSB / 0x00 , Parameter P2=Address LSB / 0x00
    //            P3 = key type / 0x05 and 
    //            Data Bytes = keynumber / (Version,Address MSB,Address LSB,Key Type,Key Number)
    //********************************************************
    private void authenticate(int block) {
        String keych;
        byte a1[] = new byte[1];
        byte receive[];
        byte data[] = new byte[5];
        currentBlock = (byte) block;
        //System.out.println(currentBlock);
        keynum = 0;
        a1[0] = keynum;//Key Number
        try {
            data[0] = (byte) 0x1;
            data[1] = (byte) 0x0;
            data[2] = currentBlock;
            data[3] = (byte) 0x60;  // keyA = 0x60 keyB = 0x61
            data[4] = keynum;
            CommandAPDU c2 = new CommandAPDU(0xff, 0x86, 0x00, 0x00, data);
            ResponseAPDU r2 = channel.transmit(c2);
            receive = r2.getBytes();
            this.IsAuthenticated = bytestohex(receive).matches("9000");
        } catch (CardException e) {
            //System.out.println(e.toString());
        }
    }

    //**************************************************************************
    //Method for Byte to Hexidecimal conversion
    //**************************************************************************
    public String bytestohex(byte hexbyte[]) {
        String s = "";
        String s1 = "";
        int n, x;
        for (n = 0; n < hexbyte.length; n++) {
            x = (int) (0x000000FF & hexbyte[n]);  // byte to int conversion
            s = Integer.toHexString(x).toUpperCase();
            if (s.length() == 1) {
                s = "0" + s;
            }
            s1 = s1 + s;
        }
        return s1;
    }
    //**************************************************************************
    //Method unsigned to signed Byte
    //**************************************************************************

    public byte intToPseudoUnsignedByte(int n) {
        if (n < 128) {
            return (byte) n;
        }
        return (byte) (n - 256);
    }

    //**************************************************************************
    //class for Hexidecimal to Byte 
    //**************************************************************************
    public byte[] fromHexString(String s) {
        String[] v = s.split(" ");
        byte[] arr = new byte[v.length];
        int i = 0;
        for (String val : v) {
            arr[i++] = Integer.decode("0x" + val).byteValue();

        }
        return arr;
    }
    //**************************************************************************
    //Method for Hexidecimal to Byte conversion
    //**************************************************************************

    public byte[] hexToBytes(String hexString) {
        HexBinaryAdapter adapter = new HexBinaryAdapter();
        byte[] bytes = adapter.unmarshal(hexString);
        return bytes;
    }

    /**
     * Blink/sound powerled/buzzer 2 times
     */
    public void blink() {
        boolean p = false;
        for (int i = 1; i <= 5; i++) {
            buzzer(p);
            p = !p;
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    //Description: Read the data from the card
    //APDU Description: ClassByte bcla = 0xFF, Instruction Byte bins=0xB0 ,Parameter P1=Address MSB , Parameter P2=Address LSB
    //                  Le=Number of Bytes to be Read 
    //********************************************************
    private byte[] readBlock(int block) {
        String read_str;
        byte receive[] = {};
        int cb = block; // blocknumber
        currentBlock = intToPseudoUnsignedByte(cb);
        //System.out.println(currentBlock);
        if (IsAuthenticated == true) {
            try {
                CommandAPDU c1 = new CommandAPDU(0xff, 0xb0, 0x00, currentBlock, null, 0x00, 0x00, 0x12);
                ResponseAPDU r1 = channel.transmit(c1);
                //System.out.println(r1);
                receive = r1.getBytes();
                if (bytestohex(receive).substring(bytestohex(receive).length() - 4, bytestohex(receive).length()).matches("6282")) {
                    //System.out.println(bytestohex(receive));
                    read_str = bytestohex(receive).substring(0, bytestohex(receive).length() - 4);
                    //System.out.println(read_str);
                    //System.out.println("Read: " + read_str + "\n"); //log                       
                } else {
                    //System.out.println("> SCardTransmit" + "   Failed(SW1 SW2 =" + bytestohex(receive).substring(bytestohex(receive).length() - 4, bytestohex(receive).length()) + ")\n\n");
                }
            } catch (CardException e) {
                //System.out.println(e.toString());
            }
        }
        return receive;
    }

    //Write
    //********************************************************
    //Function Name:writeBlock
    //Input(Parameter) : block number 
    //OutPutParameter:-------
    //Description:Write the data into the memory
    //APDU Description: ClassByte bcla = 0xFF, Instruction Byte bins=0xD6 ,Parameter P1=Address MSB , Parameter P2=Address LSB
    //                  Lc=Number of Bytes to be Updated and Data Input = Data  
    //********************************************************
    private void writeBlock(int block) {
        String s4 = "testdata90123456";
        byte receive[];
        if (IsAuthenticated == true && s4.length() == 16) {
            currentBlock = intToPseudoUnsignedByte(block);
            byte str3[] = s4.getBytes(); // MUST BE 16 bytes!
            try {
                CommandAPDU c1 = new CommandAPDU(0xff, 0xd6, 0x00, currentBlock, str3);//0x00,0x00,0x12);
                ResponseAPDU r1 = channel.transmit(c1);
                receive = r1.getBytes();
            } catch (CardException e) {
                System.out.println(e.toString());
            }
        }
    }

    //Connect
    //********************************************************
    //Function Name: connect
    //Input(Parameter) :
    //OutPutParameter:-------
    //Description:Connect to SmartCard
    //********************************************************
    private String connect() {
        String s = "";
        String s1 = "";
        String atr_temp = "";
        String uid_temp = "";
        int atr_byte = 0;
        int n, x;

        try {
            //establish a connection with the card
            card = terminal.connect("T=1");
            //System.out.print(card);

            channel = card.getBasicChannel();
            //////////ATR////////
            ATR r2 = channel.getCard().getATR();
            byte atr[] = r2.getBytes();
            for (n = 0; n < atr.length; n++) {
                x = (int) (0x000000FF & atr[n]);  // byte to int conversion
                s = Integer.toHexString(x).toUpperCase();
                if (s.length() == 1) {
                    s = "0" + s;
                }
                s1 = s1 + s + " ";

            }
            atr_temp = s1;

            try {
                atr_byte = atr[14];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(e.toString());
            }

            //////////UID///////////   
            s1 = "";
            CommandAPDU c2 = new CommandAPDU(0xff, 0xCA, 0x00, 0x00, null, 0x00, 0x00, 0x1);
            ResponseAPDU r1 = channel.transmit(c2);
            byte uid[] = r1.getBytes();


            for (n = 0; n < uid.length - 2; n++) {
                x = (int) (0x000000FF & uid[n]);  // byte to int conversion
                s = Integer.toHexString(x).toUpperCase();
                if (s.length() == 1) {
                    s = "0" + s;
                }
                s1 = s1 + s + " ";
            }
            uid_temp = s1;

            //Card Type
            if (atr_byte == 1) {
                card_Type = 1;
            } else if (atr_byte == 2) {
                card_Type = 2;
            }
        } catch (CardException e) {
            System.out.println("> SCardConnect" + "\n   Failed... \n\n");
        }
        return s1;
    }

    public String readTag() {
        if (terminal == null) {
            message = "device not opened";
            return "";
        }
        String tag = connect();
        if(tag.equals("")){
            return tag;
        }
        ArrayList<String> bytes = new ArrayList<>();
        while (true){
            if(tag.equals("")){
                break;
            }
            bytes.add(tag.substring(0, tag.indexOf(" ")));
            tag = tag.substring(tag.indexOf(" ")+1);
        }
        
        String newSerial = "";
        for(int i = 0; i < bytes.size(); i++){
            newSerial += bytes.get(bytes.size()-1-i);
        }
        
        return newSerial;
    }

    //Disconnect
    //********************************************************
    //Function Name: disconnect
    //Input(Parameter) : evt
    //OutPutParameter:-------
    //Description:Disconnect the Smartcard
    //********************************************************
    private void disconnect() {
        try {
            // disconnect
            card.disconnect(false);
            if (card.toString().contains("DISCONNECT")) {
                //System.out.println("> SCardDisconnect\n" + "     Successful \n\n");
            }
        } catch (CardException e) {
            //System.out.println(e.toString());
        }
    }

    //Load Key
    //********************************************************
    //Function Name:
    //Input(Parameter) : 
    //OutPutParameter:-------
    //Description: Loadkey 
    //APDU Description: ClassByte bcla = 0xFF, Instruction Byte bins=0x82 ,
    // Parameter P1=Key Structure , Parameter P2=Key Number
    //                  Lc = key length and Data Bytes = key
    //********************************************************
    private void loadKey() {
        String keych1;
        

        try {
            keych1 = "FFFFFFFFFFFF"; //key            
            keynum = 0;
            byte str3[] = hexToBytes(keych1);
            CommandAPDU c1 = new CommandAPDU(0xff, 0x82, 0x20, keynum, str3);
            ResponseAPDU r1 = channel.transmit(c1);
            byte[] receive = r1.getBytes();
        } catch (CardException e) {
            System.out.println(e.toString());
        }


    }
} // end file
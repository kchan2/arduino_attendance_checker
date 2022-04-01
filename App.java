import jssc.*;

public class App {

    static SerialPort serialPort;

    public static void main(String[] args) throws SerialPortException {
        // Getting serial ports names
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
        // Event listener
        try {
            // Ka Ying's port: COM4
            serialPort = new SerialPort("COM4");
            serialPort.openPort();   //Open port
            serialPort.setParams(9600, 8, 1, 0);   //Set params
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;   //Prepare mask
            serialPort.setEventsMask(mask);   //Set mask
            serialPort.addEventListener(new SerialPortReader());   //Add SerialPortEventListener
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    static class SerialPortReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {   //If data is available
                if (event.getEventValue() == 10) {   //Check bytes count in the input buffer
                    //Read data, if 10 bytes available
                    try {
                        byte buffer[] = serialPort.readBytes(10);
                        // Temporary action
                        System.out.println(new String(buffer));
                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                }
            } else if (event.isCTS()) {   //If CTS line has changed state
                if (event.getEventValue() == 1) {   //If line is ON
                    System.out.println("CTS - ON");
                } else {
                    System.out.println("CTS - OFF");
                }
            } else if (event.isDSR()) {   //If DSR line has changed state
                if (event.getEventValue() == 1) {   //If line is ON
                    System.out.println("DSR - ON");
                } else {
                    System.out.println("DSR - OFF");
                }
            }
        }
    }
}
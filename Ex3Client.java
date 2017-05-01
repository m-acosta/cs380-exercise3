import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.xml.bind.DatatypeConverter;

/**
 * @author Michael Acosta
 *
 */

public class Ex3Client {

	public static void main(String[] args) {
		try {
			Socket mySocket = new Socket("codebank.xyz", 38103);
			System.out.println("Connected to server.");
			
			DataInputStream input = new DataInputStream(mySocket.getInputStream());
			int bytesToSend = input.read();
			System.out.println("Reading " + bytesToSend + " bytes.");
			
			byte[] message = new byte[bytesToSend];
			for (int i = 0; i < message.length; i++) {
				message[i] = input.readByte();
			}
			
			System.out.println("Data received:\n\t" + DatatypeConverter.printHexBinary(message));
			
			short sum = checksum(message);
			System.out.println("Checksum calculated: 0x" + Integer.toHexString(sum & 0xFFFF).toUpperCase());
			
			DataOutputStream output = new DataOutputStream(mySocket.getOutputStream());
			output.writeShort(sum);
			
			byte response = input.readByte();
			if (response == 1) {
				System.out.println("Response good.");
			}
			else if (response == 0) {
				System.out.println("Response bad.");
			}
			
			input.close();
			output.close();
			mySocket.close();
		} catch (Exception e) {
			System.out.println(e);	
		}

	}
	
	public static short checksum(byte[] b) {
		int sum = 0, i = 0, count = b.length;
		
		while (count > 1) {
			sum += ((b[i] << 8) & 0xFF00 | (b[i + 1]) & 0xFF);
			if ((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum++;
			}
			count -= 2;
			i += 2;
		}
		
		if (count > 0) {
			sum += ((b[i] << 8) & 0xFF00);
			if ((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum++;
			}
		}
		
		return (short) ~(sum & 0xFFFF);
	}

}

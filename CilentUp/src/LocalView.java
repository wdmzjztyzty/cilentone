

import java.io.DataOutputStream;
import java.net.Socket;

import org.apache.commons.codec.binary.Base64;
import org.bridj.Pointer;

import com.github.sarxos.webcam.ds.buildin.natives.Device;
import com.github.sarxos.webcam.ds.buildin.natives.DeviceList;
import com.github.sarxos.webcam.ds.buildin.natives.OpenIMAJGrabber;

public class LocalView {
	
	public static void main(String[] args) {
			
		/**
		 * This example show how to use native OpenIMAJ API to capture raw bytes
		 * data as byte[] array. It also calculates current FPS.
		*/
		try{
			OpenIMAJGrabber grabber = new OpenIMAJGrabber();
			
			Socket s = new Socket("113.250.156.162",11111);
			DataOutputStream os = new DataOutputStream(s.getOutputStream());
			Device device = null;
			Pointer<DeviceList> devices = grabber.getVideoDevices();
			for (Device d : devices.get().asArrayList()) {
				device = d;
				break;
			}
	
			boolean started = grabber.startSession(320, 240, 30, Pointer.pointerTo(device));
			if (!started) {
				throw new RuntimeException("Not able to start native grabber!");
			}
	
			int n = 1000;
			int i = 0;
			do {
				Thread.sleep(200);
				/* Get a frame from the webcam. */
				grabber.nextFrame();
				/* Get the raw bytes of the frame. */
				byte[] raw_image=grabber.getImage().getBytes(320 * 240 * 3);
				/* Apply a crude kind of image compression. */
				byte[] compressed_image = Compressor.compress(raw_image);
				/* Prepare the date to be sent in a text friendly format. */
				byte[] base64_image = Base64.encodeBase64(compressed_image);
				
				os.writeShort(base64_image.length);
				os.write(base64_image);
				os.flush();
				
			} while (++i < n);
			grabber.stopSession();
			
		}catch(Exception e){
			e.printStackTrace();			
		}
	}
	
}

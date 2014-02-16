

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.example.dumbtest.AndroidBitmapBppConverter;

public class BitmapBppConverterTest {

	@Test
	public void testConvertIntToByteArrayRepresentation() {
		byte[] expectedArray = new byte[]{1,0,0,0};
		byte[] actualArray = AndroidBitmapBppConverter.convertIntToByteArrayOfLength4(1);
		assertEquals(Arrays.toString(expectedArray),Arrays.toString(actualArray));	
	}

	@Test
	public void testConvertIntToByteArrayRepresentationDoubleDigit() {
		byte[] expectedArray = new byte[]{10,0,0,0};
		byte[] actualArray = AndroidBitmapBppConverter.convertIntToByteArrayOfLength4(10);
		assertEquals(Arrays.toString(expectedArray),Arrays.toString(actualArray));	
	}

	@Test
	public void testConvertIntToByteArrayRepresentationLargerThanByte() {
		byte[] expectedArray = new byte[]{0x58,0x7c, 0x00, 0x00};
		byte[] actualArray = AndroidBitmapBppConverter.convertIntToByteArrayOfLength4(31832);
		assertEquals(Arrays.toString(expectedArray),Arrays.toString(actualArray));	
	}

	@Test
	public void testConvertIntToByteArrayRepresentationLargerThanByteConsiderSignedBytes() {
		byte[] expectedArray = new byte[]{0x06,(byte)0xde, 0x06, 0x00};
		byte[] actualArray = AndroidBitmapBppConverter.convertIntToByteArrayOfLength4(450054);
		assertEquals(Arrays.toString(expectedArray),Arrays.toString(actualArray));	
	}

}

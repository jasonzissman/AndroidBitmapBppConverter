

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Utility to manipulate Android Bitmap objects
 * 
 * @author Jason Zissman
 * 
 */
public class AndroidBitmapBppConverter {

	// Bitmap info taken from http://atlc.sourceforge.net/bmp.html
	public static final byte OFFSET_IDENTIFIER = 0x00;
	public static final byte OFFSET_FILE_SIZE = 0x02;
	public static final byte OFFSET_BITMAP_DATA_OFFSET = 0x0a;
	public static final byte OFFSET_BITMAP_HEADER_SIZE = 0x0e;
	public static final byte OFFSET_WIDTH = 0x12;
	public static final byte OFFSET_HEIGHT = 0x16;
	public static final byte OFFSET_NUMBER_OF_PLANES = 0x1a;
	public static final byte OFFSET_BITS_PER_PIXEL = 0x1c;
	public static final byte OFFSET_COMPRESSION = 0x1e;
	public static final byte OFFSET_BITMAP_DATA_SIZE = 0x22;
	public static final byte OFFSET_HORIZONTAL_RESOLUTION = 0x26;
	public static final byte OFFSET_VERTICAL_RESOLUTION = 0x2a;
	public static final byte OFFSET_COLORS = 0x2e;
	public static final byte OFFSET_IMPORTANT_COLORS = 0x32;
	public static final byte OFFSET_PALETTE = 0x36;
	public static final byte OFFSET_BITMAP_DATA = 0x36;

	/**
	 * Converts an Android Bitmap object to 24bpp
	 * 
	 * @param bitmap
	 *            Android Bitmap object to convert. Note: bitmap dimensions will
	 *            be truncated to be divisible by 4. This could cause a loss of
	 *            up to 3 rows or 3 columns of image data in resulting byte
	 *            array.
	 * @return byte array representing the file data of the resulting 24bpp
	 *         bitmap.  Windows-friendly.
	 */
	public static byte[] get24BppBitmapFileData(Bitmap bitmap) {

		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		// We need dimensions divisible by 4. So, we truncate.
		height = height - (height % 4);
		width = width - (width % 4);
		
		int bitmapDataSize = 3 * height * width;
		byte[] newBitmapData = new byte[OFFSET_BITMAP_DATA + bitmapDataSize];
		
		updateStaticHeaderInfo(newBitmapData);
		updateFileSizeHeader(newBitmapData);
		updateBitmapWidthHeader(newBitmapData, width);
		updateBitmapHeightHeader(newBitmapData, height);
		updateBitmapDataSizeHeader(newBitmapData, bitmapDataSize);
		updateBitmapData(newBitmapData, bitmap, height, width);

		return newBitmapData;
	}

	private static void updateBitmapData(byte[] newBitmapData, Bitmap bitmap, int height, int width) {
		// Traverse pixels from bottom to top, from left to right
		int newBitmapIndex = OFFSET_BITMAP_DATA;
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				int currentPixel = bitmap.getPixel(x, y);
				byte redValue = (byte) Color.red(currentPixel);
				byte greenValue = (byte) Color.green(currentPixel);
				byte blueValue = (byte) Color.blue(currentPixel);

				newBitmapData[newBitmapIndex] = blueValue;
				newBitmapIndex++;
				newBitmapData[newBitmapIndex] = greenValue;
				newBitmapIndex++;
				newBitmapData[newBitmapIndex] = redValue;
				newBitmapIndex++;
			}
		}
	}

	private static void updateStaticHeaderInfo(byte[] newBitmapData) {
		newBitmapData[OFFSET_IDENTIFIER] = 0x42;
		newBitmapData[OFFSET_IDENTIFIER + 1] = 0x4d;
		newBitmapData[OFFSET_BITMAP_HEADER_SIZE] = 0x28;
		newBitmapData[OFFSET_NUMBER_OF_PLANES] = 0x01;
		newBitmapData[OFFSET_BITMAP_DATA_OFFSET] = OFFSET_BITMAP_DATA;
		newBitmapData[OFFSET_BITS_PER_PIXEL] = 0x18;
	}

	private static void updateBitmapDataSizeHeader(byte[] newBitmapData, int bitmapDataSize) {
		byte[] bitmapDataSizeArray = convertIntToByteArrayOfLength4(bitmapDataSize);
		newBitmapData[OFFSET_BITMAP_DATA_SIZE] = bitmapDataSizeArray[0];
		newBitmapData[OFFSET_BITMAP_DATA_SIZE + 1] = bitmapDataSizeArray[1];
		newBitmapData[OFFSET_BITMAP_DATA_SIZE + 2] = bitmapDataSizeArray[2];
		newBitmapData[OFFSET_BITMAP_DATA_SIZE + 3] = bitmapDataSizeArray[3];
	}

	private static void updateBitmapHeightHeader(byte[] newBitmapData, int height) {
		byte[] bitmapHeightBytes = convertIntToByteArrayOfLength4(height);
		newBitmapData[OFFSET_HEIGHT] = bitmapHeightBytes[0];
		newBitmapData[OFFSET_HEIGHT + 1] = bitmapHeightBytes[1];
		newBitmapData[OFFSET_HEIGHT + 2] = bitmapHeightBytes[2];
		newBitmapData[OFFSET_HEIGHT + 3] = bitmapHeightBytes[3];
	}

	private static void updateBitmapWidthHeader(byte[] newBitmapData,int width) {
		byte[] bitmapWidthBytes = convertIntToByteArrayOfLength4(width);
		newBitmapData[OFFSET_WIDTH] = bitmapWidthBytes[0];
		newBitmapData[OFFSET_WIDTH + 1] = bitmapWidthBytes[1];
		newBitmapData[OFFSET_WIDTH + 2] = bitmapWidthBytes[2];
		newBitmapData[OFFSET_WIDTH + 3] = bitmapWidthBytes[3];
	}

	private static void updateFileSizeHeader(byte[] newBitmapData) {
		byte[] fileSizeBytes = convertIntToByteArrayOfLength4(newBitmapData.length);
		newBitmapData[OFFSET_FILE_SIZE] = fileSizeBytes[0];
		newBitmapData[OFFSET_FILE_SIZE + 1] = fileSizeBytes[1];
		newBitmapData[OFFSET_FILE_SIZE + 2] = fileSizeBytes[2];
		newBitmapData[OFFSET_FILE_SIZE + 3] = fileSizeBytes[3];
	}

	public static byte[] convertIntToByteArrayOfLength4(int intValue) {
		return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(intValue).array();
	}

}

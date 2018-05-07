package dotzip

import javax.imageio.ImageIO
import java.nio.ByteBuffer

class BitmapEncrypter {
    private BMP bmp
    private char[] xMessageBinary
    private char[] sizeBinaryMessage
    private final byte ENCRYPTED_FILE_FLAG = "/".bytes[0] // {47}

    BitmapEncrypter(String pathToFile, String xMessage){
        this.bmp = new BMP()
        this.xMessageBinary = bytesToBinary(xMessage.bytes)
        this.sizeBinaryMessage = bytesToBinary(xMessageBinary.size().byteValue())
        bmp.readBitmapPicture(pathToFile)
    }

    BitmapEncrypter(String pathToFile){
        this.bmp = new BMP()
        bmp.readBitmapPicture(pathToFile)
    }

    def bytesToBinary = { byte[] inputByteArray ->
        char[] resArray = new char[inputByteArray.size() * 8], tempArray
        for (int i = 0; i < inputByteArray.size(); i++) {
            if (inputByteArray[i] < 0) {
                inputByteArray[i] = 128 + inputByteArray[i]
                resArray[0] = '1'
                for (int k = 1; k < resArray.size(); k++) { resArray[k] = '0' }
            } else { for (int k = 0; k < resArray.size(); k++) { resArray[k] = '0' } }

            tempArray = new BigInteger(inputByteArray[i]).toString(2).toCharArray()
            for (int j = 0; j < tempArray.size(); j++) {
                resArray[resArray.size() - 1 - j] = tempArray[tempArray.size() - 1 - j] =~ /[0-9]+/ ? tempArray[tempArray.size() - 1 - j] : '0'
            }
        }
        return resArray
    }

    def binaryToBytes = { char[] bits ->
        byte[] array = new byte[4]
        byte[] temp = new BigInteger(bits.toString(), 2).toByteArray()
        for (int i = 0; i < temp.size(); i++) {
            array[array.size()-1 - i] = temp[temp.size()-1 - i] =~ /\d+/ ? temp[temp.size()-1 - i] : 0 as byte
        }
        for (int i = 0; i < array.size(); i++) {
            array[i] = array[i] =~ /\d+/ ? array[i] : 0 as byte
        }
        return array
    }

    void setEncryptionFlag(){
        char[] encryptedFileFlagBinary = bytesToBinary(ENCRYPTED_FILE_FLAG), binaryColor
        byte[] newRed = new byte[4], newGreen = new byte[4], newBlue = new byte[4]
        int[] colorArray = bmp.getPixelRGB(0, 0)
        // @red
        binaryColor = bytesToBinary(colorArray[0].byteValue())
        binaryColor[6] = encryptedFileFlagBinary[0]
        binaryColor[7] = encryptedFileFlagBinary[1]
        newRed = binaryToBytes(binaryColor)
        // @green
        binaryColor = bytesToBinary(colorArray[1].byteValue())
        binaryColor[5] = encryptedFileFlagBinary[2]
        binaryColor[6] = encryptedFileFlagBinary[3]
        binaryColor[7] = encryptedFileFlagBinary[4]
        newGreen = binaryToBytes(binaryColor)
        // @blue
        binaryColor = bytesToBinary(colorArray[2].byteValue())
        binaryColor[5] = encryptedFileFlagBinary[5]
        binaryColor[6] = encryptedFileFlagBinary[6]
        binaryColor[7] = encryptedFileFlagBinary[7]
        newBlue = binaryToBytes(binaryColor)

        colorArray = [ByteBuffer.wrap(newRed).int,
                      ByteBuffer.wrap(newGreen).int,
                      ByteBuffer.wrap(newBlue).int]
        bmp.setPixelRGB(0, 0, colorArray)
    }

    boolean isEncrypted(){
        int[] colorArray = bmp.getPixelRGB(0, 0)
        char[] encryptedFileFlagBinary = new char[8]
        char[] binaryColor = new char[8]
        // get needful bits from color pixel 0.0
        // @red
        binaryColor = bytesToBinary(colorArray[0].byteValue())
        encryptedFileFlagBinary[0] = binaryColor[6]
        encryptedFileFlagBinary[1] = binaryColor[7]
        // @green
        binaryColor = bytesToBinary(colorArray[1].byteValue())
        encryptedFileFlagBinary[2] = binaryColor[5]
        encryptedFileFlagBinary[3] = binaryColor[6]
        encryptedFileFlagBinary[4] = binaryColor[7]
        // @blue
        binaryColor = bytesToBinary(colorArray[2].byteValue())
        encryptedFileFlagBinary[5] = binaryColor[5]
        encryptedFileFlagBinary[6] = binaryColor[6]
        encryptedFileFlagBinary[7] = binaryColor[7]

        // check if result is equal to ENCRYPTED_FILE_FLAG
        return ByteBuffer.wrap(binaryToBytes(encryptedFileFlagBinary)).int == ENCRYPTED_FILE_FLAG
    }

    def writeEncryptedBitmap(String path, String fileName){
        ImageIO.write(bmp.bufferedImage, "BMP", new File("$path\\$fileName"))
    }
}
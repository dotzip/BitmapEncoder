package dotzip

import javax.imageio.ImageIO
import java.nio.ByteBuffer

class BitmapEncrypter {
    private BMP bmp
    private byte[] xMessageBytes
    private final byte ENCRYPTED_FILE_FLAG = "/".bytes[0] // {47}

    BitmapEncrypter(String pathToFile){
        this.bmp = new BMP() ; bmp.readBitmapPicture(pathToFile)
    }

    private def bytesToBinary = { byte[] inputByteArray ->
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

    private def binaryToBytes = { char[] bits ->
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

    private def setEncryptionFlag = {
        char[] encryptedFileFlagBinary = bytesToBinary(ENCRYPTED_FILE_FLAG), binaryColor
        byte[] newRed, newGreen, newBlue
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
        char[] encryptedFileFlagBinary = new char[8], binaryColor
        int[] colorArray = bmp.getPixelRGB(0, 0)
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

    private def setSizeOfXMessage = {
        char[] sizeXMessage = bytesToBinary(xMessageBytes.size().byteValue()), binaryColor
        byte[] newRed, newGreen, newBlue
        int[] colorArray = bmp.getPixelRGB(0, 1)
        // @red
        binaryColor = bytesToBinary(colorArray[0].byteValue())
        binaryColor[6] = sizeXMessage[0]
        binaryColor[7] = sizeXMessage[1]
        newRed = binaryToBytes(binaryColor)
        // @green
        binaryColor = bytesToBinary(colorArray[1].byteValue())
        binaryColor[5] = sizeXMessage[2]
        binaryColor[6] = sizeXMessage[3]
        binaryColor[7] = sizeXMessage[4]
        newGreen = binaryToBytes(binaryColor)
        // @blue
        binaryColor = bytesToBinary(colorArray[2].byteValue())
        binaryColor[5] = sizeXMessage[5]
        binaryColor[6] = sizeXMessage[6]
        binaryColor[7] = sizeXMessage[7]
        newBlue = binaryToBytes(binaryColor)

        colorArray = [ByteBuffer.wrap(newRed).int,
                      ByteBuffer.wrap(newGreen).int,
                      ByteBuffer.wrap(newBlue).int]
        bmp.setPixelRGB(0, 1, colorArray)
    }

    short getSizeOfXMessage(){
        char[] sizeOfXMessage = new char[8], binaryColor
        int[] colorArray = bmp.getPixelRGB(0, 1)
        // get needful bits from color pixel 0.1
        // @red
        binaryColor = bytesToBinary(colorArray[0].byteValue())
        sizeOfXMessage[0] = binaryColor[6]
        sizeOfXMessage[1] = binaryColor[7]
        // @green
        binaryColor = bytesToBinary(colorArray[1].byteValue())
        sizeOfXMessage[2] = binaryColor[5]
        sizeOfXMessage[3] = binaryColor[6]
        sizeOfXMessage[4] = binaryColor[7]
        // @blue
        binaryColor = bytesToBinary(colorArray[2].byteValue())
        sizeOfXMessage[5] = binaryColor[5]
        sizeOfXMessage[6] = binaryColor[6]
        sizeOfXMessage[7] = binaryColor[7]

        return ByteBuffer.wrap(binaryToBytes(sizeOfXMessage)).int
    }

    void encryptXMessage(String xMessage){
        this.xMessageBytes = xMessage.bytes
        char[] xMessageBinary = bytesToBinary(xMessageBytes)
        if (this.encrypted) { println "Bitmap pic is already contained message!"; return }
        else{ setEncryptionFlag() ; println "Successful setting flag!" }
        setSizeOfXMessage()
        /*for (int i = 0; i < bmp.bitmapWidth; i++) {
            for (int j = 0; j < bmp.bitmapHeight; j++) {

            }
        }*/
    }

    void saveEncryptedBitmap(String path, String fileName) {
        ImageIO.write(bmp.bufferedImage, "BMP", new File("$path\\$fileName"))
    }
}
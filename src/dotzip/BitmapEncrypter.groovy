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
            // java bytes range = -128..127 instead of 0..255
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

    private int pixelContentGetter(int x, int y){
        char[] contentBinary = new char[8], binaryColor
        int[] colorArray = bmp.getPixelRGB(x, y)
        // get needful bits from color pixel (x; y)
        // @red
        binaryColor = bytesToBinary(colorArray[0].byteValue())
        contentBinary[0] = binaryColor[6]
        contentBinary[1] = binaryColor[7]
        // @green
        binaryColor = bytesToBinary(colorArray[1].byteValue())
        contentBinary[2] = binaryColor[5]
        contentBinary[3] = binaryColor[6]
        contentBinary[4] = binaryColor[7]
        // @blue
        binaryColor = bytesToBinary(colorArray[2].byteValue())
        contentBinary[5] = binaryColor[5]
        contentBinary[6] = binaryColor[6]
        contentBinary[7] = binaryColor[7]

        return ByteBuffer.wrap(binaryToBytes(contentBinary)).int
    }

    private def newPixelSetter (char[] newBits, int x, int y) {
        byte[] newRed, newGreen, newBlue
        char[] binaryColor
        int[] colorArray = bmp.getPixelRGB(x, y)
        // get needful bits from color pixel (x; y)
        // @red
        binaryColor = bytesToBinary(colorArray[0].byteValue())
        binaryColor[6] = newBits[0]
        binaryColor[7] = newBits[1]
        newRed = binaryToBytes(binaryColor)
        // @green
        binaryColor = bytesToBinary(colorArray[1].byteValue())
        binaryColor[5] = newBits[2]
        binaryColor[6] = newBits[3]
        binaryColor[7] = newBits[4]
        newGreen = binaryToBytes(binaryColor)
        // @blue
        binaryColor = bytesToBinary(colorArray[2].byteValue())
        binaryColor[5] = newBits[5]
        binaryColor[6] = newBits[6]
        binaryColor[7] = newBits[7]
        newBlue = binaryToBytes(binaryColor)

        // new color array
        colorArray = [ByteBuffer.wrap(newRed).int,
                      ByteBuffer.wrap(newGreen).int,
                      ByteBuffer.wrap(newBlue).int]
        bmp.setPixelRGB(x, y, colorArray)
    }

    private def setEncryptionFlag = {
        char[] encryptedFileFlagBinary = bytesToBinary(ENCRYPTED_FILE_FLAG)
        newPixelSetter(encryptedFileFlagBinary, 0, 0)
    }

    boolean isEncrypted(){
        // check if result is equal to ENCRYPTED_FILE_FLAG
        return pixelContentGetter(0,0) == ENCRYPTED_FILE_FLAG
    }

    private def setSizeOfXMessage = {
        char[] sizeXMessage = bytesToBinary(xMessageBytes.size().byteValue())
        newPixelSetter(sizeXMessage, 0, 1)
    }

    private short getSizeOfXMessage(){
        return pixelContentGetter(0,1)
    }

    void encryptXMessage(String xMessage){
        this.xMessageBytes = xMessage.bytes
        char[] xMessageBinary
        int sizeCounter

        if (this.encrypted) { println "Bitmap pic is already contained message!"; return }
        else{ setEncryptionFlag() }
        setSizeOfXMessage()
        for (int i = 0; i < bmp.bitmapWidth; i++) {
            for (int j = 2; j < bmp.bitmapHeight; j++) {
                if(sizeCounter < xMessageBytes.size()){
                    xMessageBinary = bytesToBinary(xMessageBytes[sizeCounter])
                    newPixelSetter(xMessageBinary, i, j)
                    sizeCounter++
                }else{ println "Pic was successfully encrypted"; return}
            }
        }
        println "Pic was successfully encrypted"
    }

    String decryptXMessage(){
        if(this.encrypted){
            int sizeOfXMessage = this.sizeOfXMessage, sizeCounter = 0
            xMessageBytes = new byte[sizeOfXMessage]
            for (int i = 0; i < bmp.bitmapWidth; i++) {
                for (int j = 2; j < bmp.bitmapHeight; j++) {
                    if(sizeCounter < sizeOfXMessage){
                        xMessageBytes[sizeCounter] = pixelContentGetter(i, j)
                        sizeCounter++
                    } else {
                        return new String(xMessageBytes)
                    }
                }
            }
        }else{ return "Bitmap pic isn't contain message!" }
    }

    void saveEncryptedBitmap(String path, String fileName) {
        ImageIO.write(bmp.bufferedImage, "BMP", new File("$path\\$fileName"))
    }
}
package dotzip

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

/*char[] test = ['0', '0', '1', '0', '1', '1', '1', '1']
assert ByteBuffer.wrap(binaryToBytes(bytesToBinary(47.byteValue()))).int == 47*/
BitmapEncrypter bitmapEncrypter = new BitmapEncrypter("C:\\Users\\Public\\Pictures\\Sample Pictures\\BMP_pic.bmp")
bitmapEncrypter.encryptXMessage("SecretSecret message")
println bitmapEncrypter.sizeOfXMessage
//bitmapEncrypter.writeEncryptedBitmap("C:\\Users\\Public\\Pictures\\Sample Pictures", "RESBITMAP.bmp")
/*
def xMessage = "Secret message Secret message Secret message Secret message Secret message"
println bytesToBinary(xMessage.bytes.size().byteValue())*/

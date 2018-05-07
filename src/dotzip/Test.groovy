package dotzip

/*char[] test = ['0', '0', '1', '0', '1', '1', '1', '1']
assert ByteBuffer.wrap(binaryToBytes(bytesToBinary(47.byteValue()))).int == 47*/
BitmapEncrypter bitmapEncrypter = new BitmapEncrypter("C:\\Users\\Public\\Pictures\\Sample Pictures\\BMP_pic.bmp")
bitmapEncrypter.setEncryptionFlag()
println bitmapEncrypter.isEncrypted()
bitmapEncrypter.writeEncryptedBitmap("C:\\Users\\Public\\Pictures\\Sample Pictures", "RESBITMAP.bmp")

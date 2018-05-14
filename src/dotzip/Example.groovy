package dotzip

/**
 * @example1 how to encrypt
 * */
BitmapEncrypter bitmapEncrypter1 = new BitmapEncrypter("C:\\Users\\Public\\Pictures\\Sample Pictures\\BMP_pic.bmp")
bitmapEncrypter1.encryptXMessage("Hello, it's LSB encryption!")
bitmapEncrypter1.saveEncryptedBitmap("C:\\Users\\Public\\Pictures\\Sample Pictures", "BMP_pic_encrypted.bmp")

/**
 * @example2 how to decrypt
 * */
BitmapEncrypter bitmapEncrypter2 = new BitmapEncrypter("C:\\Users\\Public\\Pictures\\Sample Pictures\\BMP_pic_encrypted.bmp")
println bitmapEncrypter2.encrypted
println bitmapEncrypter2.decryptXMessage()

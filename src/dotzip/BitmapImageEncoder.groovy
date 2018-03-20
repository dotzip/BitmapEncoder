package dotzip

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.file.Files

def messageToBinary = { String message ->
    StringBuilder binaryMessage = new StringBuilder()
    message.bytes.each { binaryMessage.append(Integer.toBinaryString(it)) }
    return binaryMessage
}

def readImageBytes = { String path ->
    return fileContent = Files.readAllBytes(new File(path).toPath())
}

def imageBytesToBits = { byte[] imageBytes ->
    StringBuilder stringBuilder = new StringBuilder()
    imageBytes.each {
        stringBuilder.append(String.format("%8s", Integer.toBinaryString(it & 0xFF)).replace(' ', '0'))
    }
    return stringBuilder
 }

def LSB = { String messageBits, String originalImageBits ->
    int index = 431
    StringBuilder encodeImageBits = new StringBuilder(originalImageBits)
    messageBits.each { messageBit ->
        while(index <= originalImageBits.length()) {
            encodeImageBits.replace(index, index+1, messageBit)
            break
        }
        index += 8
    }
    return encodeImageBits
}

def bitsToBytes = { String encodeImageBits ->
    ArrayList<Byte> resultBytes = new ArrayList<>()
    for (int i = 0; i < encodeImageBits.length(); i+=24) {
        int a = Integer.parseInt(encodeImageBits[i..i+23], 2)
        ByteBuffer byteBuffer = ByteBuffer.allocate(4).putInt(a)
        byteBuffer.array().eachWithIndex{ byte entry, int index ->
            if(index != 0){ resultBytes.add(entry) }
        }
    }
    return resultBytes
}

def writeMessageToBitmap = { String message, String pathToImage, String imageName ->
    String originalImageBits = imageBytesToBits(readImageBytes("$pathToImage\\$imageName"))
    byte[] finalFileBytes = bitsToBytes(LSB(messageToBinary(message) as String, originalImageBits) as String)
    BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(finalFileBytes))
    ImageIO.write(resultImage, "BMP", new File("$pathToImage\\encodeBitmap.bmp"))
}


//Call
writeMessageToBitmap(
        "Secret message",
        "C:\\Users\\Public\\Pictures\\Sample Pictures",
        "BMP_pic.bmp"
)





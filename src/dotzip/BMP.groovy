package dotzip

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage

class BMP implements BitmapPictureInterface {
    private BufferedImage image

    @Override
    void readBitmapPicture(String pathToFile) {
        image = ImageIO.read(new File(pathToFile))
    }

    @Override
    int getBitmapHeight() {
        return image.getHeight()
    }

    @Override
    int getBitmapWidth() {
        return image.getWidth()
    }

    @Override
    BufferedImage getBufferedImage() {
        return image
    }

    @Override
    int[] getPixelRGB(int x, int y) {
        int[] pixelRGB = new int[3]
        Color pixelColor = new Color(image.getRGB(x, y))

        pixelRGB[0] = pixelColor.red
        pixelRGB[1] = pixelColor.green
        pixelRGB[2] = pixelColor.blue

        return pixelRGB
    }

    @Override
    void setPixelRGB(int x, int y, int[] pixelRGB) {
        int newPixelColor = new Color(pixelRGB[0], pixelRGB[1], pixelRGB[2]).RGB
        image.setRGB(x, y, newPixelColor)
    }
}
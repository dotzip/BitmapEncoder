package dotzip

import java.awt.image.BufferedImage

interface BitmapPictureInterface {

    void readBitmapPicture(String path)

    BufferedImage getBufferedImage()

    int getBitmapHeight()

    int getBitmapWidth()

    int[] getPixelRGB(int x, int y)

    void setPixelRGB(int x, int y, int[] pixelRGB)
}

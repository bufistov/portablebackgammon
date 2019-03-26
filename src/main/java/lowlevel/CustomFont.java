package lowlevel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.awt.image.ImageObserver;

public class CustomFont {

    private static int availWidth = 50000;// this is for limiting, if its 1000 we`re not limiting it now
    private static int availHeight = 50000;

    private ImageObserver imageObserver;

    public static CustomFont getFont(Image i, int inStyle, int inSize, boolean landscape, int compensator_,
                                     int amountOfLetters_, int widthBetweenPixelsCompensator_, int gap_,
                                     boolean offsetCorrectionRequired_, ImageObserver imageObserver) {
        System.out.println("font created.");
        return new CustomFont(i, inSize, inStyle, landscape, compensator_, amountOfLetters_,
            widthBetweenPixelsCompensator_, gap_, offsetCorrectionRequired_, imageObserver);
    }

    private CustomFont(Image inImage, int inStyle, int inSize, boolean landscape, int compensator_,
                       int amountOfLetters_, int widthBetweenPixelsCompensator_,
                       int gap_, boolean offsetCorrectionRequired_, ImageObserver imageObserver) {
        this.imageObserver = imageObserver;
        isLandscape = false;
        charHeight = 0;
        charWidth = 0;
        compensator = 33;
        gap = 0;
        widthBetweenPixelsCompensator = 0;
        offsetCorrectionRequired = false;
        clipX = 0;
        clipY = 0;
        clipW = 0;
        clipH = 0;
        compensator = compensator_;
        isLandscape = landscape;
        amountOfLetters = amountOfLetters_;
        image = inImage;

        if (image == null) {
            Utils._E("customfont image is null!");
        }
        widthBetweenPixelsCompensator = widthBetweenPixelsCompensator_;
        gap = gap_;
        offsetCorrectionRequired = offsetCorrectionRequired_;
        try {
            height = image.getHeight(imageObserver) + 2;
            charHeight = height;
            width = image.getWidth(imageObserver) / amountOfLetters_;
            charWidth = width - widthBetweenPixelsCompensator;
            Utils.log("charWidth:" + charWidth);
        } catch(Throwable t) {
            t.printStackTrace();
            throw new IllegalArgumentException("Specified font is invalid: " + t);
        }
    }

    public int charsWidth(char ch[], int offset, int length)
    {
        return length * charWidth;
    }

    public int getWidth() {
        return charWidth;
    }

    public int getHeight()
    {
        return height-1;
    }

    public int stringWidth(String str) {
        return str.length() * getWidth();//5;
    }

    public void drawChars(Graphics g, char data[], int offset, int length, int x, int y, int anchor) {
        if(isLandscape) {
            if((anchor & 8) != 0) {
                x += height * length;
            } else {
                if((anchor & 1) != 0)
                    x += (height * length) / 2;
            }
            if((anchor & 0x20) != 0) {
                y -= height;
            } else {
                if((anchor & 2) != 0)
                    y += height / 2;
            }
        } else {
            if((anchor & 8) != 0) {
                x -= charsWidth(data, offset, length);
            } else {
                if((anchor & 1) != 0)
                    x -= charsWidth(data, offset, length) / 2;
            }
            if((anchor & 0x20) != 0) {
                y -= height;
            } else {
                if((anchor & 2) != 0)
                    y -= height / 2;
            }
        }
        for(int i = 0; i < length; i++) {
            char c = data[offset + i];
            drawCharInternal(g, c, x, y, 0x10 | 4, i);
            if(isLandscape)
                x -= charHeight;
            else
                x += charWidth;
        }
    }

    private void drawCharInternal(Graphics g, char character, int x, int y, int anchor, int charsAlong) {
        Shape s = g.getClip();
        int comps = compensator;
        if(isLandscape) {
            if(character != ' ') {
               g.drawImage(image, width * (character - compensator), 0,
                   width, height, 0, y, x - charsAlong * gap - height, anchor, imageObserver);
         
            }
        } else {
            if(character == ' '){
                return;
            }
			if ( x>availWidth || y>availHeight+15 ) {//15 buffer period //specific to SCROLL PROJECT
				cout("out of clip for "+(char) (character - comps)+" clipW was "+clipW+" and x was: "+x);
			} else {
			    g.setClip(x, y, width, height);
				g.drawImage(image, x - width * (character - comps), y, imageObserver);
				g.setClip(s);//clipX, clipY, clipW, clipH);
			}
        }
    }

    public void drawString(Graphics g, String str, int x, int y, int anchor) {
        drawChars(g, str.toCharArray(), 0, str.length(), x, y, anchor);
    }

    public int height;
    public int width;
    private Image image;
    public static final int STYLE_PLAIN = 0;
    public static final int SIZE_SMALL = 8;
    boolean isLandscape;
    int amountOfLetters;
    int charHeight;
    int charWidth;
    int compensator;
    int gap;
    int widthBetweenPixelsCompensator;
    boolean offsetCorrectionRequired;
    //public static int maxTextWidth;
    int clipX;
    int clipY;
    int clipW;
    int clipH;

	boolean coutOn=true;
	private void cout(String outputme) {
        if (coutOn) {
            System.out.println(outputme);
        }
    }
}
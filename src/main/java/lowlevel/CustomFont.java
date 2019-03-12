package lowlevel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;

public class CustomFont
{

    public static CustomFont getFont(Image i, int inStyle, int inSize, boolean landscape, int compensator_,
                                     int amountOfLetters_, int widthBetweenPixelsCompensator_, int gap_,
                                     boolean offsetCorrectionRequired_) {
        System.out.println("font created.");
        return new CustomFont(i, inSize, inStyle, landscape, compensator_, amountOfLetters_,
            widthBetweenPixelsCompensator_, gap_, offsetCorrectionRequired_);
    }

    public int getCharWidth()
    {
        return charWidth;
    }

    public int getCharHeight()
    {
        return charHeight;
    }

    private CustomFont(Image inImage, int inStyle, int inSize, boolean landscape, int compensator_,
                       int amountOfLetters_, int widthBetweenPixelsCompensator_,
            int gap_, boolean offsetCorrectionRequired_)
    {
        isLandscape = false;
        charHeight = 0;
        charWidth = 0;
        compensator = 33;
        gap = 0;
        widthBetweenPixelsCompensator = 0;
        offsetCorrectionRequired = false;
        //maxTextWidth = 0;
        clipX = 0;
        clipY = 0;
        clipW = 0;
        clipH = 0;
        compensator = compensator_;
        isLandscape = landscape;
        amountOfLetters = amountOfLetters_;
        image = inImage;

        if (image==null)
        {
            HAL._E("customfont image is null!");
        }
        style = inStyle;
        size = inSize;
        widthBetweenPixelsCompensator = widthBetweenPixelsCompensator_;
        gap = gap_;
        offsetCorrectionRequired = offsetCorrectionRequired_;
        try
        {
            height = image.getHeight(CustomCanvas.this_)+2;
            charHeight = height;
            width = image.getWidth(CustomCanvas.this_) / amountOfLetters_;
           // Gooey.PrintTHIS("Strip width is " + image.getWidth() + " so each of the letters is " + width + " wide and height is " + height);
            charWidth = width - widthBetweenPixelsCompensator;
            HAL.log("charWidth:" + charWidth);
            baseline = calculateBaseline();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            throw new IllegalArgumentException("Specified font is invalid: " + t);
        }
    }

    //public void setMaxWidthOfParentContainer(int maximusWidthimus)
    //{
    //    maxTextWidth = maximusWidthimus;
    //}

    private int calculateBaseline()
    {
        return height;
    }

    public int charsWidth(char ch[], int offset, int length)
    {
        return length * charWidth;
    }

	public int charsWidth(byte ch[], int offset, int length)
    {
        return length * charWidth;
    }

    public int charWidth()
    {
        return charWidth;
    }
    public int getWidth()
    {
        return charWidth;
    }

    public int getBaselinePosition()
    {
        return baseline;
    }

    

    public int getHeight()
    {
        return height-1;
    }

    public int stringWidth(String str)
    {
        return str.length() * getWidth();//5;
        //return charsWidth(str.toCharArray(), 0, str.length());
    }

    public int substringWidth(String str, int offset, int len)
    {
        return charsWidth(str.toCharArray(), offset, len);
    }

    public int getSize()
    {
        return size;
    }

    public int getStyle()
    {
        return style;
    }

    public boolean isBold()
    {
        return (style & 1) != 0;
    }

    public boolean isItalic()
    {
        return (style & 2) != 0;
    }

    public boolean isPlain()
    {
        return style == 0;
    }

    public boolean isUnderlined()
    {
        return (style & 4) != 0;
    }

    public void drawChar(Graphics g1, char c, int i, int j, int k)
    {
    }
    Shape s;
    public void drawGDChars(Graphics g, byte data[], int offset, int x, int y, int anchor)
    {
		int length=data[offset];
		if (length<0)length+=256;
        if(isLandscape)
        {
            Graphics _tmp = g;
            if((anchor & 8) != 0)
            {
                x += height * length;
            } else
            {
                Graphics _tmp1 = g;
                if((anchor & 1) != 0)
                    x += (height * length) / 2;
            }
            Graphics _tmp2 = g;
            if((anchor & 0x20) != 0)
            {
                y -= height;
            } else
            {
                Graphics _tmp3 = g;
                if((anchor & 2) != 0)
                    y += height / 2;
            }
        } else
        {
            Graphics _tmp4 = g;
            if((anchor & 8) != 0)
            {
                x -= charsWidth(data, offset, length);
            } else
            {
                Graphics _tmp5 = g;
                if((anchor & 1) != 0)
                    x -= charsWidth(data, offset, length) / 2;
            }
            Graphics _tmp6 = g;
            if((anchor & 0x20) != 0)
            {
                y -= height;
            } else
            {
                Graphics _tmp7 = g;
                if((anchor & 2) != 0)
                    y -= height / 2;
            }
        }
       /* int clipX = g..getClipX();
        int clipY = g.getClipY();
        int clipW = g.getClipWidth();
        int clipH = g.getClipHeight();*/
        s = g.getClip();
        for(int i = 1; i <= length; i++)
        {
            char c = (char)data[offset + i];
            Graphics _tmp8 = g;
            Graphics _tmp9 = g;
            drawCharInternal(g, c, x, y, 0x10 | 4, i);
            /*if (c!=' ')
            {
                if(isLandscape)
                    x -= charHeight;
                else
                    x += charWidth;
            }*/
        }

    }

    public void drawChars(Graphics g, char data[], int offset, int length, int x, int y, int anchor)
    {
        if(isLandscape)
        {
            Graphics _tmp = g;
            if((anchor & 8) != 0)
            {
                x += height * length;
            } else
            {
                Graphics _tmp1 = g;
                if((anchor & 1) != 0)
                    x += (height * length) / 2;
            }
            Graphics _tmp2 = g;
            if((anchor & 0x20) != 0)
            {
                y -= height;
            } else
            {
                Graphics _tmp3 = g;
                if((anchor & 2) != 0)
                    y += height / 2;
            }
        } else
        {
            Graphics _tmp4 = g;
            if((anchor & 8) != 0)
            {
                x -= charsWidth(data, offset, length);
            } else
            {
                Graphics _tmp5 = g;
                if((anchor & 1) != 0)
                    x -= charsWidth(data, offset, length) / 2;
            }
            Graphics _tmp6 = g;
            if((anchor & 0x20) != 0)
            {
                y -= height;
            } else
            {
                Graphics _tmp7 = g;
                if((anchor & 2) != 0)
                    y -= height / 2;
            }
        }
/*        int clipX = g.getClipX();
        int clipY = g.getClipY();
        int clipW = g.getClipWidth();
        int clipH = g.getClipHeight();*/

        if (g.getClip()!=null)
        s=g.getClip();
        for(int i = 0; i < length; i++)
        {
            char c = data[offset + i];
            Graphics _tmp8 = g;
            Graphics _tmp9 = g;
            drawCharInternal(g, c, x, y, 0x10 | 4, i);
             if(isLandscape)
                    x -= charHeight;
                else
                    x += charWidth;
           /* if (c!=' ')
            {
                if(isLandscape)
                    x -= charHeight;
                else
                    x += charWidth;
            }
            else
            {
                // this is so we can reduce the gap a SPACE takes up.
                 if(isLandscape)
                    x -= (charHeight-2);
                else
                    x += (charHeight+2);
            }*/
        }

    }
	
	int availWidth=50000;// this is for limiting, if its 1000 we`re not limiting it now
	public void setAvailWidth(int availWidth_)
	{
		availWidth=availWidth_-2;
	}
	int availHeight=50000;
	public void setAvailHeight(int availHeight_)
	{
		//cout("avail height set as "+availHeight_);
		availHeight=availHeight_;//-2;
	}


    private void drawCharInternal(Graphics g, char character, int x, int y, int anchor, int charsAlong)
    {
/*        int clipX = g.getClipX();
        int clipY = g.getClipY();
        int clipW = g.getClipWidth();
        int clipH = g.getClipHeight();*/

        s = g.getClip();

        int comps = compensator;
        if(isLandscape)
        {
            if(character != ' ')
            {
               // g.drawRegion(image, width * (character - compensator), 0, width, height, 0, y, x - charsAlong * gap - height, anchor);
               g.drawImage(image, width * (character - compensator), 0, width, height, 0, y, x - charsAlong * gap - height, anchor,CustomCanvas.this_);
         
            }
        } else
        {
            if(character == ' '){return;}
			
			if ( x>availWidth || y>availHeight+15 )//15 buffer period //specific to SCROLL PROJECT
			{
				cout("out of clip for "+(char) (character - comps)+" clipW was "+clipW+" and x was: "+x);
			}
			else
			{

				g.setClip(x, y, width, height);
				
				/////////////////////if (Cell.CLIPPING)//specific to scroll, remove me for others.
				//////////////{
					//////////////if (special) // this is so that when we horizontally scroll the text we set up a special case for the clipping, otherwise, clipping is the cell.
					////////////{
					/////////////	g.clipRect(specialX,Cell.cellClipY,Cell.cellClipW,Cell.cellClipH); 
					/////////////
					////////////////else
					////////////////
					///////////////	g.clipRect(Cell.cellClipX,Cell.cellClipY,Cell.cellClipW,Cell.cellClipH); // this is the definative clipping so that nothing paints outside the cell
					//////////////}
				//////////////////}
				/*	if (Cell.CLIPPING)//specific to scroll, remove me for others.
				{
					if (special) // this is so that when we horizontally scroll the text we set up a special case for the clipping, otherwise, clipping is the cell.
					{
						g.clipRect(specialX,Cell.cellClipY,Cell.cellClipW,Cell.cellClipH); 
					}
					else
					{
						g.clipRect(Cell.cellClipX,Cell.cellClipY,Cell.cellClipW,Cell.cellClipH); // this is the definative clipping so that nothing paints outside the cell
					}
				}
				*/
					
				
				g.drawImage(image, x - width * (character - comps), y, CustomCanvas.this_);
				g.setClip(s);//clipX, clipY, clipW, clipH);
			}
            
        }
    }

	boolean special=false;
	int specialX=0;
	public void setSpecialClipping(int sX)
	{
		special=true;
		specialX=sX;
	}

    public void drawString(Graphics g, String str, int x, int y, int anchor)
    {
        drawChars(g, str.toCharArray(), 0, str.length(), x, y, anchor);
    }
    //draw a strign with a white background
    public void drawStringW(Graphics g, String str, int x, int y, int anchor)
    {
//        g.setColor(255,255,255);
        g.fillRect(x-1,y-getCharHeight()-1,stringWidth(str),getCharHeight());
        
        drawChars(g, str.toCharArray(), 0, str.length(), x, y, anchor);
    }
     //draw a strign with a black background
    public void drawStringB(Graphics g, String str, int x, int y, int anchor)
    {
       // int prevcol = g.getColor();
//        g.setColor(0,0,0);
        g.fillRect(x-2,y-getCharHeight()-1,stringWidth(str)+2,getCharHeight()+3);
        
        drawChars(g, str.toCharArray(), 0, str.length(), x, y, anchor);
        //g.setColor(prevcol);
    }
    

    public void drawSubstring(Graphics g, String str, int offset, int len, int x, int y, int anchor)
    {
        drawChars(g, str.toCharArray(), offset, len, x, y, anchor);
    }

    private int style;
    private int size;
    private int baseline;
    public int height;
    public int width;
    private Image image;
    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_UNDERLINED = 4;
    public static final int SIZE_SMALL = 8;
    public static final int SIZE_MEDIUM = 0;
    public static final int SIZE_LARGE = 16;
    public static final int FACE_SYSTEM = 0;
    public static final int FACE_MONOSPACE = 32;
    public static final int FACE_PROPORTIONAL = 64;
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
	  private void cout(String outputme)
	  {
		   if (coutOn)
		   {
			   System.out.println(outputme);
		   }
		   
	  }
}
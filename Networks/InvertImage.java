package Networks;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class InvertImage 
{
	private String name=null;
	private File file=null;
	private BufferedImage pix=null;		//原图像像素数组
	private BufferedImage npix=null;
	private double[][][] rgb=null;	//处理之后的像素数组
	//private double[][] g=null;	//处理之后的像素数组
	//private double[][] b=null;	//处理之后的像素数组
	private int width;
	private int height;
	private final int nw=64;	//样本目标宽度
	private final int nh=64;	//样本目标高度
	
	public InvertImage(String name)
	{
		this.name=name;
		file=new File(name);
		rgb=new double[3][nh][nw];
		getImage();
		transfer();
	}
	
	public void changeImage(String name)
	{
		this.name=name;
		file=new File(name);
		rgb=new double[3][nh][nw];
		getImage();
		transfer();
	}
	
	private BufferedImage getImage()
	{
		try{
			pix = ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		width = pix.getWidth(); // 像素
		height = pix.getHeight(); // 像素
		printPath();
		System.out.println("width=" + width + ",height=" + height + ".");
		return pix;
	}
	
	private void transfer()
	{
		double sw=nw*1.0/width;
		double sh=nh*1.0/height;
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(sw, sh), null);
		//Image image = pix.getScaledInstance(nw, nh, BufferedImage.SCALE_SMOOTH);
		//image=ato.filter(pix, null);
		npix=ato.filter(pix, null);
		int i,j,bit;
		for(i=0;i<nh;i++)
		{
			for(j=0;j<nw;j++)
			{
				bit=npix.getRGB(i, j);
				rgb[0][i][j]=(bit&0xff)/255.0;
				rgb[1][i][j]=((bit>>8)&0xff)/255.0;
				rgb[2][i][j]=((bit>>16)&0xff)/255.0;
			}
		}
		//CNNMath.printKernel(rgb[0]);
		/*转变为灰度图*/
		/*BufferedImage gray=new BufferedImage(nw, nh, BufferedImage.TYPE_BYTE_GRAY);
		for(i=0;i<nh;i++)
		{
			for(j=0;j<nw;j++)
			{
				gray.setRGB(j, i, npix.getRGB(j, i));
			}
		}
		npix=gray;*/
	}
	
	public double[][][] getRGBpix()
	{
		return rgb;
	}
	
	public int getlength()
	{
		return nw;
	}
	
	public void printPix()
	{
		int i,j,k;
		for(i=0;i<nh;i++)  
		{
			for(j=0;j<nw;j++)
			{
				System.out.printf("("+rgb[0][i][j]+","+rgb[1][i][j]+","+rgb[2][i][j]+")");
			}
			System.out.println();
		}
	}
	
	public BufferedImage getNewPix()
	{
		return npix;
	}
	
	public void writePixInBmp(String newname)
	{
		File newfile=new File(newname);
		try {
			ImageIO.write((RenderedImage) npix, "bmp", newfile);
		} catch (IOException e) {
			System.out.println("Gray write error");
			e.printStackTrace();
		}
	}
	
	public void printPath()
	{
		System.out.println(this.name);
	}
}

package Networks;
import java.awt.image.BufferedImage;

/**
 * 第一层对三通道输入图像(RGB)的三维卷积,单个神经元
 * @author Administrator
 *
 */
public class Filter3
{
	private final int step=1;
	private int lkernel=5;	//卷积核的大小
	private int lresult;	//结果的大小
	private double[][][] in;
	private double[][][] kernel;	//本层卷积核
	private double bias;	//本层偏置
	private double[][] result;
	private double[][][] kernelError;
	private double biasError;
	private InvertImage pix;
	
	public Filter3(InvertImage pix,int length,int kernelLength,double[][][] kernel,double bias)
	{
		this.pix=pix;
		this.lkernel=kernelLength;
		lresult=(length-lkernel)/step+1;
		result=new double[lresult][lresult];
		this.bias=bias;
		this.kernel=kernel;
	}
	
	/**
	 * 只要InvertImage对象不变，每次计算若InvertImage内的图片更新，此函数也会随之更新
	 * @return
	 */
	public double[][] filter3()
	{
		CNNMath.clearToZero(result);
		int i,j,x,y,k;
		in=pix.getRGBpix();
		//System.out.println("------------------R-----------------");
		//CNNMath.printKernel(in[0]);
		for(i=0;i<lresult;i++)
		{
			for(j=0;j<lresult;j++)
			{
				for(k=0;k<3;k++)
				{
					for(x=0;x<lkernel;x++)
					{
						for(y=0;y<lkernel;y++)
						{
							result[i][j]+=in[k][i+x][j+y]*this.kernel[k][x][y];
						}
					}
				}
				result[i][j]=CNNMath.sigmoid(result[i][j]+bias);	//sigmoid
			}
		}
		return result;
	}
	
	public void printR()
	{
		CNNMath.printKernel(in[0]);
	}
	
	public int resultLength()
	{
		return lresult;
	}
	
	public double getBias()
	{
		return bias;
	}
	
	/**
	 * 更新本神经元的权值和偏置值
	 * @param error 本层的误差
	 */
	public void updateWB(double[][] error)
	{
		if(kernelError==null)
			kernelError=new double[kernel.length][kernel[0].length][kernel[0][0].length];
		int i,j,k;
		for(k=0;k<in.length;k++)
		{
			kernelError[k]=CNNMath.mul(in[k], error);
		}
		for(k=0;k<kernel.length;k++)
		{
			for(i=0;i<kernel[k].length;i++)
			{
				for(j=0;j<kernel[k][i].length;j++)
				{
					kernel[k][i][j]+=CNNMath.studySpeed*kernelError[k][i][j];
				}
			}
		}
		biasError=CNNMath.sum(error);
		bias+=CNNMath.studySpeed*biasError;
	}
	
	public void writeWBInFile(SaveWB s)
	{
		s.writeWB3(kernel, bias);
	}
}

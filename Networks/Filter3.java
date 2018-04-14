package Networks;
import java.awt.image.BufferedImage;

/**
 * ��һ�����ͨ������ͼ��(RGB)����ά���,������Ԫ
 * @author Administrator
 *
 */
public class Filter3
{
	private final int step=1;
	private int lkernel=5;	//����˵Ĵ�С
	private int lresult;	//����Ĵ�С
	private double[][][] in;
	private double[][][] kernel;	//��������
	private double bias;	//����ƫ��
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
	 * ֻҪInvertImage���󲻱䣬ÿ�μ�����InvertImage�ڵ�ͼƬ���£��˺���Ҳ����֮����
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
	 * ���±���Ԫ��Ȩֵ��ƫ��ֵ
	 * @param error ��������
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

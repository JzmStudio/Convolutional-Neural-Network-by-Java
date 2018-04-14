package Networks;
/**
 * ����Ϊ�����������(����)��Ԫ
 * @author Administrator
 *
 */
public class Filter {
	private final int step=1;	//����
	private int lkernel=5;	//����˵Ĵ�С
	private int lresult;	//����Ĵ�С
	private double[][] kernel;	//����ľ����
	private double bias;
	private double[][] result;
	private double[][][] in;
	private double[][] outError=null;	//����Ԫ��������
	private double[][] kernelError=null;	//�����Ȩֵ�����
	private double biasError;
	private double[][][] backError;
	private int index=0;
	int inputSum;	//����Ķ�άͼ����

	public Filter(int inputSum,int inputLength,int kernelLength,double[][] kernel,double bias)
	{
		this.inputSum=inputSum;
		in=new double[inputSum][][];
		backError=new double[inputSum][][];
		this.lkernel=kernelLength;
		lresult=(inputLength-lkernel)/step+1;
		result=new double[lresult][lresult];
		this.bias=bias;
		this.kernel=kernel;
	}
	
	/**
	 * 
	 * @param input ��Ϊ����Ķ�άͼ
	 * @param error ��Ϊ����ʱ�����
	 */
	public void addInput(double[][] input,double[][] error)
	{
		if(index==inputSum)
		{
			System.out.println("Add overflow");
			return;
		}
		in[index]=input;
		backError[index]=error;
		index++;
	}
	
	public double[][] filter()
	{
		CNNMath.clearToZero(result);	//result reused;
		if(index!=inputSum)
		{
			System.out.println("Please add first");
			return null;
		}
		int i,j,x,y,k;
		for(i=0;i<lresult;i++)
		{
			for(j=0;j<lresult;j++)
			{
				for(k=0;k<inputSum;k++)
				{
					for(x=0;x<lkernel;x++)
					{
						for(y=0;y<lkernel;y++)
						{
							result[i][j]+=in[k][i+x][j+y]*kernel[x][y];
						}
					}
				}
				result[i][j]=CNNMath.sigmoid(result[i][j]+bias);	//sigmoid
			}
		}
		return result;
	}
	
	public int resultLength()
	{
		return lresult;
	}
	
	public double[][] getKernel()
	{
		return kernel;
	}
	
	public double getBias()
	{
		return bias;
	}
	
	/**
	 * ���¸���Ԫ��Ӧ����˵�Ȩֵ�������ر���Ԫ��������
	 * @param e4 ǰ��ػ�������
	 * @return �������
	 */
	public double[][] updateWB(double[][] e4)
	{
		if(outError==null)
			outError=new double[e4.length*2][e4[0].length*2];
		if(kernelError==null)
			kernelError=new double[kernel.length][kernel[0].length];
		int i,j,k;
		double unsam;
		/*unsample*/
		for(i=0;i<e4.length;i++)
		{
			for(j=0;j<e4[i].length;j++)
			{
				unsam=e4[i][j]/4;
				outError[i*2][j*2]=unsam*result[i*2][j*2]*(1-result[i*2][j*2]);
				outError[i*2+1][j*2]=unsam*result[i*2+1][j*2]*(1-result[i*2+1][j*2]);
				outError[i*2][j*2+1]=unsam*result[i*2][j*2+1]*(1-result[i*2][j*2+1]);
				outError[i*2+1][j*2+1]=unsam*result[i*2+1][j*2+1]*(1-result[i*2+1][j*2+1]);
			}
		}
		double[][][] ek=new double[in.length][][];
		for(k=0;k<in.length;k++)
		{
			ek[k]=CNNMath.mul(in[k], outError);
		}
		double[][] sum=new double[kernel.length][kernel[0].length];
		for(k=0;k<in.length;k++)
		{
			for(i=0;i<kernel.length;i++)
			{
				for(j=0;j<kernel[i].length;j++)
				{
					sum[i][j]+=ek[k][i][j];
				}
			}
		}
		kernelError=sum;
		//System.out.println("************3k**************");
		//CNNMath.printKernel(kernelError);	//
		for(i=0;i<kernel.length;i++)
		{
			for(j=0;j<kernel[i].length;j++)
			{
				kernel[i][j]+=CNNMath.studySpeed*sum[i][j];
			}
		}
		biasError=CNNMath.sum(e4);
		bias+=CNNMath.studySpeed*biasError;
		return outError;
	}
	
	/**
	 * ���º���ػ�������(���������񾭽ڵ�ֱ���и��¼��㣬������ǰ���񾭽ڵ���)
	 * @return
	 */
	public void updateBackError()
	{
		int i,j,k;
		//double[][][] e2=new double[backError.length][][];
		double[][] e2=CNNMath.cov2(CNNMath.full(outError, kernel.length-1), kernel);
		//��ÿһ���������ͼk
		for(k=0;k<backError.length;k++)
		{
			for(i=0;i<backError[k].length;i++)
			{
				for(j=0;j<backError[k][i].length;j++)
				{
					backError[k][i][j]+=e2[i][j]*in[k][i][j]*(1-in[k][i][j]);
				}
			}
		}
		//backError[0][0][0]=0.01f;
	}
	
	/**
	 * ǰ��Ϊ�ػ��㣬���㷵�ر����������
	 * @param errorInPool �ػ������
	 * @param out ����Գػ��������
	 * @return
	 */
	public static double[][][] error(double[][][] errorInPool,double[][][] out)
	{
		double[][][] e=new double[errorInPool.length][errorInPool[0].length*2][errorInPool[0][0].length*2];
		int i,j,k;
		double unsam;
		for(k=0;k<errorInPool.length;k++)
		{
			for(i=0;i<errorInPool[k].length;i++)
			{
				for(j=0;j<errorInPool[k][i].length;j++)
				{
					unsam=errorInPool[k][i][j]/4;
					e[k][i*2][j*2]=unsam*out[k][i*2][j*2]*(1-out[k][i*2][j*2]);
					e[k][i*2+1][j*2]=unsam*out[k][i*2+1][j*2]*(1-out[k][i*2+1][j*2]);
					e[k][i*2][j*2+1]=unsam*out[k][i*2][j*2+1]*(1-out[k][i*2][j*2+1]);
					e[k][i*2+1][j*2+1]=unsam*out[k][i*2+1][j*2+1]*(1-out[k][i*2+1][j*2+1]);
				}
			}
		}
		return e;
	}
	
	public void writeWBInFile(SaveWB s)
	{
		s.writeWB2(kernel, bias);
	}
}

package Networks;
/**
 * 全连接层的(单个)神经元
 * @author Administrator
 *
 */
public class FullJoin {
	private double result;
	private double[][][] in;
	private double[][][] weight;
	private double bias;
	private int inlen;
	private int kernelNum;
	private double error;

	/**
	 * 
	 * @param in 对该神经元的输入
	 * @param length 输入的二维图长度
	 * @param kernelNum	输出的二维图数量
	 */
	public FullJoin(int length,int kernelNum,double[][][] filter,double bias)
	{
		this.inlen=length;
		this.kernelNum=kernelNum;
		this.weight=filter;
		this.bias=bias;
	}
	
	public double CalculateFullJoin(double[][][] in)
	{
		this.in=in;
		int i,j,k;
		result=0;
		for(i=0;i<kernelNum;i++)
		{
			for(j=0;j<inlen;j++)
			{
				for(k=0;k<inlen;k++)
				{
					result+=weight[i][j][k]*in[i][j][k];
				}
			}
		}
		result=CNNMath.sigmoid(result+bias);
		return result;	//sigmoid
	}
	
	public double[][][] getWeight()
	{
		return CNNMath.copy(weight);
	}
	
	public double getBias()
	{
		return bias;
	}
	
	public double updateWB(double target)
	{
		error=(target-result)*result*(1-result);
		//System.out.println("Result is "+result+",Full Join Error is "+error);
		int i,j,k;
		for(k=0;k<weight.length;k++)
		{
			for(i=0;i<weight[k].length;i++)
			{
				for(j=0;j<weight[k][i].length;j++)
				{
					weight[k][i][j]+=CNNMath.studySpeed*error*in[k][i][j];
				}
			}
		}
		bias+=CNNMath.studySpeed*error;
		//System.out.println("Bias change: "+CNNMath.studySpeed*error+" Weight change: "+CNNMath.studySpeed*error*in[0][0][0]);
		return error;
	}
	
	public void writeWBInFile(SaveWB s)
	{
		s.writeWB3(weight, bias);
	}
}
